package org.matrixlab.octopus.core;

/**
 * A {@link Reproducible} is capable of creating new instance based on itself, i.e. a copy of itself. Shallow
 * copying versus deep copying is up to the implementer, but we are generally making deeps copies unless the
 * object is immutable.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Reproducible {

    /**
     * Implementers need to return a new instance based on <code>this</code> instance.
     *
     * @return new instance
     */
    Reproducible newInstance();
}