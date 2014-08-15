/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual.infoTable;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Davisson
 */
public abstract class AbstractInfoTable extends JTable {

    public AbstractInfoTable(TableModel dm) {
        super(dm);
    }

    public AbstractInfoTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        super.changeSelection(rowIndex, columnIndex, toggle, extend);

        if (editCellAt(rowIndex, columnIndex)) {
            Component editor = getEditorComponent();
            editor.requestFocusInWindow();

            if (editor instanceof JTextComponent) {
                ((JTextComponent) editor).selectAll();
            }
        }
    }

    @Override
    public AbstractInfoTableModel getModel() {
        return (AbstractInfoTableModel) super.getModel();
    }
}
