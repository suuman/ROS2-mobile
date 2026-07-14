package com.schneewittchen.rosandroid.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * Connection details of the rosbridge server running on the ROS 2 system
 * (default port of the rosbridge websocket server is 9090).
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 30.01.20
 * @updated on 12.07.2026 (ROS 2 migration)
 */
@Entity(tableName = "master_table")
public class MasterEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long configId;
    public String ip = "192.168.0.0";
    public int port = 9090;
}
