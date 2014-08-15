/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual.infoTable;

import ardis.model.Objeto;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Davisson
 */
public abstract class AbstractInfoTableModel extends AbstractTableModel{

    protected Objeto objeto;
    protected String[] nomesColunas;
    protected Object[][] columnData;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return nomesColunas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnData[rowIndex][columnIndex];
    }

    public void setObjeto(Objeto objeto) {
        this.objeto = objeto;
        update();
    }

    public abstract void update();

    public void clean() {
        columnData = new Object[1][nomesColunas.length];
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return this.nomesColunas[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}
