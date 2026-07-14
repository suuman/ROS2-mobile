package builtin_interfaces;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * builtin_interfaces/msg/Time (ROS 2).
 */
public class Time extends Message {

    public static final String _TYPE = "builtin_interfaces/msg/Time";

    public int sec;
    public int nanosec;

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getNanosec() {
        return nanosec;
    }

    public void setNanosec(int nanosec) {
        this.nanosec = nanosec;
    }

    public double toSeconds() {
        return sec + nanosec / 1e9;
    }
}
