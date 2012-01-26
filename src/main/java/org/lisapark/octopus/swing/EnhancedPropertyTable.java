package org.lisapark.octopus.swing;

import com.jidesoft.grid.EditingNotStoppedException;
import com.jidesoft.grid.Property;
import com.jidesoft.grid.PropertyTable;
import com.jidesoft.grid.PropertyTableModel;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.jidesoft.validation.ValidationResult;

import javax.swing.event.ChangeEvent;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class EnhancedPropertyTable extends PropertyTable {

    private ValidationFailedListener validationFailedListener;

    public EnhancedPropertyTable() {

    }

    public ValidationFailedListener getValidationFailedListener() {
        return validationFailedListener;
    }

    public void setValidationFailedListener(ValidationFailedListener validationFailedListener) {
        this.validationFailedListener = validationFailedListener;
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        try {
            super.editingStopped(e);

        } catch (EditingNotStoppedException ex) {
            ValidationResult result = ex.getValidationResult();

            if (validationFailedListener != null) {
                validationFailedListener.validationFailed(result);
            }
        }
    }

    /**
     * We override {@link PropertyTable#getToolTipText(java.awt.event.MouseEvent)} because we are going to use the
     *
     * @param mouseEvent
     * @return
     */
//    public String getToolTipText(MouseEvent mouseEvent) {
//
//        Point point = getCellAt(mouseEvent.getPoint());
//        Property property = getPropertyAtPoint(point);
//        String toolTip = null;
//
//        if (property != null) {
//            if (point.x == 1) {
//                if(property instanceof EnhancedProperty) {
//                    toolTip = ((EnhancedProperty)property).getToolTipText();
//                } else {
//                    toolTip = property.getDescription() == null ? property.getName() : property.getDescription();
//                    toolTip = "<HTML>" + HtmlUtils.formatHtmlSubString(toolTip) + "</HTML>";
//                }
//
//            } else {
//                toolTip = property.getDisplayName();
//            }
//        }
//
//        return toolTip;
//    }

    Property getPropertyAtPoint(Point point) {
        int rowIndex = point.y;
        TableModel tableModel = getModel();
        PropertyTableModel propertyTableModel = (PropertyTableModel) TableModelWrapperUtils.getActualTableModel(tableModel, PropertyTableModel.class);

        int propertyIndex = TableModelWrapperUtils.getActualRowAt(tableModel, rowIndex, propertyTableModel);

        return propertyTableModel.getPropertyAt(propertyIndex);
    }
}
