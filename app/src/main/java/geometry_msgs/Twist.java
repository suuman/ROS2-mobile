package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * geometry_msgs/msg/Twist (ROS 2).
 */
public class Twist extends Message {

    public static final String _TYPE = "geometry_msgs/msg/Twist";

    public Vector3 linear = new Vector3();
    public Vector3 angular = new Vector3();

    public Vector3 getLinear() {
        return linear;
    }

    public void setLinear(Vector3 linear) {
        this.linear = linear;
    }

    public Vector3 getAngular() {
        return angular;
    }

    public void setAngular(Vector3 angular) {
        this.angular = angular;
    }
}
