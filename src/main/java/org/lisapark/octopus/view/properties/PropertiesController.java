package org.lisapark.octopus.view.properties;

import org.lisapark.octopus.swing.DefaultValidationFailedListener;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PropertiesController {

    private PropertiesModel model;
    private PropertiesView view;

    public PropertiesController() {

        model = new PropertiesModel();
        view = new PropertiesView();

        view.setValidationFailedListener(new DefaultValidationFailedListener(view));
        view.modelToView(model);
    }

    public void initialize() {

        view.modelToView(model);
    }

    public void setSelectedObject(Object object) {
        checkArgument(object != null, "processor cannot be null");

        // todo remove any old listeners add new

        model.setData(object);
        view.getPropertyTable().expandFirstLevel();
    }

    public PropertiesView getView() {
        return view;
    }
}
