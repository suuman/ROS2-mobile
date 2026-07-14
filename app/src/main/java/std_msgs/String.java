package std_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * std_msgs/msg/String (ROS 2).
 */
public class String extends Message {

    public static final java.lang.String _TYPE = "std_msgs/msg/String";

    public java.lang.String data = "";

    public java.lang.String getData() {
        return data;
    }

    public void setData(java.lang.String data) {
        this.data = data;
    }
}
