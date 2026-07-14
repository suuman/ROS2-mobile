package com.schneewittchen.rosandroid.model.repositories.rosRepo;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.schneewittchen.rosandroid.model.entities.MasterEntity;
import com.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import com.schneewittchen.rosandroid.model.entities.widgets.GroupEntity;
import com.schneewittchen.rosandroid.model.entities.widgets.IPublisherEntity;
import com.schneewittchen.rosandroid.model.entities.widgets.ISilentEntity;
import com.schneewittchen.rosandroid.model.entities.widgets.ISubscriberEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.connection.ConnectionCheckTask;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.connection.ConnectionListener;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.connection.ConnectionType;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.connection.RosbridgeClient;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.RosData;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.AbstractNode;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.BaseData;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.PubNode;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.SubNode;

import org.ros.rosjava_geometry.FrameTransformTree;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import geometry_msgs.TransformStamped;
import tf2_msgs.TFMessage;


/**
 * The ROS repository is responsible for connecting to a ROS 2 system via the
 * rosbridge protocol and for creating nodes depending on the respective
 * widgets. On the robot side a rosbridge server has to be running, e.g.:
 * ros2 launch rosbridge_server rosbridge_websocket_launch.xml
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 16.01.20
 * @updated on 12.07.2026 (migration from rosjava/ROS 1 to rosbridge/ROS 2)
 */
public class RosRepository implements SubNode.NodeListener {

    private static final String TAG = RosRepository.class.getSimpleName();
    private static RosRepository instance;

    private final WeakReference<Context> contextReference;
    private final List<BaseEntity> currentWidgets;
    private final HashMap<Topic, AbstractNode> currentNodes;
    private final MutableLiveData<ConnectionType> rosConnected;
    private final MutableLiveData<RosData> receivedData;
    private final FrameTransformTree frameTransformTree;
    private MasterEntity master;
    private RosbridgeClient client;
    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock wakeLock;

    /**
     * Default private constructor. Initialize empty lists and maps of intern widgets and nodes.
     */
    private RosRepository(Context context) {
        this.contextReference = new WeakReference<>(context);
        this.currentWidgets = new ArrayList<>();
        this.currentNodes = new HashMap<>();
        this.rosConnected = new MutableLiveData<>(ConnectionType.DISCONNECTED);
        this.receivedData = new MutableLiveData<>();
        this.frameTransformTree = TransformProvider.getInstance().getTree();

        this.initStaticNodes();
    }


    /**
     * Return the singleton instance of the repository.
     *
     * @return Instance of this Repository
     */
    public static RosRepository getInstance(final Context context) {
        if (instance == null) {
            instance = new RosRepository(context);
        }

        return instance;
    }


    /**
     * Initialize static nodes eg. tf and tf_static.
     */
    private void initStaticNodes() {
        Topic tfTopic = new Topic("/tf", TFMessage._TYPE);
        SubNode tfNode = new SubNode(this);
        tfNode.setTopic(tfTopic);
        currentNodes.put(tfTopic, tfNode);

        Topic tfStaticTopic = new Topic("/tf_static", TFMessage._TYPE);
        SubNode tfStaticNode = new SubNode(this);
        tfStaticNode.setTopic(tfStaticTopic);
        currentNodes.put(tfStaticTopic, tfStaticNode);
    }


    @Override
    public void onNewMessage(RosData message) {
        // Save transforms from tf messages
        if (message.getMessage() instanceof TFMessage) {
            TFMessage tf = (TFMessage) message.getMessage();

            for (TransformStamped transform : tf.getTransforms()) {
                frameTransformTree.update(transform);
            }
        }

        this.receivedData.postValue(message);
    }

    /**
     * Find the associated node and inform it about the changed data.
     *
     * @param data Widget data that has changed
     */
    public void publishData(BaseData data) {
        AbstractNode node = currentNodes.get(data.getTopic());

        if (node instanceof PubNode) {
            ((PubNode) node).setData(data);
        }
    }

    /**
     * Connect all registered nodes and establish a connection to the
     * rosbridge server with the connection details given by the already
     * delivered master entity.
     */
    public void connectToMaster() {
        Log.i(TAG, "Connect to rosbridge server");

        ConnectionType connectionType = rosConnected.getValue();
        if (connectionType == ConnectionType.CONNECTED || connectionType == ConnectionType.PENDING) {
            return;
        }

        if (master == null) {
            rosConnected.setValue(ConnectionType.FAILED);
            return;
        }

        rosConnected.setValue(ConnectionType.PENDING);

        // Check reachability of the host before opening the websocket
        new ConnectionCheckTask(new ConnectionListener() {

            @Override
            public void onSuccess() {
                ClientCallbacks callbacks = new ClientCallbacks();
                RosbridgeClient newClient = new RosbridgeClient(callbacks);
                callbacks.owner = newClient;
                client = newClient;
                newClient.connect(getMasterURI());
            }

            @Override
            public void onFailed() {
                rosConnected.postValue(ConnectionType.FAILED);
            }
        }).execute(master);
    }

