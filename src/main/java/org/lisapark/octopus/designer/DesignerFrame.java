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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * This is the main {@link JFrame} for the Octopus Designer application.
 *
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DesignerFrame extends DefaultDockableBarDockableHolder {

    private static final Logger LOG = LoggerFactory.getLogger(DesignerFrame.class);

    /**
     * This is the profile used for Jide's {@link DockingManager}. It allows it to store layout information
     * that will be persisted from one session of the application to the next
     */
    private static final String PROFILE_KEY = "octopus";

    /**
     * These are the keys associated with the different {@link DockableFrame}s. They are used by the
     * {@link DockingManager} to show a frame
     */
    private static final String PROPERTIES_KEY = "Properties";
    private static final String OUTPUT_KEY = "Output";
    private static final String PALETTE_KEY = "Palette";

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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initializeDockableBarManager();
        initializeDockingManager();
        initializeStatusBar();

        addWindowListener(new WindowClosingListener());

        pack();
    }

    /**
     * Initializes the {@link DockableBarManager} by creating and adding the {@link #createMenuBar()}
     */
    private void initializeDockableBarManager() {
        DockableBarManager dockableBarManager = getDockableBarManager();
        dockableBarManager.setShowInitial(false);
        // loadLayoutData() will make the main JFrame visible, so we disable this feature with the next call
        dockableBarManager.addDockableBar(createMenuBar());
        dockableBarManager.loadLayoutData();
    }

    /**
     * Initializes and adds all of the {@link DockableFrame}s to the {@link DockingManager}
     */
    private void initializeDockingManager() {
        DockingManager dockingManager = getDockingManager();
        // loadLayoutData() will make the main JFrame visible, so we disable this feature with the next call
        dockingManager.setShowInitial(false);
        dockingManager.setProfileKey(PROFILE_KEY);

        dockingManager.getWorkspace().add(createCanvas());
        dockingManager.beginLoadLayoutData();
        dockingManager.setInitSplitPriority(DockingManager.SPLIT_EAST_WEST_SOUTH_NORTH);

        dockingManager.addFrame(createProperties());
        dockingManager.addFrame(createOutput());
        dockingManager.addFrame(createPalette());

        // load layout information from previous session. This indicates the end of beginLoadLayoutData() method above.
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

        DockableFrame propertiesFrame = createDockableFrameWithName(PALETTE_KEY);
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

        DockableFrame propertiesFrame = createDockableFrameWithName(PROPERTIES_KEY);
        propertiesFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        propertiesFrame.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
        propertiesFrame.getContext().setInitIndex(0);
        propertiesFrame.add(createScrollPaneForComponent(propertiesPanel));

        return propertiesFrame;
    }

    private DockableFrame createOutput() {
        // todo real output and can we hook it up with some meaningful messages?
        DockableFrame logFrame = createDockableFrameWithName(OUTPUT_KEY);
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
        JMenu viewMenu = createViewMenu();

        commandBar.add(fileMenu);
        commandBar.add(viewMenu);
        // todo 
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
        exitMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        });

        JMenu fileMnu = new JideMenu("File");
        fileMnu.setMnemonic('F');
        fileMnu.add(newMi);
        fileMnu.add(openMi);
        fileMnu.addSeparator();
        fileMnu.add(saveMi);

        // todo save as
        //fileMnu.add(saveAsMi);
        fileMnu.addSeparator();
        fileMnu.add(exitMi);

        return fileMnu;
    }

    private JMenu createViewMenu() {
        JMenuItem nextViewMi = new JMenuItem("Select Next View");
        nextViewMi.setMnemonic('N');
        nextViewMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        nextViewMi.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String frameKey = getDockingManager().getNextFrame(getDockingManager().getActiveFrameKey());
                if (frameKey != null) {
                    getDockingManager().showFrame(frameKey);
                }
            }
        });

        JMenuItem previousMi = new JMenuItem("Select Previous View");
        previousMi.setMnemonic('P');
        previousMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.SHIFT_MASK));
        previousMi.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String frameKey = getDockingManager().getPreviousFrame(getDockingManager().getActiveFrameKey());
                if (frameKey != null) {
                    getDockingManager().showFrame(frameKey);
                }
            }
        });

        JMenuItem paletteMi = new JMenuItem(PALETTE_KEY);
        paletteMi.setMnemonic('P');
        paletteMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        paletteMi.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                getDockingManager().showFrame(PALETTE_KEY);
            }
        });

        JMenuItem propertiesMi = new JMenuItem(PROPERTIES_KEY);
        propertiesMi.setMnemonic('W');
        propertiesMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        propertiesMi.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                getDockingManager().showFrame(PROPERTIES_KEY);
            }
        });

        JMenuItem outputMi = new JMenuItem(OUTPUT_KEY);
        outputMi.setMnemonic('U');
        outputMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        outputMi.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                getDockingManager().showFrame(OUTPUT_KEY);
            }
        });

        JMenu viewMenu = new JideMenu("View");
        viewMenu.setMnemonic('V');
        viewMenu.add(nextViewMi);
        viewMenu.add(previousMi);
        viewMenu.addSeparator();
        viewMenu.add(paletteMi);
        viewMenu.add(propertiesMi);
        viewMenu.addSeparator();
        viewMenu.add(outputMi);

        return viewMenu;
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

    /**
     * Shuts down the application by saving the layout state and dispose the frame
     */
    private void shutdown() {
        DockingManager dockingManager = getDockingManager();

        if (dockingManager != null) {
            LOG.debug("Saving docking layout");
            dockingManager.saveLayoutData();
        }

        DockableBarManager dockableBarManager = getDockableBarManager();

        if (dockableBarManager != null) {
            LOG.debug("Saving docking bar layout");
            dockableBarManager.saveLayoutData();
        }

        setVisible(false);
        dispose();
    }

    private JMenuItem createMenuItemWithMnemonicAndAccelerator(String name, int mnemonic, KeyStroke accelerator) {
        JMenuItem newItem = new JMenuItem(name, mnemonic);
        newItem.setAccelerator(accelerator);

        return newItem;
    }

    /**
     * This listener will respond to the user closing the frame. It will save the layout data for the
     * {@link com.jidesoft.docking.DockingManager}, {@link DockableBarManager} and dispose of the frame.
     */
    private class WindowClosingListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            shutdown();
        }

        @Override
        public void windowOpened(WindowEvent e) {
            DockingManager dockingManager = getDockingManager();

            if (dockingManager != null) {
                dockingManager.showInitial();
            }

            DockableBarManager dockableBarManager = getDockableBarManager();

            if (dockableBarManager != null) {
                dockableBarManager.showInitial();
            }
        }
    }
}
