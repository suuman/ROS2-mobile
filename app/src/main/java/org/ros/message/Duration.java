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
 * Minimal replacement of the rosjava Duration class.
 */
public class Duration {

    public int secs;
    public int nsecs;

    public Duration() {
    }

    public Duration(int secs, int nsecs) {
        this.secs = secs;
        this.nsecs = nsecs;
        normalize();
    }

    public Duration(double secs) {
        this.secs = (int) secs;
        this.nsecs = (int) ((secs - this.secs) * 1000000000);
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
