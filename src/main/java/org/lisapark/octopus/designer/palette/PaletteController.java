package org.lisapark.octopus.designer.palette;

import org.lisapark.octopus.service.ExternalSinkService;
import org.lisapark.octopus.service.ExternalSourceService;
import org.lisapark.octopus.service.ProcessorService;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PaletteController {

    private PaletteModel model;
    private PaletteView view;

    private final ProcessorService processorService;
    private final ExternalSourceService externalSourceService;
    private final ExternalSinkService externalSinkService;

    public PaletteController(ProcessorService processorService,
                             ExternalSourceService externalSourceService,
                             ExternalSinkService externalSinkService) {
        this.processorService = processorService;
        this.externalSourceService = externalSourceService;
        this.externalSinkService = externalSinkService;

        model = new PaletteModel();
        view = new PaletteView();
    }

    public void initialize() {
        model.setProcessorListData(processorService.getAllProcessorTemplates());
        model.setExternalSourceListData(externalSourceService.getAllExternalSourceTemplates());
        model.setExternalSinkListData(externalSinkService.getAllExternalSinkTemplates());

        view.modelToView(model);
    }

    public PaletteView getView() {
        return view;
    }
}
