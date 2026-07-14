package com.schneewittchen.rosandroid.model.repositories.rosRepo.node;

import com.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;

/**
 * Base class of widget data which can be converted into a ROS 2 message
 * for publishing over the rosbridge connection.
 */
public abstract class BaseData {

    protected Topic topic;

    public Topic getTopic() {
        return this.topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * Convert this data into a publishable ROS message.
     *
     * @param widget widget entity the data belongs to
     * @return ROS message or null if the data cannot be converted
     */
    public Message toRosMessage(BaseEntity widget) {
        return null;
    }
}
