package sensor_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * sensor_msgs/msg/NavSatFix (ROS 2).
 */
public class NavSatFix extends Message {

    public static final String _TYPE = "sensor_msgs/msg/NavSatFix";

    public static final byte COVARIANCE_TYPE_UNKNOWN = 0;
    public static final byte COVARIANCE_TYPE_APPROXIMATED = 1;
    public static final byte COVARIANCE_TYPE_DIAGONAL_KNOWN = 2;
    public static final byte COVARIANCE_TYPE_KNOWN = 3;

    public Header header = new Header();
    public NavSatStatus status = new NavSatStatus();
    public double latitude;
    public double longitude;
    public double altitude;
    public double[] position_covariance = new double[9];
    public byte position_covariance_type = COVARIANCE_TYPE_UNKNOWN;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public NavSatStatus getStatus() {
        return status;
    }

    public void setStatus(NavSatStatus status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double[] getPositionCovariance() {
        return position_covariance;
    }

    public void setPositionCovariance(double[] positionCovariance) {
        this.position_covariance = positionCovariance;
    }

    public byte getPositionCovarianceType() {
        return position_covariance_type;
    }

    public void setPositionCovarianceType(byte positionCovarianceType) {
        this.position_covariance_type = positionCovarianceType;
    }
}
