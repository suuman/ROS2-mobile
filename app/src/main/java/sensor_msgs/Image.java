package sensor_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * sensor_msgs/msg/Image (ROS 2). The pixel data arrives base64 encoded over
 * the rosbridge protocol and is decoded into a plain byte array.
 */
public class Image extends Message {

    public static final String _TYPE = "sensor_msgs/msg/Image";

    public Header header = new Header();
    public int height;
    public int width;
    public java.lang.String encoding = "";
    public int is_bigendian;
    public int step;
    public byte[] data = new byte[0];

    public Header getHeader() {
        return header;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public java.lang.String getEncoding() {
        return encoding;
    }

    public int getIsBigendian() {
        return is_bigendian;
    }

    public int getStep() {
        return step;
    }

    public byte[] getData() {
        return data;
    }
}
