package org.lisapark.octopus.repository.db4o;

import com.db4o.instrumentation.core.ClassFilter;
import org.lisapark.octopus.core.Persistable;

import java.lang.annotation.Annotation;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PersistableFilter implements ClassFilter {
    @Override
    public boolean accept(Class<?> aClass) {
        if (null == aClass || aClass.equals(Object.class)) {
            return false;
        }
        return hasAnnotation(aClass)
                || accept(aClass.getSuperclass());
    }

    private boolean hasAnnotation(Class<?> aClass) {
        // We compare by name, to be class-loader independent
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName()
                    .equals(Persistable.class.getName())) {
                return true;
            }
        }
        return false;
    }

}
