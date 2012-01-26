package org.lisapark.octopus.view;

import com.jidesoft.action.CommandBar;
import com.jidesoft.action.CommandMenuBar;
import com.jidesoft.action.DefaultDockableBarDockableHolder;
import com.jidesoft.action.DockableBarManager;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideMenu;
import com.jidesoft.swing.JideScrollPane;
import com.jidesoft.utils.Lm;
import org.lisapark.octopus.view.canvas.DesignerView;
import org.lisapark.octopus.view.palette.PaletteView;
import org.lisapark.octopus.view.properties.PropertiesView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class ApplicationView extends DefaultDockableBarDockableHolder {
    private final DesignerView designerView;
    private final PropertiesView propertiesView;
    private final PaletteView paletteView;

    // todo localize and icons

    public ApplicationView(DesignerView designerView, PropertiesView propertiesView, PaletteView paletteView) {
        super("Octopus");
        this.designerView = designerView;
        this.propertiesView = propertiesView;
        this.paletteView = paletteView;

        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add the menu bar
        DockableBarManager dockableBarManager = getDockableBarManager();
        DockingManager dockingManager = getDockingManager();

        dockableBarManager.addDockableBar(createMenuBar());

        dockingManager.getWorkspace().add(createScrollPaneForComponent(designerView));
        dockingManager.beginLoadLayoutData();
        dockingManager.setInitSplitPriority(DockingManager.SPLIT_EAST_WEST_SOUTH_NORTH);

        DockableFrame modelFrame = createDockableFrameWithName("Properties");
        modelFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        modelFrame.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
        modelFrame.add(createScrollPaneForComponent(propertiesView));

        dockingManager.addFrame(modelFrame);

        // todo real output and can we hook it up with some meaningful messages?
        DockableFrame logFrame = createDockableFrameWithName("Output");
        logFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        logFrame.getContext().setInitSide(DockContext.DOCK_SIDE_SOUTH);
        // todo
        logFrame.add(createScrollPaneForComponent(new JTextArea()));

        dockingManager.addFrame(logFrame);

        DockableFrame paletteFrame = createDockableFrameWithName("Palette");
        paletteFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
        paletteFrame.getContext().setInitSide(DockContext.DOCK_SIDE_EAST);
        paletteFrame.add(createScrollPaneForComponent(paletteView));

        dockingManager.addFrame(paletteFrame);

        // todo IMPORTANT need these statements to work
        dockingManager.loadLayoutData();
        dockableBarManager.loadLayoutData();

        StatusBar statusBar = new StatusBar();
        TimeStatusBarItem time = new TimeStatusBarItem();
        statusBar.add(time, JideBoxLayout.FLEXIBLE);
        MemoryStatusBarItem gc = new MemoryStatusBarItem();
        statusBar.add(gc, JideBoxLayout.FIX);

        getContentPane().add(statusBar, BorderLayout.AFTER_LAST_LINE);

        pack();
    }

    private DockableFrame createDockableFrameWithName(String name) {
        // todo sizing and names
        DockableFrame dockableFrame = new DockableFrame(name);
        //   dockableFrame.setPreferredSize(new Dimension(200, 200));
        dockableFrame.setTitle(name);
        dockableFrame.setSideTitle("Side " + name);
        dockableFrame.setTabTitle("Tab " + name);

        return dockableFrame;
    }

    private JScrollPane createScrollPaneForComponent(JComponent c) {
        JScrollPane pane = new JideScrollPane(c);
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
        JMenu windowMenu = createWindowsMenu();
        JMenu helpMenu = createHelpMenu();

        commandBar.add(fileMenu);
        commandBar.add(viewMenu);
        commandBar.add(windowMenu);
        commandBar.add(helpMenu);

        return commandBar;
    }

    private JMenu createFileMenu() {
        JMenuItem item;

        JMenu menu = new JideMenu("File");
        menu.setMnemonic('F');

        item = new JMenuItem("Exit");
        menu.add(item);
        return menu;
    }

    private JMenu createViewMenu() {
        JMenuItem item;
        JMenu menu = new JideMenu("View");
        menu.setMnemonic('V');

        item = new JMenuItem("Project View", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.FRAME1));
        item.setMnemonic('P');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        menu.add(item);

        item = new JMenuItem("Class View", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.FRAME2));
        item.setMnemonic('A');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        menu.add(item);

        item = new JMenuItem("Server Explorer", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.FRAME3));
        item.setMnemonic('V');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        menu.add(item);

        item = new JMenuItem("Resource View", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.FRAME4));
        item.setMnemonic('R');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        menu.add(item);

        item = new JMenuItem("Properties Window", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.FRAME5));
        item.setMnemonic('W');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        menu.add(item);

        return menu;
    }

    private JMenu createWindowsMenu() {
        JMenu menu = new JideMenu("Window");
        menu.setMnemonic('W');

        JMenuItem item = new JMenuItem("Load Default Layout");

        menu.add(item);

        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = new JideMenu("Help");
        menu.setMnemonic('H');

        JMenuItem item = new JMenuItem("About JIDE Products");
        item.setMnemonic('A');
        item.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Lm.showAboutMessageBox();
            }
        });
        menu.add(item);

        return menu;
    }

}
