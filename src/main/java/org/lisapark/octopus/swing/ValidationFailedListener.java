package org.lisapark.octopus.swing;

import com.jidesoft.validation.ValidationResult;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface ValidationFailedListener {

    void validationFailed(ValidationResult result);
}
