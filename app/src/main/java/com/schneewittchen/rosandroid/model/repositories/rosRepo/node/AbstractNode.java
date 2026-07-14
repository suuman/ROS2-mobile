package com.schneewittchen.rosandroid.model.repositories.rosRepo.node;

import com.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.connection.RosbridgeClient;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.RosData;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;

/**
 * Base class of the publisher and subscriber nodes. A node is bound to a
 * topic and forwards its data over the rosbridge connection (ROS 2).
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 15.09.20
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public abstract class AbstractNode {

    public static final String TAG = AbstractNode.class.getSimpleName();

    protected Topic topic;
    protected BaseEntity widget;
    protected RosData lastRosData;
    protected RosbridgeClient client;


    /**
     * Called as soon as a rosbridge connection is available. Implementations
     * have to register themselves (advertise/subscribe) on the given client.
     *
     * @param client Connected rosbridge client
     */
    public void onConnected(RosbridgeClient client) {
        this.client = client;
    }

    /**
     * Called when the node gets unregistered or the connection is lost.
     * Implementations have to clean up (unadvertise/unsubscribe).
     */
    public void onDisconnected() {
        this.client = null;
    }


    public Topic getTopic() {
        return this.topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public BaseEntity getWidget() {
        return this.widget;
    }

    public void setWidget(BaseEntity widget) {
        this.widget = widget;
        this.setTopic(widget.topic);
    }

    public RosData getLastRosData() {
        return lastRosData;
    }
}
