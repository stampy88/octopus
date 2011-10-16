package org.matrixlab.octopus.core.event;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class AttributeDefinitionTest {

    @Test
    public void testIsNumeric() {
        AttributeDefinition<?> attr = AttributeDefinition.integerAttribute("test");
        assertTrue(attr.isNumeric());

        attr = AttributeDefinition.doubleAttribute("test");
        assertTrue(attr.isNumeric());

        attr = AttributeDefinition.dateAttribute("test");
        assertFalse(attr.isNumeric());

        attr = AttributeDefinition.floatAttribute("test");
        assertTrue(attr.isNumeric());

        attr = AttributeDefinition.longAttribute("test");
        assertTrue(attr.isNumeric());

        attr = AttributeDefinition.stringAttribute("test");
        assertFalse(attr.isNumeric());
    }
}
