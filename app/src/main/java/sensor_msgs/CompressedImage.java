package sensor_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * sensor_msgs/msg/CompressedImage (ROS 2). The compressed data (jpeg/png)
 * arrives base64 encoded over the rosbridge protocol and is decoded into a
 * plain byte array.
 */
public class CompressedImage extends Message {

    public static final String _TYPE = "sensor_msgs/msg/CompressedImage";

    public Header header = new Header();
    public java.lang.String format = "";
    public byte[] data = new byte[0];

    public Header getHeader() {
        return header;
    }

    public java.lang.String getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }
}
