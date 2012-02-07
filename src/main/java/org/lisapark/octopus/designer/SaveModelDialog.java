package org.lisapark.octopus.designer;

import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import com.jidesoft.plaf.UIDefaultsLookup;
import org.lisapark.octopus.core.ProcessingModel;
import org.lisapark.octopus.repository.OctopusRepository;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class SaveModelDialog extends StandardDialog {

    private final OctopusRepository repository;
    private ProcessingModel modelToSave;
    private JButton okButton;
    private JTextField modelNameTxt;

    private SaveModelDialog(JFrame frame, OctopusRepository repository, ProcessingModel modelToSave) {
        super(frame, "Octopus");
        setResizable(false);
        this.repository = repository;
        this.modelToSave = modelToSave;
    }

    @Override
    public JComponent createBannerPanel() {
        BannerPanel bannerPanel = new BannerPanel("Save Model",
                "Please type in the name for the processing model you are saving.",
                OctopusIconsFactory.getImageIcon(OctopusIconsFactory.OCTOPUS_LARGE));
        bannerPanel.setBackground(Color.WHITE);
        bannerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        return bannerPanel;
    }

    @Override
    public JComponent createContentPanel() {
        JLabel modelNameLbl = new JLabel("Model Name: ");
        modelNameLbl.setDisplayedMnemonic('N');
        modelNameLbl.setHorizontalAlignment(SwingConstants.CENTER);

        modelNameTxt = new JTextField();
        modelNameTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                okButton.setEnabled(modelNameTxt.getText().trim().length() > 0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                okButton.setEnabled(modelNameTxt.getText().trim().length() > 0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                okButton.setEnabled(modelNameTxt.getText().trim().length() > 0);
            }
        });
        modelNameTxt.setText(modelToSave.getModelName());

        modelNameLbl.setLabelFor(modelNameTxt);

        // note that this padding numbers are Jide recommendations
        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        topPanel.add(modelNameLbl, BorderLayout.BEFORE_LINE_BEGINS);
        topPanel.add(modelNameTxt, BorderLayout.CENTER);

        // note that this padding numbers are Jide recommendations
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        // note that this padding numbers are Jide recommendations
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        contentPanel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        contentPanel.setPreferredSize(new Dimension(400, 100));

        // we want the model name text field to have focus first when the dialog opens
        setInitFocusedComponent(modelNameTxt);

        return contentPanel;
    }

    @Override
    public ButtonPanel createButtonPanel() {
        ButtonPanel buttonPanel = new ButtonPanel();
        // note that these padding numbers coincide with what Jide recommends for StandardDialog button panels
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        okButton = new JButton();
        okButton.setName(OK);
        okButton.setAction(new AbstractAction("Save") {
            public void actionPerformed(ActionEvent e) {
                modelToSave.setModelName(modelNameTxt.getText());

                repository.saveProcessingModel(modelToSave);

                setDialogResult(RESULT_AFFIRMED);
                setVisible(false);
                dispose();
            }
        });
        // we need to disable the button AFTER setting the action
        okButton.setEnabled(false);

        JButton cancelButton = new JButton();
        cancelButton.setName(CANCEL);
        cancelButton.setAction(new AbstractAction(UIDefaultsLookup.getString("OptionPane.cancelButtonText")) {
            public void actionPerformed(ActionEvent e) {
                setDialogResult(RESULT_CANCELLED);
                setVisible(false);
                dispose();
            }
        });

        buttonPanel.addButton(okButton, ButtonPanel.AFFIRMATIVE_BUTTON);
        buttonPanel.addButton(cancelButton, ButtonPanel.CANCEL_BUTTON);

        setDefaultCancelAction(cancelButton.getAction());
        setDefaultAction(okButton.getAction());
        getRootPane().setDefaultButton(okButton);

        return buttonPanel;
    }

    /**
     * This method will create and display a new {@link org.lisapark.octopus.designer.SaveModelDialog} for opening
     * a {@link org.lisapark.octopus.core.ProcessingModel}.
     *
     * @param parent     to center dialog over
     * @param repository used for searching for models
     */
    public static void saveProcessingModel(Component parent, ProcessingModel modelToSave, OctopusRepository repository) {
        JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parent);
        SaveModelDialog dialog = new SaveModelDialog(frame, repository, modelToSave);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}

