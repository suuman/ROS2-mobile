package com.schneewittchen.rosandroid.widgets.debug;

import com.schneewittchen.rosandroid.model.entities.widgets.SubscriberWidgetEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;


/**
 * Debug widget entity. Subscribes to a topic of any message type.
 *
 * @author Nils Rottmann
 * @version 2.0.0
 * @created on 17.08.20
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public class DebugEntity extends SubscriberWidgetEntity {

    public int numberMessages;


    public DebugEntity() {
        this.width = 4;
        this.height = 3;
        this.topic = new Topic("MessageToDebug", Topic.WILDCARD_TYPE);
        this.numberMessages = 10;
    }
}
