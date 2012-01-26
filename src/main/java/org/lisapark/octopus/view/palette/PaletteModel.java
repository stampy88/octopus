package org.lisapark.octopus.view.palette;

import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;

import java.util.List;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PaletteModel {

    private final NodeListModel<Processor> processorListModel;
    private final NodeListModel<ExternalSink> externalSinkListModel;
    private final NodeListModel<ExternalSource> externalSourceListMode;

    public PaletteModel() {
        processorListModel = NodeListModel.newNodeListModel();
        externalSinkListModel = NodeListModel.newNodeListModel();
        externalSourceListMode = NodeListModel.newNodeListModel();
    }

    public void setProcessorListData(List<Processor> data) {
        processorListModel.setData(data);
    }

    public NodeListModel<Processor> getProcessorListModel() {
        return processorListModel;
    }

    public void setExternalSinkListData(List<ExternalSink> data) {
        externalSinkListModel.setData(data);
    }

    public NodeListModel<ExternalSink> getExternalSinkListModel() {
        return externalSinkListModel;
    }

    public void setExternalSourceListData(List<ExternalSource> data) {
        externalSourceListMode.setData(data);
    }

    public NodeListModel<ExternalSource> getExternalSourceListMode() {
        return externalSourceListMode;
    }

}
