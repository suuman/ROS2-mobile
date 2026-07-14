package sensor_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * sensor_msgs/msg/NavSatStatus (ROS 2).
 */
public class NavSatStatus extends Message {

    public static final String _TYPE = "sensor_msgs/msg/NavSatStatus";

    public static final byte STATUS_NO_FIX = -1;
    public static final byte STATUS_FIX = 0;
    public static final byte STATUS_SBAS_FIX = 1;
    public static final byte STATUS_GBAS_FIX = 2;

    public static final int SERVICE_GPS = 1;
    public static final int SERVICE_GLONASS = 2;
    public static final int SERVICE_COMPASS = 4;
    public static final int SERVICE_GALILEO = 8;

    public byte status = STATUS_NO_FIX;
    public int service;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }
}
