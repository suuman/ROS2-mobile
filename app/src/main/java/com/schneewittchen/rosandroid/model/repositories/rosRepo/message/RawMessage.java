package com.schneewittchen.rosandroid.model.repositories.rosRepo.message;

/**
 * Generic message for topics with unknown or wildcard type. Only carries the
 * raw rosbridge JSON, which can be introspected by generic widgets like the
 * debug view or the rqt plot view.
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 12.07.2026 (ROS 2 migration)
 */
public class RawMessage extends Message {
}
