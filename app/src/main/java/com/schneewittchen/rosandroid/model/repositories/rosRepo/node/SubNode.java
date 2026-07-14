package com.schneewittchen.rosandroid.model.repositories.rosRepo.node;

import android.util.Log;

import com.google.gson.JsonObject;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.connection.RosbridgeClient;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.MessageGson;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.MessageRegistry;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.RawMessage;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.RosData;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;

/**
 * Node subscribing to a specific topic over the rosbridge connection (ROS 2).
 * Received JSON messages are converted into their typed message classes if
 * the type is known, otherwise a raw message carrying only the JSON is
 * delivered (e.g. for the debug and plot widgets which accept any type).
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 16.09.20
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public class SubNode extends AbstractNode {

    private final NodeListener listener;

    public SubNode(NodeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onConnected(RosbridgeClient client) {
        super.onConnected(client);

        if (this.widget != null) {
            this.widget.validMessage = true;
        }

        String type = topic.type;
        if (Topic.WILDCARD_TYPE.equals(type)) {
            // Let rosbridge infer the type of the topic.
            type = null;
        } else {
            type = MessageRegistry.toRos2Type(type);
        }

        client.subscribe(topic.name, type);
    }

    @Override
    public void onDisconnected() {
        if (client != null && client.isConnected()) {
            client.unsubscribe(topic.name);
        }

        super.onDisconnected();
    }

    /**
     * Handle an incoming message on the subscribed topic.
     *
     * @param json rosbridge JSON representation of the message
     */
    public void onNewMessage(JsonObject json) {
        Message message;

        try {
            Class<? extends Message> messageClass = MessageRegistry.get(topic.type);

            if (messageClass != null) {
                message = MessageGson.get().fromJson(json, messageClass);
            } else {
                // Unknown type: deliver the raw JSON only. Flag typed widgets
                // (non-wildcard subscriptions) as invalid so the UI can show it.
                if (widget != null && !Topic.WILDCARD_TYPE.equals(topic.type)) {
                    widget.validMessage = false;
                }
                message = new RawMessage();
            }

            message.setRawJson(json);

        } catch (Exception e) {
            Log.e(TAG, "Failed to parse message on topic " + topic.name, e);

            if (this.widget != null) {
                this.widget.validMessage = false;
            }
            return;
        }

        lastRosData = new RosData(topic, message);
        listener.onNewMessage(lastRosData);
    }

    public interface NodeListener {
        void onNewMessage(RosData message);
    }
}
