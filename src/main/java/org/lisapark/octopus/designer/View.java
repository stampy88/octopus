package org.lisapark.octopus.designer;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface View<T> {

    void modelToView(T model);

    T viewToModel();
}
