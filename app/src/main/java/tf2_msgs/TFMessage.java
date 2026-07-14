package tf2_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import java.util.ArrayList;
import java.util.List;

import geometry_msgs.TransformStamped;

/**
 * tf2_msgs/msg/TFMessage (ROS 2).
 */
public class TFMessage extends Message {

    public static final String _TYPE = "tf2_msgs/msg/TFMessage";

    public List<TransformStamped> transforms = new ArrayList<>();

    public List<TransformStamped> getTransforms() {
        return transforms;
    }
}
