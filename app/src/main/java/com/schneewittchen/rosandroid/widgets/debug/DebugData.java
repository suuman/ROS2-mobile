package com.schneewittchen.rosandroid.widgets.debug;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.message.Message;
import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.BaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Converts an arbitrary received ROS 2 message into a human readable,
 * YAML-like string by traversing its raw rosbridge JSON representation.
 *
 * @author Nils Rottmann
 * @version 2.0.0
 * @created on 17.08.2020
 * @updated on 12.07.2026 (ROS 2 migration, JSON based introspection)
 */
public class DebugData extends BaseData {

    public static final String TAG = DebugData.class.getSimpleName();

    /**
     * Maximum number of displayed elements of primitive arrays
     * (e.g. laser scan ranges or map data).
     */
    private static final int MAX_ARRAY_ELEMENTS = 32;

    private final ArrayList<String> content;
    public String value;


    public DebugData(Message message) {
        content = new ArrayList<>();

        JsonObject json = message.getRawJson();
        if (json != null) {
            jsonToString(json, 0);
        }

        content.add("---------");
        value = joinContent("\n", content);
    }


    private void jsonToString(JsonObject json, int level) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            elementToString(entry.getKey(), entry.getValue(), level);
        }
    }

    private void elementToString(String name, JsonElement element, int level) {
        String indent = repeat("\t", level);

        if (element.isJsonObject()) {
            content.add(indent + name + ":");
            jsonToString(element.getAsJsonObject(), level + 1);

        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();

            if (array.size() > 0 && (array.get(0).isJsonObject() || array.get(0).isJsonArray())) {
                content.add(indent + name + ":");

                for (JsonElement arrayElement : array) {
                    content.add(repeat("\t", level + 1) + "-");

                    if (arrayElement.isJsonObject()) {
                        jsonToString(arrayElement.getAsJsonObject(), level + 2);
                    } else {
                        elementToString("", arrayElement, level + 2);
                    }
                }

            } else {
                content.add(indent + name + ": " + arrayToString(array));
            }

        } else {
            content.add(indent + name + ": " + element.toString());
        }
    }

    private String arrayToString(JsonArray array) {
        StringBuilder out = new StringBuilder("[");

        int n = Math.min(array.size(), MAX_ARRAY_ELEMENTS);
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                out.append(", ");
            }
            out.append(array.get(i).toString());
        }

        if (array.size() > MAX_ARRAY_ELEMENTS) {
            out.append(", ... (").append(array.size()).append(" values)");
        }

        out.append("]");
        return out.toString();
    }

    private String repeat(String str, int times) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < times; i++) {
            out.append(str);
        }
        return out.toString();
    }

    private String joinContent(String delimiter, List<String> content) {
        String loopDelim = "";
        StringBuilder out = new StringBuilder();

        for (String s : content) {
            out.append(loopDelim);
            out.append(s);

            loopDelim = delimiter;
        }

        return out.toString();
    }
}
