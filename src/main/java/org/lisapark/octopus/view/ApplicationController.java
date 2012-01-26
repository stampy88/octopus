package org.lisapark.octopus.view;

import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.service.ExternalSinkService;
import org.lisapark.octopus.service.ExternalSourceService;
import org.lisapark.octopus.service.ProcessorService;
import org.lisapark.octopus.view.designer.DesignerView;
import org.lisapark.octopus.view.palette.PaletteModel;
import org.lisapark.octopus.view.palette.PaletteView;
import org.lisapark.octopus.view.properties.PropertiesController;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;

import java.util.Set;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ApplicationController {

    // todo other services

    private final ProcessingModel model;
    private final ProcessorService processorService;
    private final ExternalSourceService externalSourceService;
    private final ExternalSinkService externalSinkService;

    private PaletteView paletteView;
    private PaletteModel paletteModel;

    private PropertiesController propertiesController;

    private DesignerView designerView;

    private ApplicationView applicationView;

    public ApplicationController(ProcessingModel model, ProcessorService processorService,
                                 ExternalSourceService externalSourceService,
                                 ExternalSinkService externalSinkService) {
        this.model = model;
        this.processorService = processorService;
        this.externalSourceService = externalSourceService;
        this.externalSinkService = externalSinkService;

        init();
    }

    private void init() {
        createAndInitializePalette();
        createAndInitializeProperties();
        createAndInitializeDesigner();
        createAndInitializeApplication();
    }

    public void start() {
        applicationView.setVisible(true);
    }

    private void createAndInitializeApplication() {
        applicationView = new ApplicationView(designerView, propertiesController.getView(), paletteView);

    }

    private void createAndInitializeDesigner() {
        // todo name? and model
        designerView = new DesignerView(model);

        designerView.addObjectSceneListener(new MyObjectSceneListener(), ObjectSceneEventType.values());
    }

    private void createAndInitializePalette() {
        paletteModel = new PaletteModel();
        paletteModel.setProcessorListData(processorService.getAllProcessorTemplates());
        paletteModel.setExternalSourceListData(externalSourceService.getAllExternalSourceTemplates());
        paletteModel.setExternalSinkListData(externalSinkService.getAllExternalSinkTemplates());

        paletteView = new PaletteView();
        paletteView.modelToView(paletteModel);
    }

    private void createAndInitializeProperties() {
        propertiesController = new PropertiesController();
    }

    private class MyObjectSceneListener implements ObjectSceneListener {

        @Override
        public void objectAdded(ObjectSceneEvent event, Object addedObject) {
            System.out.println("objectAdded");
        }

        @Override
        public void objectRemoved(ObjectSceneEvent event, Object removedObject) {
            System.out.println("removedObject");
        }

        @Override
        public void objectStateChanged(ObjectSceneEvent event, Object changedObject, ObjectState previousState, ObjectState newState) {
            System.out.println("objectStateChanged");

            System.out.printf("%s %s -> %s", changedObject, previousState, newState);

            // Sma@f5e806a ObjectState@41f227 -> org.netbeans.api.visual.model.ObjectState@1811e2cselectionChanged
            if (changedObject != null && newState != null && newState.isSelected()) {

                // todo 

                if (changedObject instanceof Processor) {
                    propertiesController.setSelectedObject(changedObject);

                } else if (changedObject instanceof ExternalSource) {
                    propertiesController.setSelectedObject(changedObject);

                } else if (changedObject instanceof ExternalSink) {
                    propertiesController.setSelectedObject(changedObject);
                }
            }
        }

        @Override
        public void selectionChanged(ObjectSceneEvent event, Set<Object> previousSelection, Set<Object> newSelection) {
            System.out.println("selectionChanged");
        }

        @Override
        public void highlightingChanged(ObjectSceneEvent event, Set<Object> previousHighlighting, Set<Object> newHighlighting) {
            System.out.println("highlightingChanged");
        }

        @Override
        public void hoverChanged(ObjectSceneEvent event, Object previousHoveredObject, Object newHoveredObject) {
            System.out.println("hoverChanged");
        }

        @Override
        public void focusChanged(ObjectSceneEvent event, Object previousFocusedObject, Object newFocusedObject) {
            System.out.println("focusChanged");
        }
    }
}
