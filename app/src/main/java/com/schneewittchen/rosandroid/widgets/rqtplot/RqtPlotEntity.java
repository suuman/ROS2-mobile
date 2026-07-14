package com.schneewittchen.rosandroid.widgets.rqtplot;

import com.schneewittchen.rosandroid.model.entities.widgets.SubscriberWidgetEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;


/**
 * Plot widget entity. Subscribes to a topic of any message type.
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 29.05.21
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public class RqtPlotEntity extends SubscriberWidgetEntity {

    public String fieldPath;

    public RqtPlotEntity() {
        this.width = 8;
        this.height = 6;
        this.topic = new Topic("/plot", Topic.WILDCARD_TYPE);
        this.fieldPath = "/pos/xy";
    }
}
