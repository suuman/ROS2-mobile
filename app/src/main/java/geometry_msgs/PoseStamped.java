package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * geometry_msgs/msg/PoseStamped (ROS 2).
 */
public class PoseStamped extends Message {

    public static final String _TYPE = "geometry_msgs/msg/PoseStamped";

    public Header header = new Header();
    public Pose pose = new Pose();

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }
}
