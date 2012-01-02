package org.lisapark.octopus.core;

import org.lisapark.octopus.core.processor.parameter.Parameter;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Node extends Reproducible, Validatable {

    UUID getId();

    String getName();

    Node setName(String name);

    String getDescription();

    Node setDescription(String description);

    Point getLocation();

    Node setLocation(Point location);

    Set<Parameter> getParameters();
}
