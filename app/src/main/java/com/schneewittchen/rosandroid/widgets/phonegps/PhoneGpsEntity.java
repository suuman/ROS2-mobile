package com.schneewittchen.rosandroid.widgets.phonegps;

import com.schneewittchen.rosandroid.model.entities.widgets.PublisherWidgetEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;

import geometry_msgs.Twist;

/**
 * Phone GPS widget entity. The widget publishes Twist messages built from
 * the GPS course (see GPSData and PhoneGPSDetailVH); the former NavSatFix
 * topic type was a leftover inconsistency and is fixed with the ROS 2
 * migration.
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 31.01.20
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public class PhoneGpsEntity extends PublisherWidgetEntity {

    public String xAxisMapping;
    public String yAxisMapping;
    public float xScaleLeft;
    public float xScaleRight;
    public float yScaleLeft;
    public float yScaleRight;
    public boolean rectangularLimits;

    public PhoneGpsEntity() {
        this.width = 4;
        this.height = 4;
        this.topic = new Topic("android/gps", Twist._TYPE);
        this.immediatePublish = false;
        this.publishRate = 20f;
        this.xAxisMapping = "Angular/Z";
        this.yAxisMapping = "Linear/X";
        this.xScaleLeft = 1;
        this.xScaleRight = -1;
        this.yScaleLeft = -1;
        this.yScaleRight = 1;
        this.rectangularLimits = false;
    }

}
