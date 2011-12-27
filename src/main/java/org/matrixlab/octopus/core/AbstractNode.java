package org.matrixlab.octopus.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.matrixlab.octopus.core.processor.ProcessorComponent;
import org.matrixlab.octopus.core.processor.parameter.Parameter;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Abstract Base class implementation of the {@link Node} interface that contains a {@link #name}, {@link #description}
 * and {@link #location} along with the corresponding setter/getter implementations from the {@link Node} interface.
 * <p/>
 * This class also provides method to manipulate the {@link Parameter}s for this node.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class AbstractNode implements Node {

    private final UUID id;
    private String name;
    private String description;
    private Point location;
    private Set<Parameter> parameters = Sets.newHashSet();

    protected AbstractNode(UUID id) {
        this.id = id;
    }

    protected AbstractNode(UUID id, String name, String description) {
        this.id = id;
        setName(name);
        setDescription(description);
    }

    protected AbstractNode(UUID id, AbstractNode copyFromNode) {
        this.id = id;
        setName(copyFromNode.name);
        setDescription(copyFromNode.description);
        for (Parameter parameter : copyFromNode.getParameters()) {
            this.addParameter(parameter.newInstance());
        }
    }

    protected AbstractNode(AbstractNode copyFromNode) {
        this.id = copyFromNode.id;
        setName(copyFromNode.name);
        setDescription(copyFromNode.description);

        for (Parameter parameter : copyFromNode.getParameters()) {
            this.addParameter(parameter.copyOf());
        }
    }

    public final UUID getId() {
        return id;
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

    protected void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
    }

    protected void addParameter(Parameter.Builder parameter) {
        this.parameters.add(parameter.build());
    }

    protected Parameter getParameter(int parameterId) {
        return ProcessorComponent.getComponentById(parameters, parameterId);
    }

    protected String getParameterValueAsString(int parameterId) {
        return ProcessorComponent.getComponentById(parameters, parameterId).getValueAsString();
    }

    @Override
    public Set<Parameter> getParameters() {
        return ImmutableSet.copyOf(this.parameters);
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
