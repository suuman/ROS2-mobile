package com.schneewittchen.rosandroid.model.repositories.rosRepo.message;

/**
 * Container for a received ROS message and its topic.
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 21.09.20
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public class RosData {

    private final Topic topic;
    private final Message message;


    public RosData(Topic topic, Message message) {
        this.topic = topic;
        this.message = message;
    }


    public Topic getTopic() {
        return this.topic;
    }

    public Message getMessage() {
        return this.message;
    }
}
