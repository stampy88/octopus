package org.lisapark.octopus.designer.properties;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PropertiesController {

    private PropertiesPresentationModel model;
    private PropertiesView view;

    public PropertiesController() {

        model = new PropertiesPresentationModel();
        view = new PropertiesView(model);

    }

    public void setSelectedObject(Object object) {
        checkArgument(object != null, "processor cannot be null");

        // todo remove any old listeners add new

        model.setCurrentNode(object);
    }

    public PropertiesView getView() {
        return view;
    }
}
