package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * geometry_msgs/msg/Point (ROS 2).
 */
public class Point extends Message {

    public static final String _TYPE = "geometry_msgs/msg/Point";

    public double x;
    public double y;
    public double z;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
