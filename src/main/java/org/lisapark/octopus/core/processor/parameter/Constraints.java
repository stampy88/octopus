package org.lisapark.octopus.core.processor.parameter;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class Constraints {

    public static Constraint<String> classConstraintWithMessage(String message) {
        checkArgument(message != null, "message cannot be null");
        return new ClassConstraint(message);
    }

    private static class ClassConstraint implements Constraint<String> {

        private final String message;

        public ClassConstraint(String message) {
            this.message = message;
        }

        @Override
        public void validate(String name, String value) throws ConstraintException {
            try {
                Class.forName(value);
            } catch (ClassNotFoundException e) {
                throw new ConstraintException(String.format(message, value), e);
            }
        }

        @Override
        public ClassConstraint copyOf() {
            return new ClassConstraint(message);
        }
    }
}
