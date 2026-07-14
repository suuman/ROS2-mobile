package geometry_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

/**
 * geometry_msgs/msg/Transform (ROS 2).
 */
public class Transform extends Message {

    public static final String _TYPE = "geometry_msgs/msg/Transform";

    public Vector3 translation = new Vector3();
    public Quaternion rotation = new Quaternion();

    public Vector3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3 translation) {
        this.translation = translation;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }
}
