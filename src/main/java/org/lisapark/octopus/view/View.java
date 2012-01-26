package org.lisapark.octopus.view;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public interface View<T> {

    void modelToView(T model);

    T viewToModel();
}
