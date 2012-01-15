package org.lisapark.octopus.core;

import org.lisapark.octopus.core.processor.parameter.Parameter;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.UUID;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface Node extends Reproducible, Validatable, Copyable {

    UUID getId();

    String getName();

    Node setName(String name);

    String getDescription();

    Node setDescription(String description);

    Set<Parameter> getParameters();

    Point getLocation();

    Node setLocation(Point location);

    Icon getIcon();

    Node setIcon(Icon icon);
}
