/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Israel
 */
public class ToolTipHeader extends JTableHeader {

    private HashMap tooltips = new HashMap();

    public ToolTipHeader(TableColumnModel model) {
        super(model);
    }

    public String getToolTipText(MouseEvent evt) {
        int column = columnAtPoint(evt.getPoint());
        String tooltip = (String) tooltips.get(new Integer(column));
        return tooltip;
    }

    public void addToolTipToColumn(int column, String tooltip) {
        tooltips.put(new Integer(column), tooltip);
    }
}
