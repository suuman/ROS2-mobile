package com.schneewittchen.rosandroid.widgets.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.schneewittchen.rosandroid.model.repositories.rosRepo.node.BaseData;

import sensor_msgs.CompressedImage;
import sensor_msgs.Image;


/**
 * Converts ROS 2 image messages into bitmaps. The pixel data arrives as a
 * plain byte array (base64 decoded from the rosbridge JSON).
 *
 * @author Nils Rottmann
 * @version 2.0.0
 * @created on 27.04.2020
 * @updated on 12.07.2026 (ROS 2 migration)
 */

public class CameraData extends BaseData {

    public static final String TAG = "CameraData";

    public Bitmap map;


    public CameraData(CompressedImage image) {
        this.map = this.convert(image);
    }

    public CameraData(Image image) {
        this.map = this.convert(image);
    }


    private Bitmap convert(CompressedImage image) {
        byte[] data = image.getData();
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private Bitmap convert(Image image) {
        Bitmap.Config config = null;

        // Get the data
        byte[] data = image.getData();
        int height = image.getHeight();
        int width = image.getWidth();
        int step = image.getStep();

        if (data.length < height * step || width == 0 || height == 0) {
            Log.i(TAG, "Invalid image data size");
            return null;
        }

        // Get the starting point of the data
        int dataStart = data.length - (height * step);
        int pixelBytesNum = step / width;

        // Encode Byte and transform to image
        int iStep, iWidth, dataStep, iColor;

        // Storage capacities
        int[] intArray = new int[height * width];
        int iR, iG, iB, iA, iM;

        // Init data extraction steps
        int monoX0, monoX1;

        switch (image.getEncoding()) {
            case "rgb8":
                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        iR = data[dataStep];
                        iG = data[dataStep + 1];
                        iB = data[dataStep + 2];

                        iColor = -16777216 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff);
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "rgba8":
                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        iR = data[dataStep];
                        iG = data[dataStep + 1];
                        iB = data[dataStep + 2];
                        iA = data[dataStep + 3];

                        iColor = ((iA & 0xff) << 24 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "bgr8":
                iA = 255;

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        iB = data[dataStep];
                        iG = data[dataStep + 1];
                        iR = data[dataStep + 2];

                        iColor = ((iA & 0xff) << 24 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "bgra8":
                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        iB = data[dataStep];
                        iG = data[dataStep + 1];
                        iR = data[dataStep + 2];
                        iA = data[dataStep + 3];

                        iColor = ((iA & 0xff) << 24 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "mono8":
                iA = 255;

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        iM = data[dataStep];

                        iColor = ((iA & 0xff) << 24 | (iM & 0xff) << 16 | (iM & 0xff) << 8 | (iM & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "mono16":
                iA = 255;

                if (image.getIsBigendian() == 0) {
                    monoX0 = 0;
                    monoX1 = 1;
                } else {
                    monoX0 = 1;
                    monoX1 = 0;
                }

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;
                        int m = ((data[dataStep + monoX1] & 0xff) << 8 | (data[dataStep + monoX0] & 0xff)) >> 8;

                        iColor = ((iA & 0xff) << 24 | (m & 0xff) << 16 | (m & 0xff) << 8 | (m & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            case "rgb16":
            case "rgba16":
            case "bgr16":
            case "bgra16":
                // 16 bit color channels are down converted to 8 bit.
                boolean isBgr = image.getEncoding().startsWith("bgr");
                boolean hasAlpha = image.getEncoding().contains("a");
                int channelCount = hasAlpha ? 4 : 3;
                int lowByte = image.getIsBigendian() == 0 ? 1 : 0;

                for (int i = 0; i < height; i++) {
                    iStep = i * step;
                    iWidth = i * width;

                    for (int j = 0; j < width; j++) {
                        dataStep = dataStart + iStep + j * pixelBytesNum;

                        int c0 = data[dataStep + lowByte] & 0xff;
                        int c1 = data[dataStep + 2 + lowByte] & 0xff;
                        int c2 = data[dataStep + 4 + lowByte] & 0xff;
                        iA = hasAlpha ? data[dataStep + 6 + lowByte] & 0xff : 255;

                        iR = isBgr ? c2 : c0;
                        iG = c1;
                        iB = isBgr ? c0 : c2;

                        iColor = ((iA & 0xff) << 24 | (iR & 0xff) << 16 | (iG & 0xff) << 8 | (iB & 0xff));
                        intArray[iWidth + j] = iColor;
                    }
                }

                config = Bitmap.Config.ARGB_8888;
                break;

            default:
                Log.i(TAG, "No compatible encoding: " + image.getEncoding());
        }

        // Create the bitmap if config is set and image is creatable
        if (config != null) {
            return Bitmap.createBitmap(intArray, width, height, config);

        } else {
            return null;
        }
    }
}
