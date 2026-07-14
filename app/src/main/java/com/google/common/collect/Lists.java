/*
 * Minimal shim of the Guava Lists class covering exactly the methods used in
 * this project. Replaces the full Guava dependency which was only available
 * transitively through the removed rosjava artifacts.
 */

package com.google.common.collect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Lists {

    private Lists() {
        // Utility class
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * @return a reversed copy of the given list
     */
    public static <E> List<E> reverse(List<E> list) {
        List<E> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }
}
