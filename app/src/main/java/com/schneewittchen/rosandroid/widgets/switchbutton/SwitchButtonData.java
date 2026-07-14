package com.schneewittchen.rosandroid.widgets.switchbutton;

import com.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.BaseData;

import std_msgs.Bool;

/**
 * Switch button data which is converted into a std_msgs/msg/Bool message.
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 10.05.2022
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public class SwitchButtonData extends BaseData {

    public boolean pressed;

    public SwitchButtonData(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public Message toRosMessage(BaseEntity widget) {
        Bool message = new Bool();
        message.setData(pressed);
        return message;
    }
}
