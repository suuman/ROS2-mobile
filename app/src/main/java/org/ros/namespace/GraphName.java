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

package org.ros.namespace;

/**
 * Minimal replacement of the rosjava GraphName used to identify
 * ROS graph resources (e.g. tf frame ids). Kept in the original package
 * so that the visualization code base stays untouched after the
 * migration from rosjava (ROS 1) to rosbridge (ROS 2).
 */
public class GraphName {

    private final String name;

    private GraphName(String name) {
        this.name = name;
    }

    public static GraphName of(String name) {
        if (name == null) {
            return empty();
        }
        return new GraphName(canonicalize(name));
    }

    public static GraphName empty() {
        return new GraphName("");
    }

    private static String canonicalize(String name) {
        String result = name.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * @return this graph name without any leading slashes. ROS 2 tf frame ids
     * are not allowed to start with a slash, while ROS 1 sources sometimes
     * published them with one. Relative names make both worlds comparable.
     */
    public GraphName toRelative() {
        String result = name;
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        return new GraphName(result);
    }

    public boolean isEmpty() {
        return name.isEmpty();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return name.equals(((GraphName) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
