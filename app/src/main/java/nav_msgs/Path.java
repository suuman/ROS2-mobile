package nav_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import java.util.ArrayList;
import java.util.List;

import geometry_msgs.PoseStamped;
import std_msgs.Header;

/**
 * nav_msgs/msg/Path (ROS 2).
 */
public class Path extends Message {

    public static final String _TYPE = "nav_msgs/msg/Path";

    public Header header = new Header();
    public List<PoseStamped> poses = new ArrayList<>();

    public Header getHeader() {
        return header;
    }

    public List<PoseStamped> getPoses() {
        return poses;
    }
}
