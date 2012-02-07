package org.lisapark.octopus.core.event;

import org.lisapark.octopus.ProgrammerException;
import org.lisapark.octopus.core.Copyable;
import org.lisapark.octopus.core.Persistable;
import org.lisapark.octopus.core.ValidationException;

import static org.lisapark.octopus.util.Naming.checkValidity;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
@Persistable
public class Attribute implements Copyable {

    public static final Class[] SUPPORTED_TYPES = {
            String.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class
    };

    private String name;
    private Class type;

    private Attribute(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    private Attribute(Attribute attribute) {
        this.name = attribute.name;
        this.type = attribute.type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public boolean isCompatibleWith(Class<?> type) {
        boolean compatible = (this.type == type);

        if (!compatible && isNumeric(this.type) && isNumeric(type)) {
            compatible = true;
        }

        return compatible;
    }

    public Object createSampleData(int randomNumber) {
        if (type == String.class) {
            return String.valueOf(randomNumber);
        }

        if (type == Integer.class) {
            return randomNumber;
        }

        if (type == Long.class) {
            return (long) randomNumber;
        }

        if (type == Float.class) {
            return (float) randomNumber;
        }

        if (type == Double.class) {
            return (double) randomNumber;
        }

        if (type == Short.class) {
            return (short) randomNumber;
        }

        if (type == Boolean.class) {
            return randomNumber % 2;
        }

        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new ProgrammerException(e);
        }
    }

    private boolean isNumeric(Class<?> type) {
        return Number.class.isAssignableFrom(type);
    }

    @Override
    public Attribute copyOf() {
        return new Attribute(this);
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    public static Attribute newAttribute(Class clazz, String name) throws ValidationException {
        if (clazz == String.class) {
            return stringAttribute(name);
        }

        if (clazz == Integer.class) {
            return integerAttribute(name);
        }

        if (clazz == Long.class) {
            return longAttribute(name);
        }

        if (clazz == Float.class) {
            return floatAttribute(name);
        }

        if (clazz == Double.class) {
            return doubleAttribute(name);
        }

        if (clazz == Short.class) {
            return shortAttribute(name);
        }

        if (clazz == Boolean.class) {
            return booleanAttribute(name);
        }

        throw new IllegalArgumentException(String.format("%s is not a valid attribute type", clazz));
    }

    public static Attribute stringAttribute(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        return new Attribute(name, String.class);
    }

    public static Attribute integerAttribute(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        return new Attribute(name, Integer.class);
    }

    public static Attribute shortAttribute(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        return new Attribute(name, Short.class);
    }

    public static Attribute longAttribute(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        return new Attribute(name, Long.class);
    }

    public static Attribute floatAttribute(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        return new Attribute(name, Float.class);
    }

    public static Attribute doubleAttribute(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        return new Attribute(name, Double.class);
    }

    public static Attribute booleanAttribute(String name) throws ValidationException {
        checkValidity(name, "Attribute name");
        return new Attribute(name, Boolean.class);
    }
}
