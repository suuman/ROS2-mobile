package std_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * std_msgs/msg/Header (ROS 2). Note that the ROS 1 'seq' field no longer
 * exists in ROS 2.
 */
public class Header extends Message {

    public static final java.lang.String _TYPE = "std_msgs/msg/Header";

    public builtin_interfaces.Time stamp = new builtin_interfaces.Time();
    public java.lang.String frame_id = "";

    public java.lang.String getFrameId() {
        return frame_id;
    }

    public void setFrameId(java.lang.String frameId) {
        this.frame_id = frameId;
    }

    public org.ros.message.Time getStamp() {
        return new org.ros.message.Time(stamp.sec, stamp.nanosec);
    }

    public void setStamp(builtin_interfaces.Time stamp) {
        this.stamp = stamp;
    }
}
