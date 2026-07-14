package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * geometry_msgs/msg/PoseWithCovariance (ROS 2).
 */
public class PoseWithCovariance extends Message {

    public static final String _TYPE = "geometry_msgs/msg/PoseWithCovariance";

    public Pose pose = new Pose();
    public double[] covariance = new double[36];

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public double[] getCovariance() {
        return covariance;
    }

    public void setCovariance(double[] covariance) {
        this.covariance = covariance;
    }
}
