/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.logico.indice.Indice;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Israel
 */
public class IndiceTableModel extends AbstractTableModel{

    private List<Indice> indices;
    private String[] nomesColunas = {"Nome","Tipo"};
    private Object[][] columnData;

    public IndiceTableModel() {
        indices = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return indices.size();
    }

    @Override
    public int getColumnCount() {
        return nomesColunas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnData[rowIndex][columnIndex];
    }
    
    public String getColumnName(int column)
    {
        return this.nomesColunas[column];
    }
    
    public void updateDataModel()
    {
        atualizaDados();
        fireTableDataChanged();
    }
    private void atualizaDados()
    {
        columnData = new Object[indices.size()][nomesColunas.length+1];
        for(int i = 0; i < indices.size();i++)
        {
            Indice indice = indices.get(i);
            columnData[i][0] = indice.getNome();
            columnData[i][1] = indice.getTipo().toString();
            columnData[i][2] = indice;
        }
    }

    public List<Indice> getIndices() {
        return indices;
    }

    public void setIndices(List<Indice> indices) {
        this.indices = indices;
    }
}
