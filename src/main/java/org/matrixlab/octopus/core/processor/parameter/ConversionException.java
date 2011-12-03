package org.matrixlab.octopus.core.processor.parameter;

import org.matrixlab.octopus.core.ValidationException;

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
