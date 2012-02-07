package org.lisapark.octopus.designer;

import com.jidesoft.action.CommandBar;
import com.jidesoft.action.CommandMenuBar;
import com.jidesoft.action.DefaultDockableBarDockableHolder;
import com.jidesoft.action.DockableBarManager;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideMenu;
import com.jidesoft.swing.JideScrollPane;
import org.lisapark.octopus.core.Node;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.core.processor.Processor;
import org.lisapark.octopus.core.sink.external.ExternalSink;
import org.lisapark.octopus.core.source.external.ExternalSource;
import org.lisapark.octopus.designer.canvas.CanvasPanel;
import org.lisapark.octopus.designer.canvas.NodeSelectionListener;
import org.lisapark.octopus.designer.palette.PalettePanel;
import org.lisapark.octopus.designer.properties.PropertiesPanel;
import org.lisapark.octopus.repository.OctopusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * This is the main {@link JFrame} for the Octopus Designer application.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DesignerFrame extends DefaultDockableBarDockableHolder {

    private static final Logger LOG = LoggerFactory.getLogger(DesignerFrame.class);

    /**
     * This is the current {@link ProcessingModel} we are working on
     */
    private ProcessingModel currentProcessingModel;

    /**
     * The repository is used for retrieving and saving different Octopus artifacts
     */
    private final OctopusRepository repository;

    private CanvasPanel canvasPanel;
    private PropertiesPanel propertiesPanel;
    private PalettePanel palettePanel;

    /**
     * This status bar label will contain the name of the {@link #currentProcessingModel}
     */
    private LabelStatusBarItem modelNameStatusItem;

    public DesignerFrame(OctopusRepository repository) {
        super("Octopus");
        this.repository = repository;

        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeDockableBarManager();
        initializeDockingManager();
        initializeStatusBar();
        pack();
    }

    /**
     * Initializes the {@link DockableBarManager} by creating and adding the {@link #createMenuBar()}
     */
    private void initializeDockableBarManager() {
        DockableBarManager dockableBarManager = getDockableBarManager();
        dockableBarManager.addDockableBar(createMenuBar());
        dockableBarManager.loadLayoutData();
    }

    /**
     * Initializes and adds all of the {@link DockableFrame}s to the {@link DockingManager}
     */
    private void initializeDockingManager() {
        DockingManager dockingManager = getDockingManager();
        dockingManager.getWorkspace().add(createCanvas());
        dockingManager.beginLoadLayoutData();
        dockingManager.setInitSplitPriority(DockingManager.SPLIT_EAST_WEST_SOUTH_NORTH);

        dockingManager.addFrame(createProperties());
        dockingManager.addFrame(createOutput());
        dockingManager.addFrame(createPalette());

        dockingManager.loadLayoutData();
    }

    /**
     * Initializes and adds the {@link StatusBar}
     */
    private void initializeStatusBar() {
        StatusBar statusBar = new StatusBar();

        TimeStatusBarItem time = new TimeStatusBarItem();
        MemoryStatusBarItem gc = new MemoryStatusBarItem();
        modelNameStatusItem = new LabelStatusBarItem();

        statusBar.add(time, JideBoxLayout.FLEXIBLE);
        statusBar.add(modelNameStatusItem, JideBoxLayout.VARY);
        statusBar.add(gc, JideBoxLayout.FIX);

        getContentPane().add(statusBar, BorderLayout.AFTER_LAST_LINE);
    }

    /**
     * Creates and returns a new {@link DockableFrame} with all relavent names set to the specified name.
     *
     * @param name of title
     * @return new frame
     */
    private DockableFrame createDockableFrameWithName(String name) {
        DockableFrame dockableFrame = new DockableFrame(name);
        dockableFrame.setTitle(name);
        dockableFrame.setSideTitle(name);
        dockableFrame.setTabTitle(name);

        return dockableFrame;
    }

    private JComponent createCanvas() {
        this.canvasPanel = new CanvasPanel();
        return createScrollPaneForComponent(canvasPanel);
    }

    private DockableFrame createPalette() {
        this.palettePanel = new PalettePanel();

        DockableFrame propertiesFrame = createDockableFrameWithName("Palette");
        propertiesFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        propertiesFrame.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
        propertiesFrame.getContext().setInitIndex(1);
        propertiesFrame.add(createScrollPaneForComponent(palettePanel));

        return propertiesFrame;
    }

    private DockableFrame createProperties() {
        this.propertiesPanel = new PropertiesPanel();

        NodeSelectionListener nodeSelectionListener = new NodeSelectionListener() {
            @Override
            public void selectedChanged(Node node) {
                if (node instanceof Processor) {
                    propertiesPanel.setSelectedProcessor((Processor) node);

                } else if (node instanceof ExternalSource) {
                    propertiesPanel.setSelectedExternalSource((ExternalSource) node);

                } else if (node instanceof ExternalSink) {
                    propertiesPanel.setSelectedExternalSink((ExternalSink) node);
                } else {
                    propertiesPanel.clearSelection();
                }
            }
        };
        canvasPanel.setNodeSelectionListener(nodeSelectionListener);

        DockableFrame propertiesFrame = createDockableFrameWithName("Properties");
        propertiesFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        propertiesFrame.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
        propertiesFrame.getContext().setInitIndex(0);
        propertiesFrame.add(createScrollPaneForComponent(propertiesPanel));

        return propertiesFrame;
    }

    private DockableFrame createOutput() {
        // todo real output and can we hook it up with some meaningful messages?
        DockableFrame logFrame = createDockableFrameWithName("Output");
        logFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        logFrame.getContext().setInitSide(DockContext.DOCK_SIDE_SOUTH);
        // todo
        logFrame.add(createScrollPaneForComponent(new JTextArea()));

        return logFrame;
    }

    private JScrollPane createScrollPaneForComponent(JComponent component) {
        JScrollPane pane = new JideScrollPane(component);
        pane.setVerticalScrollBarPolicy(JideScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return pane;
    }

    protected CommandBar createMenuBar() {
        // todo real menus and toolbar
        CommandBar commandBar = new CommandMenuBar("Menu Bar");
        commandBar.setStretch(true);
        commandBar.setPaintBackground(false);

        JMenu fileMenu = createFileMenu();


        commandBar.add(fileMenu);
        // todo
//        commandBar.add(viewMenu);
//        commandBar.add(windowMenu);
//        commandBar.add(helpMenu);

        return commandBar;
    }

    private JMenu createFileMenu() {
        JMenuItem newMi = createMenuItemWithMnemonicAndAccelerator("New", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newProcessingModel();
            }
        });

        JMenuItem openMi = createMenuItemWithMnemonicAndAccelerator("Open...", KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProcessingModel();
            }
        });

        JMenuItem saveMi = createMenuItemWithMnemonicAndAccelerator("Save", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProcessingModel();
            }
        });

        JMenuItem saveAsMi = createMenuItemWithMnemonicAndAccelerator("Save As", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
        saveAsMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //getController().saveAsProcessingModel();
            }
        });

        JMenuItem exitMi = new JMenuItem("Exit");
        exitMi.setMnemonic(KeyEvent.VK_X);

        JMenu fileMnu = new JideMenu("File");
        fileMnu.setMnemonic('F');
        fileMnu.add(newMi);
        fileMnu.add(openMi);
        fileMnu.add(new JSeparator());
        fileMnu.add(saveMi);

        // todo save as
        //fileMnu.add(saveAsMi);
        fileMnu.add(new JSeparator());
        fileMnu.add(exitMi);

        return fileMnu;
    }

    void loadInitialDataFromRepository() {
        List<ExternalSink> sinkTemplates = repository.getAllExternalSinkTemplates();
        palettePanel.setExternalSinks(sinkTemplates);

        List<ExternalSource> sourceTemplates = repository.getAllExternalSourceTemplates();
        palettePanel.setExternalSources(sourceTemplates);

        List<Processor> processorTemplates = repository.getAllProcessorTemplates();
        palettePanel.setProcessors(processorTemplates);
    }

    private void setCurrentProcessingModel(ProcessingModel currentProcessingModel) {
        this.currentProcessingModel = currentProcessingModel;
        modelNameStatusItem.setText(currentProcessingModel.getModelName());

        canvasPanel.setProcessingModel(currentProcessingModel);
        propertiesPanel.setProcessingModel(currentProcessingModel);
    }

    private void saveProcessingModel() {
        SaveModelDialog.saveProcessingModel(this, currentProcessingModel, repository);

        modelNameStatusItem.setText(currentProcessingModel.getModelName());

    }

    private void openProcessingModel() {
        ProcessingModel modelToOpen = OpenModelDialog.openProcessingModel(this, repository);

        if (modelToOpen != null) {
            LOG.debug("Opening processing model {}", modelToOpen.getModelName());

            setCurrentProcessingModel(modelToOpen);
        }
    }

    private void newProcessingModel() {
        setCurrentProcessingModel(new ProcessingModel("model"));
    }

    private JMenuItem createMenuItemWithMnemonicAndAccelerator(String name, int mnemonic, KeyStroke accelerator) {
        JMenuItem newItem = new JMenuItem(name, mnemonic);
        newItem.setAccelerator(accelerator);

        return newItem;
    }
}
