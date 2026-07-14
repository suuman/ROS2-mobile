package com.schneewittchen.rosandroid.model.repositories.rosRepo.message;

import com.google.gson.JsonObject;

/**
 * Base class of all ROS 2 messages exchanged over the rosbridge protocol.
 * Replaces the former rosjava message interface
 * (org.ros.internal.message.Message).
 * <p>
 * Concrete messages are plain Java objects whose public fields mirror the
 * ROS 2 message definition (snake_case names) so that they can be
 * (de)serialized with Gson straight from/to the rosbridge JSON wire format.
 * The raw JSON of a received message is kept alongside the typed fields so
 * generic widgets (debug, rqt plot) can introspect arbitrary message types.
 *
 * @author Nico Studt
 * @version 2.0.0
 * @updated on 12.07.2026 (ROS 2 migration)
 */
public abstract class Message {

    /**
     * Raw rosbridge JSON of a received message. Transient so it is ignored
     * when the message itself is serialized for publishing. Null for
     * messages created locally.
     */
    private transient JsonObject rawJson;

    public JsonObject getRawJson() {
        return rawJson;
    }

    public void setRawJson(JsonObject rawJson) {
        this.rawJson = rawJson;
    }
}
