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

package org.ros.message;

/**
 * Minimal replacement of the rosjava Time class. In ROS 2 timestamps are
 * builtin_interfaces/msg/Time (sec/nanosec); this class keeps the rosjava
 * naming so that the plotting widgets stay untouched.
 */
public class Time {

    public int secs;
    public int nsecs;

    public Time() {
        secs = 0;
        nsecs = 0;
    }

    public Time(int secs, int nsecs) {
        this.secs = secs;
        this.nsecs = nsecs;
        normalize();
    }

    public Time(double secs) {
        this.secs = (int) secs;
        this.nsecs = (int) ((secs - this.secs) * 1000000000);
    }

    public static Time fromMillis(long timeInMillis) {
        int secs = (int) (timeInMillis / 1000);
        int nsecs = (int) (timeInMillis % 1000) * 1000000;
        return new Time(secs, nsecs);
    }

    public Duration subtract(Time other) {
        return new Duration(secs - other.secs, nsecs - other.nsecs);
    }

    public double toSeconds() {
        return secs + nsecs / 1e9;
    }

    public void normalize() {
        while (nsecs < 0) {
            nsecs += 1000000000;
            secs -= 1;
        }
        while (nsecs >= 1000000000) {
            nsecs -= 1000000000;
            secs += 1;
        }
    }

    @Override
    public String toString() {
        return secs + ":" + nsecs;
    }
}
