package sensor_msgs;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;

import std_msgs.Header;

/**
 * sensor_msgs/msg/BatteryState (ROS 2).
 */
public class BatteryState extends Message {

    public static final String _TYPE = "sensor_msgs/msg/BatteryState";

    public static final byte POWER_SUPPLY_STATUS_UNKNOWN = 0;
    public static final byte POWER_SUPPLY_STATUS_CHARGING = 1;
    public static final byte POWER_SUPPLY_STATUS_DISCHARGING = 2;
    public static final byte POWER_SUPPLY_STATUS_NOT_CHARGING = 3;
    public static final byte POWER_SUPPLY_STATUS_FULL = 4;

    public Header header = new Header();
    public float voltage;
    public float temperature;
    public float current;
    public float charge;
    public float capacity;
    public float design_capacity;
    public float percentage;
    public byte power_supply_status;
    public byte power_supply_health;
    public byte power_supply_technology;
    public boolean present;

    public Header getHeader() {
        return header;
    }

    public float getVoltage() {
        return voltage;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getCurrent() {
        return current;
    }

    public float getCharge() {
        return charge;
    }

    public float getCapacity() {
        return capacity;
    }

    public float getPercentage() {
        return percentage;
    }

    public byte getPowerSupplyStatus() {
        return power_supply_status;
    }

    public boolean getPresent() {
        return present;
    }
}
