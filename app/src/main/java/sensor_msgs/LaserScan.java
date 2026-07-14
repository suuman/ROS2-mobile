package sensor_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * sensor_msgs/msg/LaserScan (ROS 2).
 */
public class LaserScan extends Message {

    public static final String _TYPE = "sensor_msgs/msg/LaserScan";

    public Header header = new Header();
    public float angle_min;
    public float angle_max;
    public float angle_increment;
    public float time_increment;
    public float scan_time;
    public float range_min;
    public float range_max;
    public float[] ranges = new float[0];
    public float[] intensities = new float[0];

    public Header getHeader() {
        return header;
    }

    public float getAngleMin() {
        return angle_min;
    }

    public float getAngleMax() {
        return angle_max;
    }

    public float getAngleIncrement() {
        return angle_increment;
    }

    public float getTimeIncrement() {
        return time_increment;
    }

    public float getScanTime() {
        return scan_time;
    }

    public float getRangeMin() {
        return range_min;
    }

    public float getRangeMax() {
        return range_max;
    }

    public float[] getRanges() {
        return ranges;
    }

    public float[] getIntensities() {
        return intensities;
    }
}
