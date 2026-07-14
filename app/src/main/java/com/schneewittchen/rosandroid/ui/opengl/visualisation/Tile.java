package com.schneewittchen.rosandroid.ui.opengl.visualisation;

/**
 * In order to draw maps with a size outside the maximum size of a texture,
 * we split the map into multiple tiles and draw one texture per tile.
 *
 * @author moesenle@google.com (Lorenz Moesenlechner)
 * @version 3.0
 * @updated on 12.07.2026 (ROS 2 migration, netty buffer replaced by int array)
 */

import com.google.common.base.Preconditions;

import org.ros.rosjava_geometry.Transform;

import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import nav_msgs.OccupancyGrid;


public class Tile {

    /**
     * Color of transparent cells in the map.
     */
    private static final int COLOR_TRANSPARENT = 0x00000000;

    private final TextureBitmap textureBitmap = new TextureBitmap();

    /**
     * Pixel color buffer of the tile.
     */
    private int[] pixelBuffer = new int[TextureBitmap.STRIDE * TextureBitmap.HEIGHT];

    /**
     * Number of valid pixels in {@link #pixelBuffer}.
     */
    private int pixelCount = 0;

    /**
     * Resolution of the {@link OccupancyGrid}.
     */
    private final float resolution;

    /**
     * Points to the top left of the {@link Tile}.
     */
    private Transform origin;

    /**
     * Width of the {@link Tile}.
     */
    private int stride;

    /**
     * Height of the {@link Tile}.
     */
    private int height;

    /**
     * {@code true} when the {@link Tile} is ready to be drawn.
     */
    private boolean ready;


    public Tile(float resolution) {
        this.resolution = resolution;
        ready = false;
    }

    public void draw(VisualizationView view, GL10 gl) {
        if (ready) {
            textureBitmap.draw(view, gl);
        }
    }

    public void clearHandle() {
        textureBitmap.clearHandle();
    }

    public void writeInt(int value) {
        if (pixelCount == pixelBuffer.length) {
            pixelBuffer = Arrays.copyOf(pixelBuffer, pixelBuffer.length * 2);
        }
        pixelBuffer[pixelCount++] = value;
    }

    public void update() {
        Preconditions.checkNotNull(origin);
        textureBitmap.updateFromPixelBuffer(pixelBuffer, pixelCount, stride, height, resolution,
                origin, COLOR_TRANSPARENT);
        pixelCount = 0;
        ready = true;
    }

    public void setOrigin(Transform origin) {
        this.origin = origin;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
