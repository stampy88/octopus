package org.matrixlab.octopus.core.processor;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link ProcessorComponent} is a configurable piece of a {@link Processor} that has an immutable {@link #id} which
 * is used for internal identification within the processor only, not external identification, a {@link #name}
 * which is a mutable name for this component that is meant for a GUI, along with a mutable {@link #description} of
 * this component which is also meant for display purposes.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class ProcessorComponent {
    /**
     * The id is used internally by a {@link Processor} to have something to identity an individual component by. This is
     * needed because both the {@link #name} and {@link #description} are mutable.
     */
    private final int id;

    private String name;
    private String description;

    protected ProcessorComponent(int id) {
        this.id = id;
    }

    protected ProcessorComponent(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    protected ProcessorComponent(ProcessorComponent otherComponent) {
        this.id = otherComponent.id;
        this.name = otherComponent.name;
        this.description = otherComponent.description;
    }

    public final int getId() {
        return id;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof ProcessorComponent)) {
            return false;
        }

        ProcessorComponent that = (ProcessorComponent) otherObject;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static <T extends ProcessorComponent> T getComponentById(Collection<T> components, int id) {
        checkArgument(components != null, "components cannot be null");
        T component = null;

        for (T candidateComponent : components) {
            if (candidateComponent.getId() == id) {
                component = candidateComponent;
                break;
            }
        }

        return component;
    }
}
