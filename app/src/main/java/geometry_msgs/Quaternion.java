package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * geometry_msgs/msg/Quaternion (ROS 2).
 */
public class Quaternion extends Message {

    public static final String _TYPE = "geometry_msgs/msg/Quaternion";

    public double x;
    public double y;
    public double z;
    public double w = 1.0;

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

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }
}
