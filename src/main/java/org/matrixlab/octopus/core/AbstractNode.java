package org.matrixlab.octopus.core;

import java.awt.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Abstract Base class implementation of the {@link Node} interface that contains a {@link #name}, {@link #description}
 * and {@link #location} along with the corresponding setter/getter implementations from the {@link Node} interface.
 *
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public abstract class AbstractNode implements Node {

    private String name;
    private String description;
    private Point location;

    protected AbstractNode() {
    }

    protected AbstractNode(String name, String description) {
        setName(name);
        setDescription(description);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public AbstractNode setName(String name) {
        checkArgument(name != null, "name cannot be null");
        this.name = name;

        return this;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    @Override
    public AbstractNode setDescription(String description) {
        checkArgument(description != null, "description cannot be null");
        this.description = description;

        return this;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public AbstractNode setLocation(Point location) {
        checkArgument(location != null, "location cannot be null");
        this.location = location;

        return this;
    }

    /**
     * This implementation of equals will check that the specified otherObject is an instance of a {@link Node}
     * and the {@link #getId()} are equivalent.
     *
     * @param otherObject the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the otherObject; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Node)) {
            return false;
        }

        AbstractNode that = (AbstractNode) otherObject;

        return this.getId().equals(that.getId());
    }

    /**
     * This implementation of hashCode returns the {@link java.util.UUID#hashCode()} of the {@link #getId()}
     *
     * @return a hash code value for this node.
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}