package org.lisapark.octopus.view.palette;

import com.jidesoft.swing.JideScrollPane;
import com.jidesoft.swing.JideTabbedPane;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.view.Constants;
import org.lisapark.octopus.view.OctopusIconsFactory;
import org.lisapark.octopus.view.View;
import org.lisapark.octopus.view.dnd.ExternalSinkTransferable;
import org.lisapark.octopus.view.dnd.ExternalSourceTransferable;
import org.lisapark.octopus.view.dnd.ProcessorTransferable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Locale;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class PaletteView extends JPanel implements View<PaletteModel> {

    private PaletteModel model;

    private JList processorList;
    private JList externalSourceList;
    private JList externalSinkList;
    private final NodeListCellRenderer cellRenderer = new NodeListCellRenderer();

    public PaletteView() {
        super(new BorderLayout(Constants.COMPONENT_HORIZONTAL_GAP, Constants.COMPONENT_VERTICAL_GAP));
        init();
    }

    private void init() {
        JideTabbedPane tabbedPane = new JideTabbedPane(JideTabbedPane.BOTTOM);
        tabbedPane.setOpaque(true);

        Component leadingComponent = new JLabel(OctopusIconsFactory.getImageIcon(OctopusIconsFactory.OCTOPUS_LARGE));
        tabbedPane.setTabLeadingComponent(leadingComponent);

        final String[] titles = new String[]{
                "Processors",
                "Sources",
                "Sinks"
        };

        final int[] mnemonics = new int[]{
                KeyEvent.VK_P,
                KeyEvent.VK_S,
                KeyEvent.VK_K,
        };

        // todo real icons
        final ImageIcon[] icons = new ImageIcon[]{
                OctopusIconsFactory.getImageIcon("/function-icon.png"),
                OctopusIconsFactory.getImageIcon("/node-insert-icon.png"),
                OctopusIconsFactory.getImageIcon("/node-delete-child-icon.png"),
        };

        final JComponent[] components = new JComponent[]{
                createProcessorPanel(),
                createExternalSourcePanel(),
                createExternalSinkPanel()
        };

        for (int i = 0; i < titles.length; i++) {
            //  scrollPane.setPreferredSize(new Dimension(530, 530));
            tabbedPane.addTab(titles[i], icons[i], components[i]);
            tabbedPane.setMnemonicAt(i, mnemonics[i]);
        }

        add(tabbedPane);
    }

    private JComponent createProcessorPanel() {
        processorList = createListWithTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                Processor processor = (Processor) processorList.getSelectedValue();

                return new ProcessorTransferable(processor);
            }
        });

        JPanel processorPanel = new JPanel(new BorderLayout(Constants.COMPONENT_HORIZONTAL_GAP, Constants.COMPONENT_VERTICAL_GAP));
        processorPanel.add(new JideScrollPane(processorList), BorderLayout.CENTER);

        return processorPanel;
    }

    private JComponent createExternalSourcePanel() {
        externalSourceList = createListWithTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                ExternalSource externalSource = (ExternalSource) externalSourceList.getSelectedValue();

                return new ExternalSourceTransferable(externalSource);
            }
        });

        JPanel processorPanel = new JPanel(new BorderLayout(Constants.COMPONENT_HORIZONTAL_GAP, Constants.COMPONENT_VERTICAL_GAP));
        processorPanel.add(new JideScrollPane(externalSourceList), BorderLayout.CENTER);

        return processorPanel;
    }

    private JComponent createExternalSinkPanel() {
        externalSinkList = createListWithTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                ExternalSink externalSink = (ExternalSink) externalSinkList.getSelectedValue();

                return new ExternalSinkTransferable(externalSink);
            }
        });

        JPanel processorPanel = new JPanel(new BorderLayout(Constants.COMPONENT_HORIZONTAL_GAP, Constants.COMPONENT_VERTICAL_GAP));
        processorPanel.add(new JideScrollPane(externalSinkList), BorderLayout.CENTER);

        return processorPanel;
    }

    private JList createListWithTransferHandler(TransferHandler transferHandler) {
        JList list = new ListWithToolTip();

        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setDragEnabled(true);
        list.setCellRenderer(cellRenderer);
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setTransferHandler(transferHandler);

        return list;
    }

    @Override
    public void modelToView(PaletteModel model) {
        this.model = model;

        processorList.setModel(model.getProcessorListModel());
        externalSinkList.setModel(model.getExternalSinkListModel());
        externalSourceList.setModel(model.getExternalSourceListMode());
    }

    @Override
    public PaletteModel viewToModel() {
        return model;
    }

    /**
     * Gets the localized string from resource bundle. Available keys are defined in palette.properties that
     * begin with "PaletteView.".
     *
     * @param key the resource key
     * @return the localized string.
     */
    protected String getResourceString(String key) {
        return Resources.getResourceBundle(Locale.getDefault()).getString(key);
    }

    private static class ListWithToolTip extends JList {
        // This method is called as the cursor moves within the list.

        public String getToolTipText(MouseEvent evt) {
            int index = locationToIndex(evt.getPoint());

            String toolTip = null;
            if (index > -1) {
                // Get item
                Object item = getModel().getElementAt(index);

                if (item != null) {
                    Node node = (Node) item;
                    toolTip = node.getDescription();
                }
            }

            return toolTip;
        }
    }
}
