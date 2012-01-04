package org.lisapark.octopus.core;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Copyable {
    /**
     * Implementers need to return new instance that is an <b>exact</b> copy of this instance.
     *
     * @return copy of this instance
     */
    Copyable copyOf();
}
