package org.lisapark.octopus.core.parameter;

import org.lisapark.octopus.core.ValidationException;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ConversionException extends ValidationException {
    public ConversionException(Exception e) {
        super(e);
    }

    public ConversionException(String msg) {
        super(msg);
    }
}
