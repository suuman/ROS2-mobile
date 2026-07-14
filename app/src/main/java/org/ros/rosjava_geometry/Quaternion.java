/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.rosjava_geometry;

/**
 * A quaternion. Ported from rosjava_geometry and adapted to the plain ROS 2
 * message classes used with the rosbridge protocol.
 *
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class Quaternion {

    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Quaternion fromAxisAngle(Vector3 axis, double angle) {
        Vector3 normalized = axis.normalize();
        double sin = Math.sin(angle / 2.0d);
        double cos = Math.cos(angle / 2.0d);
        return new Quaternion(normalized.getX() * sin, normalized.getY() * sin,
                normalized.getZ() * sin, cos);
    }

    public static Quaternion fromQuaternionMessage(geometry_msgs.Quaternion message) {
        return new Quaternion(message.x, message.y, message.z, message.w);
    }

    public static Quaternion identity() {
        return new Quaternion(0, 0, 0, 1);
    }

    public Quaternion scale(double factor) {
        return new Quaternion(x * factor, y * factor, z * factor, w * factor);
    }

    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public Quaternion invert() {
        double mm = getMagnitudeSquared();
        return new Quaternion(-x / mm, -y / mm, -z / mm, w / mm);
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                w * other.x + x * other.w + y * other.z - z * other.y,
                w * other.y + y * other.w + z * other.x - x * other.z,
                w * other.z + z * other.w + x * other.y - y * other.x,
                w * other.w - x * other.x - y * other.y - z * other.z);
    }

    public Vector3 rotateAndScaleVector(Vector3 vector) {
        Quaternion vectorQuaternion = new Quaternion(vector.getX(), vector.getY(), vector.getZ(), 0);
        Quaternion rotatedQuaternion = multiply(vectorQuaternion.multiply(conjugate()));
        return new Vector3(rotatedQuaternion.getX(), rotatedQuaternion.getY(),
                rotatedQuaternion.getZ());
    }

    public double getMagnitudeSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public double getMagnitude() {
        return Math.sqrt(getMagnitudeSquared());
    }

    public boolean isAlmostNeutral(double epsilon) {
        return Math.abs(1 - x * x - y * y - z * z - w * w) < epsilon;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public geometry_msgs.Quaternion toQuaternionMessage(geometry_msgs.Quaternion result) {
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        return result;
    }

    @Override
    public String toString() {
        return String.format("Quaternion<x: %.4f, y: %.4f, z: %.4f, w: %.4f>", x, y, z, w);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(w);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quaternion other = (Quaternion) obj;
        return Double.doubleToLongBits(w) == Double.doubleToLongBits(other.w)
                && Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
                && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y)
                && Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z);
    }
}