    /**
     * Disconnect all running nodes and cut the connection to the rosbridge server.
     */
    public void disconnectFromMaster() {
        Log.i(TAG, "Disconnect from rosbridge server");
        if (client == null) {
            return;
        }

        this.unregisterAllNodes();
        client.disconnect();
        // Detach the client so late callbacks of the closing socket are ignored.
        client = null;
        releaseLocks();
        rosConnected.postValue(ConnectionType.DISCONNECTED);
    }


    /**
     * Change the connection details to the rosbridge server like the IP or port.
     *
     * @param master Master data
     */
    public void updateMaster(MasterEntity master) {
        Log.i(TAG, "Update Master");

        if (master == null) {
            Log.i(TAG, "Master is null");
            return;
        }

        this.master = master;
    }

    /**
     * Set the device IP. Only kept for compatibility with the master UI:
     * with rosbridge the device does not have to expose its own IP to the
     * ROS network anymore (no XML-RPC callbacks like in ROS 1).
     */
    public void setMasterDeviceIp(String deviceIp) {
        // Not required for the rosbridge (ROS 2) connection.
    }


    /**
     * React on a widget change. If at least one widget is added, deleted or changed this method
     * should be called.
     *
     * @param newWidgets Current list of widgets
     */
    public void updateWidgets(List<BaseEntity> newWidgets) {
        Log.i(TAG, "Update widgets");

        // Unpack widgets as a widget can contain child widgets
        List<BaseEntity> newEntities = new ArrayList<>();
        for (BaseEntity baseEntity : newWidgets) {
            if (baseEntity instanceof GroupEntity) {
                newEntities.addAll(baseEntity.childEntities);
            } else {
                newEntities.add(baseEntity);
            }
        }

        // Compare old and new widget lists
        // Create widget check with ids
        HashMap<Long, Boolean> widgetCheckMap = new HashMap<>();
        HashMap<Long, BaseEntity> widgetEntryMap = new HashMap<>();

        for (BaseEntity oldWidget : currentWidgets) {
            widgetCheckMap.put(oldWidget.id, false);
            widgetEntryMap.put(oldWidget.id, oldWidget);
        }

        for (BaseEntity newWidget : newEntities) {
            if (widgetCheckMap.containsKey(newWidget.id)) {
                // Node included in old and new list

                widgetCheckMap.put(newWidget.id, true);

                // Check if widget has changed
                BaseEntity oldWidget = widgetEntryMap.get(newWidget.id);
                updateNode(oldWidget, newWidget);

            } else {
                // Node not included in old list
                addNode(newWidget);
            }
        }

        // Delete unused widgets
        for (Long id : widgetCheckMap.keySet()) {
            if (!widgetCheckMap.get(id)) {
                // Node not included in new list
                removeNode(widgetEntryMap.get(id));
            }
        }

        this.currentWidgets.clear();
        this.currentWidgets.addAll(newEntities);
    }

    /**
     * Get the current connection status of the ROS service as a live data.
     *
     * @return Connection status
     */
    public LiveData<ConnectionType> getRosConnectionStatus() {
        return rosConnected;
    }


    // RosbridgeClient callbacks ----------------------------------------------

    /**
     * Client listener bound to a specific {@link RosbridgeClient} instance.
     * Callbacks of a client that is no longer the active one (e.g. a late
     * close event of an old socket after a reconnect) are ignored, so they
     * cannot overwrite the state of a newer connection.
     */
    private final class ClientCallbacks implements RosbridgeClient.ClientListener {

        RosbridgeClient owner;

        private boolean isStale() {
            return owner != client;
        }

        @Override
        public void onConnected() {
            if (isStale()) return;

            acquireLocks();
            rosConnected.postValue(ConnectionType.CONNECTED);
            registerAllNodes();
        }

        @Override
        public void onDisconnected() {
            if (isStale()) return;

            unregisterAllNodes();
            client = null;
            releaseLocks();
            rosConnected.postValue(ConnectionType.DISCONNECTED);
        }

        @Override
        public void onConnectionFailed() {
            if (isStale()) return;

            unregisterAllNodes();
            client = null;
            releaseLocks();
            rosConnected.postValue(ConnectionType.FAILED);
        }

        @Override
        public void onNewMessage(String topicName, JsonObject message) {
            if (isStale()) return;

            synchronized (currentNodes) {
                for (AbstractNode node : currentNodes.values()) {
                    if (node instanceof SubNode && node.getTopic() != null
                            && topicName.equals(node.getTopic().name)) {
                        ((SubNode) node).onNewMessage(message);
                    }
                }
            }
        }
    }


