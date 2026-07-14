package com.schneewittchen.rosandroid.widgets.button;

import com.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.BaseData;

import std_msgs.Bool;

/**
 * Button data which is converted into a std_msgs/msg/Bool message.
 *
 * @author Dragos Circa
 * @version 2.0.0
 * @created on 02.11.2020
 * @updated on 12.07.2026 (ROS 2 migration)
 */

public class ButtonData extends BaseData {

    public boolean pressed;

    public ButtonData(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public Message toRosMessage(BaseEntity widget) {
        Bool message = new Bool();
        message.setData(pressed);
        return message;
    }
}
