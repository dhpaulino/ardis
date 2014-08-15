/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.logico.coluna.Coluna;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Israel
 */
public class ColunasTableModel extends AbstractTableModel{
    
    private List<Coluna> colunas;
    private List<Coluna> colunasSelecionadas;
    private String[] nomesColunas = {"","Coluna"};
    private Object[][] columnData;

    public ColunasTableModel() {
        colunas = new ArrayList<Coluna>();
        colunasSelecionadas = new ArrayList<Coluna>();
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        Coluna coluna = (Coluna)getValueAt(rowIndex, 2);
        if(columnIndex == 0){
            if((Boolean)aValue){
                colunasSelecionadas.add(coluna);
            }else{
                colunasSelecionadas.remove(coluna);
            }
        }
        updateDataModel();
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return !(columnIndex == 1);
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0){
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }
    
    @Override
    public int getRowCount() {
        return colunas.size();
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
        columnData = new Object[colunas.size()][nomesColunas.length+1];
        for(int i = 0; i < colunas.size();i++)
        {
            Coluna coluna = colunas.get(i);
            columnData[i][0] = colunasSelecionadas.contains(coluna);
            columnData[i][1] = coluna.getNome();
            columnData[i][2] = coluna;
        }
    }
    
    public void setColunas(List<Coluna> colunas)
    {
        this.colunas = colunas;
    }

    public List<Coluna> getColunasSelecionadas() {
        return colunasSelecionadas;
    }

    public void setColunasSelecionadas(List<Coluna> colunasSelecionadas) {
        this.colunasSelecionadas.clear();
        this.colunasSelecionadas.addAll(colunasSelecionadas);
    }
}
