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

import org.ros.namespace.GraphName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import geometry_msgs.TransformStamped;

/**
 * A tree of {@link FrameTransform}s. Simplified port of the rosjava
 * FrameTransformTree working on the plain ROS 2 message classes that are
 * received via the rosbridge protocol.
 *
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class FrameTransformTree {

    /**
     * Maps a child frame to the transform that describes the child frame
     * relative to its parent frame (i.e. transforms data from the child frame
     * into the parent frame).
     */
    private final Map<GraphName, ParentTransform> transforms = new ConcurrentHashMap<>();

    /**
     * Updates the tree with the given transform message.
     *
     * @param transformStamped transform of a child frame relative to its parent
     */
    public void update(TransformStamped transformStamped) {
        if (transformStamped == null || transformStamped.child_frame_id == null) {
            return;
        }

        GraphName child = GraphName.of(transformStamped.child_frame_id).toRelative();
        GraphName parent = GraphName.of(transformStamped.getHeader().getFrameId()).toRelative();
        Transform transform = Transform.fromTransformMessage(transformStamped.transform);

        transforms.put(child, new ParentTransform(parent, transform));
    }

    /**
     * @return the {@link FrameTransform} from source frame to target frame, or
     * null if no connection between both frames is known
     */
    public FrameTransform transform(GraphName source, GraphName target) {
        if (source == null || target == null) {
            return null;
        }

        GraphName sourceFrame = source.toRelative();
        GraphName targetFrame = target.toRelative();

        if (sourceFrame.equals(targetFrame)) {
            return new FrameTransform(Transform.identity(), sourceFrame, targetFrame);
        }

        FrameTransform sourceToRoot = transformToRoot(sourceFrame);
        FrameTransform targetToRoot = transformToRoot(targetFrame);

        if (sourceToRoot == null && targetToRoot == null) {
            return null;
        }

        if (sourceToRoot == null) {
            if (targetToRoot.getTargetFrame().equals(sourceFrame)) {
                // The source frame is the root of the target frame.
                return targetToRoot.invert();
            }
            return null;
        }

        if (targetToRoot == null) {
            if (sourceToRoot.getTargetFrame().equals(targetFrame)) {
                // The target frame is the root of the source frame.
                return sourceToRoot;
            }
            return null;
        }

        if (sourceToRoot.getTargetFrame().equals(targetToRoot.getTargetFrame())) {
            // Both frames share the same root.
            Transform transform =
                    targetToRoot.getTransform().invert().multiply(sourceToRoot.getTransform());
            return new FrameTransform(transform, sourceFrame, targetFrame);
        }

        return null;
    }

    /**
     * @return the transform from the given frame to its tree root together
     * with the root as target frame, or null if the frame is unknown
     */
    private FrameTransform transformToRoot(GraphName frame) {
        ParentTransform parentTransform = transforms.get(frame);
        if (parentTransform == null) {
            return null;
        }

        Transform result = parentTransform.transform;
        GraphName root = parentTransform.parent;

        // Walk up the tree. Guard against cycles caused by broken tf trees.
        int depth = 0;
        while (depth++ < 100) {
            ParentTransform next = transforms.get(root);
            if (next == null) {
                break;
            }
            result = next.transform.multiply(result);
            root = next.parent;
        }

        return new FrameTransform(result, frame, root);
    }

    private static final class ParentTransform {
        final GraphName parent;
        final Transform transform;

        ParentTransform(GraphName parent, Transform transform) {
            this.parent = parent;
            this.transform = transform;
        }
    }
}
