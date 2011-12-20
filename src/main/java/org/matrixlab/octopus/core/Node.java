package org.matrixlab.octopus.core;

import java.awt.*;
import java.util.UUID;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
public interface Node {

    UUID getId();

    String getName();

    Node setName(String name);

    String getDescription();

    Node setDescription(String description);

    Point getLocation();

    Node setLocation(Point location);
}
