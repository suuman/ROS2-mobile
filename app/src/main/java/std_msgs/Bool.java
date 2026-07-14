package std_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * std_msgs/msg/Bool (ROS 2).
 */
public class Bool extends Message {

    public static final java.lang.String _TYPE = "std_msgs/msg/Bool";

    public boolean data;

    public boolean getData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}
