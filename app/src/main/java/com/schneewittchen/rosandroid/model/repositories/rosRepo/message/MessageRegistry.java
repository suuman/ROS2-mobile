package com.schneewittchen.rosandroid.model.repositories.rosRepo.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps ROS 2 message type strings to their local message classes.
 * Both the ROS 2 style ("geometry_msgs/msg/Twist") and the short ROS 1
 * style ("geometry_msgs/Twist") are accepted, since older app databases and
 * some rosbridge versions still use the short form.
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 12.07.2026 (ROS 2 migration)
 */
public final class MessageRegistry {

    private static final Map<String, Class<? extends Message>> REGISTRY = new HashMap<>();

    static {
        register(std_msgs.Bool._TYPE, std_msgs.Bool.class);
        register(std_msgs.String._TYPE, std_msgs.String.class);
        register(std_msgs.Header._TYPE, std_msgs.Header.class);

        register(builtin_interfaces.Time._TYPE, builtin_interfaces.Time.class);

        register(geometry_msgs.Point._TYPE, geometry_msgs.Point.class);
        register(geometry_msgs.Quaternion._TYPE, geometry_msgs.Quaternion.class);
        register(geometry_msgs.Vector3._TYPE, geometry_msgs.Vector3.class);
        register(geometry_msgs.Pose._TYPE, geometry_msgs.Pose.class);
        register(geometry_msgs.PoseStamped._TYPE, geometry_msgs.PoseStamped.class);
        register(geometry_msgs.PoseWithCovariance._TYPE, geometry_msgs.PoseWithCovariance.class);
        register(geometry_msgs.PoseWithCovarianceStamped._TYPE, geometry_msgs.PoseWithCovarianceStamped.class);
        register(geometry_msgs.Twist._TYPE, geometry_msgs.Twist.class);
        register(geometry_msgs.Transform._TYPE, geometry_msgs.Transform.class);
        register(geometry_msgs.TransformStamped._TYPE, geometry_msgs.TransformStamped.class);

        register(sensor_msgs.NavSatFix._TYPE, sensor_msgs.NavSatFix.class);
        register(sensor_msgs.NavSatStatus._TYPE, sensor_msgs.NavSatStatus.class);
        register(sensor_msgs.LaserScan._TYPE, sensor_msgs.LaserScan.class);
        register(sensor_msgs.BatteryState._TYPE, sensor_msgs.BatteryState.class);
        register(sensor_msgs.Image._TYPE, sensor_msgs.Image.class);
        register(sensor_msgs.CompressedImage._TYPE, sensor_msgs.CompressedImage.class);

        register(nav_msgs.OccupancyGrid._TYPE, nav_msgs.OccupancyGrid.class);
        register(nav_msgs.MapMetaData._TYPE, nav_msgs.MapMetaData.class);
        register(nav_msgs.Path._TYPE, nav_msgs.Path.class);

        register(tf2_msgs.TFMessage._TYPE, tf2_msgs.TFMessage.class);
    }

    private MessageRegistry() {
        // Utility class
    }

    private static void register(String type, Class<? extends Message> clazz) {
        REGISTRY.put(type, clazz);
        REGISTRY.put(toShortType(type), clazz);
    }

    /**
     * @param type message type string in ROS 1 or ROS 2 style
     * @return matching message class or null if the type is unknown
     */
    public static Class<? extends Message> get(String type) {
        if (type == null) {
            return null;
        }
        return REGISTRY.get(type);
    }

    /**
     * @return the ROS 2 style type string ("pkg/msg/Name") for a
     * potentially short ("pkg/Name") type string
     */
    public static String toRos2Type(String type) {
        if (type == null || type.isEmpty() || type.contains("/msg/")) {
            return type;
        }

        int separator = type.indexOf('/');
        if (separator < 0) {
            return type;
        }

        return type.substring(0, separator) + "/msg/" + type.substring(separator + 1);
    }

    /**
     * @return the short type string ("pkg/Name") for a ROS 2 style
     * ("pkg/msg/Name") type string
     */
    public static String toShortType(String type) {
        if (type == null) {
            return null;
        }
        return type.replace("/msg/", "/");
    }
}
