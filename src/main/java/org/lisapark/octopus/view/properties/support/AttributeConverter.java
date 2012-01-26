package org.lisapark.octopus.view.properties.support;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import org.lisapark.octopus.core.event.Attribute;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class AttributeConverter implements ObjectConverter {
    // todo read more about converters

    @Override
    public String toString(Object object, ConverterContext converterContext) {
        String value = null;
        if (object instanceof Attribute) {
            return ((Attribute) object).getName();
        }

        return value;
    }

    @Override
    public boolean supportToString(Object o, ConverterContext converterContext) {
        return true;
    }

    @Override
    public Object fromString(String s, ConverterContext converterContext) {
        return null;
    }

    @Override
    public boolean supportFromString(String s, ConverterContext converterContext) {
        return false;
    }
}
