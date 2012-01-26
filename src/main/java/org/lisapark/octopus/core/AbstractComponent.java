package org.lisapark.octopus.core;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link AbstractComponent} is a configurable piece of a {@link org.lisapark.octopus.core.processor.Processor},
 * {@link org.lisapark.octopus.core.sink.Sink}, or {@link org.lisapark.octopus.core.source.Source} that has an
 * immutable {@link #id} which is used for internal identification within host processor, source, etc. only,
 * not external identification, a {@link #name} which is a mutable name for this component that is meant for a GUI,
 * along with a mutable {@link #description} of this component which is also meant for display purposes.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class AbstractComponent implements Copyable, Validatable {
    /**
     * The id is used internally by a {@link org.lisapark.octopus.core.processor.Processor} to have something to identity an individual component by. This is
     * needed because both the {@link #name} and {@link #description} are mutable.
     */
    private final int id;

    private String name;
    private String description;

    protected AbstractComponent(int id) {
        this.id = id;
    }

    protected AbstractComponent(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    protected AbstractComponent(AbstractComponent otherComponent) {
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

    public AbstractComponent setDescription(String description) {
        this.description = description;
        return this;
    }

    public final String getName() {
        return name;
    }

    public AbstractComponent setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public void validate() throws ValidationException {
        // nothing to validate
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof AbstractComponent)) {
            return false;
        }

        AbstractComponent that = (AbstractComponent) otherObject;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static <T extends AbstractComponent> T getComponentById(Collection<T> components, int id) {
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
