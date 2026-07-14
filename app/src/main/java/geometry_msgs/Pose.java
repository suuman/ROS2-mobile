package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * geometry_msgs/msg/Pose (ROS 2).
 */
public class Pose extends Message {

    public static final String _TYPE = "geometry_msgs/msg/Pose";

    public Point position = new Point();
    public Quaternion orientation = new Quaternion();

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }
}
