package com.schneewittchen.rosandroid.model.repositories.rosRepo.connection;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.MessageGson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * WebSocket client implementing the rosbridge v2 protocol
 * (https://github.com/RobotWebTools/rosbridge_suite), which is the standard
 * way to communicate with a ROS 2 system from devices that cannot run a DDS
 * middleware themselves.
 * <p>
 * On the robot side a rosbridge server has to be running, e.g.:
 * ros2 launch rosbridge_server rosbridge_websocket_launch.xml
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 12.07.2026 (ROS 2 migration)
 */
public class RosbridgeClient extends WebSocketListener {

    private static final String TAG = RosbridgeClient.class.getSimpleName();
    private static final long SERVICE_CALL_TIMEOUT_MS = 2500;

    /**
     * Shared across all client instances so reconnects do not accumulate
     * OkHttp thread pools.
     */
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .pingInterval(10, TimeUnit.SECONDS)
            .build();

    private final ClientListener listener;
    private final Map<String, PendingServiceCall> pendingServiceCalls;
    private final AtomicLong idCounter;

    private WebSocket webSocket;
    private volatile boolean connected;

    public RosbridgeClient(ClientListener listener) {
        this.listener = listener;
        this.pendingServiceCalls = new ConcurrentHashMap<>();
        this.idCounter = new AtomicLong(0);
    }


    /**
     * Open the connection to a rosbridge server.
     *
     * @param uri WebSocket uri, e.g. 'ws://192.168.0.100:9090'
     */
    public void connect(String uri) {
        Request request = new Request.Builder()
                .url(uri)
                .build();

        webSocket = HTTP_CLIENT.newWebSocket(request, this);
    }

    /**
     * Close the connection to the rosbridge server.
     */
    public void disconnect() {
        connected = false;

        if (webSocket != null) {
            webSocket.close(1000, "Closed by user");
            webSocket = null;
        }
    }

    public boolean isConnected() {
        return connected;
    }


    // Rosbridge operations ---------------------------------------------------

    public void advertise(String topic, String type) {
        JsonObject op = newOperation("advertise", topic);
        op.addProperty("type", type);
        send(op);
    }

    public void unadvertise(String topic) {
        send(newOperation("unadvertise", topic));
    }

    public void publish(String topic, JsonElement message) {
        JsonObject op = newOperation("publish", topic);
        op.add("msg", message);
        send(op);
    }

    public void subscribe(String topic, String type) {
        JsonObject op = newOperation("subscribe", topic);
        if (type != null && !type.isEmpty()) {
            op.addProperty("type", type);
        }
        send(op);
    }

    public void unsubscribe(String topic) {
        send(newOperation("unsubscribe", topic));
    }

    /**
     * Call a ROS service and wait for its response.
     *
     * @param service service name, e.g. '/rosapi/topics'
     * @param args    service arguments, may be null
     * @return the 'values' object of the response, or null on error/timeout
     */
    public JsonObject callServiceBlocking(String service, JsonObject args) {
        if (!connected) {
            return null;
        }

        String id = "call_service:" + service + ":" + idCounter.incrementAndGet();
        PendingServiceCall pendingCall = new PendingServiceCall();
        pendingServiceCalls.put(id, pendingCall);

        JsonObject op = new JsonObject();
        op.addProperty("op", "call_service");
        op.addProperty("id", id);
        op.addProperty("service", service);
        if (args != null) {
            op.add("args", args);
        }
        send(op);

        try {
            if (pendingCall.latch.await(SERVICE_CALL_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                return pendingCall.values;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            pendingServiceCalls.remove(id);
        }

        Log.w(TAG, "Service call timed out: " + service);
        return null;
    }


    private JsonObject newOperation(String op, String topic) {
        JsonObject json = new JsonObject();
        json.addProperty("op", op);
        json.addProperty("id", op + ":" + topic + ":" + idCounter.incrementAndGet());
        json.addProperty("topic", topic);
        return json;
    }

    private synchronized void send(JsonObject json) {
        WebSocket socket = this.webSocket;
        if (socket == null) {
            Log.w(TAG, "Cannot send, not connected: " + json.get("op"));
            return;
        }

        socket.send(MessageGson.get().toJson(json));
    }


    // WebSocketListener ------------------------------------------------------

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i(TAG, "Connected to rosbridge server");
        connected = true;
        listener.onConnected();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            String op = json.has("op") ? json.get("op").getAsString() : "";

            switch (op) {
                case "publish":
                    String topic = json.get("topic").getAsString();
                    JsonObject msg = json.getAsJsonObject("msg");
                    listener.onNewMessage(topic, msg);
                    break;

                case "service_response":
                    handleServiceResponse(json);
                    break;

                case "status":
                    Log.i(TAG, "Rosbridge status: " + text);
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to handle incoming message", e);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.i(TAG, "Disconnected from rosbridge server: " + reason);
        connected = false;
        listener.onDisconnected();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Connection to rosbridge server failed", t);
        boolean wasConnected = connected;
        connected = false;

        if (wasConnected) {
            listener.onDisconnected();
        } else {
            listener.onConnectionFailed();
        }
    }

    private void handleServiceResponse(JsonObject json) {
        if (!json.has("id")) {
            return;
        }

        PendingServiceCall pendingCall = pendingServiceCalls.remove(json.get("id").getAsString());
        if (pendingCall == null) {
            return;
        }

        boolean result = !json.has("result") || json.get("result").getAsBoolean();
        if (result && json.has("values") && json.get("values").isJsonObject()) {
            pendingCall.values = json.getAsJsonObject("values");
        }

        pendingCall.latch.countDown();
    }


    private static class PendingServiceCall {
        final CountDownLatch latch = new CountDownLatch(1);
        volatile JsonObject values;
    }


    /**
     * Callbacks of the rosbridge connection. All methods are invoked on
     * background threads.
     */
    public interface ClientListener {

        void onConnected();

        void onDisconnected();

        void onConnectionFailed();

        void onNewMessage(String topic, JsonObject message);
    }
}
