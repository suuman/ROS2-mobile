package com.schneewittchen.rosandroid.model.repositories.rosRepo.message;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Central Gson instance for (de)serializing ROS 2 messages from/to the
 * rosbridge JSON wire format.
 * <p>
 * rosbridge encodes uint8[] fields (e.g. images, occupancy grids) as base64
 * strings, while int8[] fields may arrive as plain JSON arrays of numbers.
 * Additionally float fields can arrive as null (NaN in strict JSON mode).
 * Custom type adapters cover all of these cases.
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 12.07.2026 (ROS 2 migration)
 */
public final class MessageGson {

    private static final Gson INSTANCE = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new ByteArrayAdapter())
            .registerTypeAdapter(float[].class, new FloatArrayAdapter())
            .serializeSpecialFloatingPointValues()
            .create();

    private MessageGson() {
        // Utility class
    }

    public static Gson get() {
        return INSTANCE;
    }

    /**
     * Reads byte arrays either from a base64 encoded string (rosbridge
     * default for uint8[]) or from a plain JSON array of numbers (int8[]).
     * Writes base64 strings, which rosbridge accepts for byte array fields.
     */
    private static class ByteArrayAdapter extends TypeAdapter<byte[]> {

        @Override
        public void write(JsonWriter out, byte[] value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(Base64.encodeToString(value, Base64.NO_WRAP));
        }

        @Override
        public byte[] read(JsonReader in) throws IOException {
            JsonToken token = in.peek();

            if (token == JsonToken.NULL) {
                in.nextNull();
                return new byte[0];
            }

            if (token == JsonToken.STRING) {
                return Base64.decode(in.nextString(), Base64.DEFAULT);
            }

            if (token == JsonToken.BEGIN_ARRAY) {
                List<Byte> bytes = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        bytes.add((byte) 0);
                    } else {
                        bytes.add((byte) in.nextInt());
                    }
                }
                in.endArray();

                byte[] result = new byte[bytes.size()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = bytes.get(i);
                }
                return result;
            }

            in.skipValue();
            return new byte[0];
        }
    }

    /**
     * Reads float arrays tolerating null entries (NaN values that some
     * rosbridge versions serialize as null, e.g. laser scan ranges).
     */
    private static class FloatArrayAdapter extends TypeAdapter<float[]> {

        @Override
        public void write(JsonWriter out, float[] value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            for (float f : value) {
                out.value(f);
            }
            out.endArray();
        }

        @Override
        public float[] read(JsonReader in) throws IOException {
            JsonToken token = in.peek();

            if (token == JsonToken.NULL) {
                in.nextNull();
                return new float[0];
            }

            List<Float> floats = new ArrayList<>();
            in.beginArray();
            while (in.hasNext()) {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    floats.add(Float.NaN);
                } else {
                    floats.add((float) in.nextDouble());
                }
            }
            in.endArray();

            float[] result = new float[floats.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = floats.get(i);
            }
            return result;
        }
    }
}
