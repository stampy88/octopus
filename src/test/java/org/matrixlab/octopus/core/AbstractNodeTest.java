package org.matrixlab.octopus.core;

import org.junit.Test;

import java.awt.*;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class AbstractNodeTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullName() throws Exception {
        new AbstractNodeImpl(null, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullDescription() throws Exception {
        new AbstractNodeImpl("name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetName_Null() throws Exception {
        Node node = new AbstractNodeImpl();
        node.setName(null);
    }

    @Test
    public void testSetName() throws Exception {
        Node node = new AbstractNodeImpl();
        node = node.setName("test");

        assertThat(node.getName(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDescription_Null() throws Exception {
        Node node = new AbstractNodeImpl();
        node.setDescription(null);
    }

    @Test
    public void testSetDescription() throws Exception {
        Node node = new AbstractNodeImpl();
        node = node.setDescription("test");

        assertThat(node.getDescription(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLocation_Null() throws Exception {
        Node node = new AbstractNodeImpl();
        node.setLocation(null);
    }

    @Test
    public void testSetLocation() throws Exception {
        Node node = new AbstractNodeImpl();
        Point location = new Point(1, 2);
        node = node.setLocation(location);

        assertThat(node.getLocation(), is(location));
    }

    private static class AbstractNodeImpl extends AbstractNode {

        private AbstractNodeImpl(String name, String description) {
            super(name, description);
        }

        private AbstractNodeImpl() {
        }

        @Override
        public UUID getId() {
            return null;
        }

        @Override
        public Reproducible newInstance() {
            return new AbstractNodeImpl(this.getName(), this.getDescription());
        }
    }
}
