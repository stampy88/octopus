package org.lisapark.octopus.util;

import com.google.common.base.Objects;

/**
 * Data structure for holding a pair of objects.
 *
 * @author dave sinclair (dsinclair@chariotsolutions.com)
 * @param <F> type of First object of Pair
 * @param <S> type if Second object of Pair
 */
public class Pair<F, S> {

    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null || getClass() != rhs.getClass()) {
            return false;
        }

        Pair otherPair = (Pair) rhs;

        if (first != null && otherPair.first != null && (!first.equals(otherPair.first))) {
            return false;
        }

        return second != null && otherPair.second != null && second.equals(otherPair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(first, second);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("first", first).
                add("second", "second").toString();
    }

    public static <F, S> Pair<F, S> newInstance(F first, S second) {
        return new Pair<F, S>(first, second);
    }
}