    /**
     * Keep wifi and cpu active while a connection is running, like the
     * former ROS 1 node service did. Otherwise the connection is dropped by
     * the wifi power save mode as soon as the screen turns off.
     */
    private void acquireLocks() {
        Context context = contextReference.get();
        if (context == null) {
            return;
        }

        try {
            if (wakeLock == null) {
                PowerManager powerManager =
                        (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "ROS-Mobile:RosConnection");
            }
            if (!wakeLock.isHeld()) {
                wakeLock.acquire();
            }

            if (wifiLock == null) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                        "ROS-Mobile:RosConnection");
            }
            if (!wifiLock.isHeld()) {
                wifiLock.acquire();
            }

        } catch (Exception e) {
            Log.w(TAG, "Failed to acquire wifi/wake lock", e);
        }
    }

    private void releaseLocks() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }


    /**
     * Create a node for a specific widget entity.
     * The node will be created and subsequently registered.
     *
     * @param widget Widget to be added
     */
    private AbstractNode addNode(BaseEntity widget) {
        if (widget instanceof ISilentEntity) return null;
        Log.i(TAG, "Add node: " + widget.name);

        // Create a new node from widget
        AbstractNode node;
        if (widget instanceof IPublisherEntity) {
            node = new PubNode();

        } else if (widget instanceof ISubscriberEntity) {
            node = new SubNode(this);

        } else {
            Log.i(TAG, "Widget is either publisher nor subscriber.");
            return null;
        }

        // Set node topic, add to node list and register it
        node.setWidget(widget);
        synchronized (currentNodes) {
            currentNodes.put(node.getTopic(), node);
        }
        this.registerNode(node);

        return node;
    }


    /**
     * Update a widget and its associated Node by ID in the ROS graph.
     *
     * @param oldWidget Old version of the widget
     * @param widget    Widget to update
     */
    private void updateNode(BaseEntity oldWidget, BaseEntity widget) {
        if (widget instanceof ISilentEntity) return;
        Log.i(TAG, "Update Node: " + oldWidget.name);

        if (oldWidget.equalRosState(widget)) {
            AbstractNode node = this.currentNodes.get(widget.topic);
            if (node == null) {
                addNode(widget);
                return;
            }

            node.setWidget(widget);

        } else {
            this.removeNode(oldWidget);
            this.addNode(widget);
        }
    }

    /**
     * Remove a widget and its associated Node in the ROS graph.
     *
     * @param widget Widget to remove
     */
    private void removeNode(BaseEntity widget) {
        if (widget instanceof ISilentEntity) return;
        Log.i(TAG, "Remove Node: " + widget.name);

        AbstractNode node;
        synchronized (currentNodes) {
            node = this.currentNodes.remove(widget.topic);
        }
        this.unregisterNode(node);
    }

    /**
     * Connect the node to the ROS graph if a rosbridge connection is running.
     *
     * @param node Node to connect
     */
    private void registerNode(AbstractNode node) {
        Log.i(TAG, "Register Node: " + node.getTopic().name);

        if (client == null || !client.isConnected()) {
            Log.w(TAG, "Not connected to a rosbridge server");
            return;
        }

        node.onConnected(client);
    }

    /**
     * Disconnect the node from the ROS graph.
     *
     * @param node Node to disconnect
     */
    private void unregisterNode(AbstractNode node) {
        if (node == null) return;

        Log.i(TAG, "Unregister Node: " + node.getTopic().name);

        node.onDisconnected();
    }

    private void registerAllNodes() {
        synchronized (currentNodes) {
            for (AbstractNode node : currentNodes.values()) {
                this.registerNode(node);
            }
        }
    }

    private void unregisterAllNodes() {
        synchronized (currentNodes) {
            for (AbstractNode node : currentNodes.values()) {
                this.unregisterNode(node);
            }
        }
    }

    private String getMasterURI() {
        return String.format("ws://%s:%s", master.ip, master.port);
    }

    public LiveData<RosData> getData() {
        return receivedData;
    }

    public HashMap<Topic, AbstractNode> getLastRosData() {
        return currentNodes;
    }

    /**
     * Get a list with all available topics from the ROS 2 system by calling
     * the rosapi topics service. Requires the rosapi node, which is included
     * in the default rosbridge server launch file.
     *
     * @return Topic list
     */
    public List<Topic> getTopicList() {
        ArrayList<Topic> topicList = new ArrayList<>();
        if (client == null || !client.isConnected()) {
            return topicList;
        }

        JsonObject values = client.callServiceBlocking("/rosapi/topics", null);
        if (values == null) {
            return topicList;
        }

        JsonArray topics = values.getAsJsonArray("topics");
        JsonArray types = values.getAsJsonArray("types");
        if (topics == null) {
            return topicList;
        }

        for (int i = 0; i < topics.size(); i++) {
            String name = topics.get(i).getAsString();
            String type = (types != null && i < types.size()) ? types.get(i).getAsString() : "";

            topicList.add(new Topic(name, type));
        }

        return topicList;
    }
}
