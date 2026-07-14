package nav_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * nav_msgs/msg/OccupancyGrid (ROS 2). Cell occupancy probabilities are in the
 * range [0, 100] and unknown cells are -1.
 */
public class OccupancyGrid extends Message {

    public static final String _TYPE = "nav_msgs/msg/OccupancyGrid";

    public Header header = new Header();
    public MapMetaData info = new MapMetaData();
    public byte[] data = new byte[0];

    public Header getHeader() {
        return header;
    }

    public MapMetaData getInfo() {
        return info;
    }

    public byte[] getData() {
        return data;
    }
}
