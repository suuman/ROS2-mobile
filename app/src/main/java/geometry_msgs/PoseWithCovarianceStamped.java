package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * geometry_msgs/msg/PoseWithCovarianceStamped (ROS 2).
 */
public class PoseWithCovarianceStamped extends Message {

    public static final String _TYPE = "geometry_msgs/msg/PoseWithCovarianceStamped";

    public Header header = new Header();
    public PoseWithCovariance pose = new PoseWithCovariance();

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public PoseWithCovariance getPose() {
        return pose;
    }

    public void setPose(PoseWithCovariance pose) {
        this.pose = pose;
    }
}
