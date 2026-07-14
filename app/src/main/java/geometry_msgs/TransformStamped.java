package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * geometry_msgs/msg/TransformStamped (ROS 2).
 */
public class TransformStamped extends Message {

    public static final String _TYPE = "geometry_msgs/msg/TransformStamped";

    public Header header = new Header();
    public java.lang.String child_frame_id = "";
    public Transform transform = new Transform();

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public java.lang.String getChildFrameId() {
        return child_frame_id;
    }

    public void setChildFrameId(java.lang.String childFrameId) {
        this.child_frame_id = childFrameId;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
