package org.lisapark.octopus.util;

import org.lisapark.octopus.core.ValidationException;

/**
 * This is a utility class that contains various methods dealing with naming of different elements in the
 * Octopus domain.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Naming {

    /**
     * Determines if the specified name is permissible as a valid name.
     *
     * @param name     to check
     * @param nameType used when throwing {@link ValidationException} to identify the type of name
     * @throws org.lisapark.octopus.core.ValidationException
     *          if the name is not valid
     */
    public static void checkValidity(String name, String nameType) throws ValidationException {
        if (name == null) {
            throw new ValidationException(String.format("%s cannot be null", nameType));
        }

        if (name.length() == 0) {
            throw new ValidationException(String.format("%s cannot be empty", nameType));
        }

        if (name.indexOf(' ') > -1) {
            throw new ValidationException(String.format("%s cannot have spaces in it", nameType));
        }

        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            throw new ValidationException(
                    String.format("%s cannot start with a '%c'. " +
                            "Names can only begin with a letter, a '$' or an '_'", nameType, name.charAt(0))
            );
        }

        for (int index = 1; index < name.length(); ++index) {
            if (!Character.isJavaIdentifierPart(name.charAt(index))) {
                throw new ValidationException(
                        String.format("%s cannot contain a '%c'", nameType, name.charAt(index))
                );
            }
        }
    }
}
