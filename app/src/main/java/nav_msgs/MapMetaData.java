package nav_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import geometry_msgs.Pose;

/**
 * nav_msgs/msg/MapMetaData (ROS 2).
 */
public class MapMetaData extends Message {

    public static final String _TYPE = "nav_msgs/msg/MapMetaData";

    public builtin_interfaces.Time map_load_time = new builtin_interfaces.Time();
    public float resolution;
    public int width;
    public int height;
    public Pose origin = new Pose();

    public float getResolution() {
        return resolution;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Pose getOrigin() {
        return origin;
    }
}
