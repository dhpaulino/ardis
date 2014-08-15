/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.Check;
import ardis.model.logico.constraint.Default;
import ardis.model.logico.constraint.NotNull;
import ardis.model.logico.constraint.PK;
import ardis.model.logico.constraint.Unique;
import ardis.model.logico.tabela.Tabela;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Israel
 */
public class TabelaTableModel extends AbstractTableModel{
    
    private List<Coluna> colunas;
    private Tabela tabela;
    private String[] nomesColunas = {"Nome","Tipo","Tamanho","Primary Key", "Not Null",
                                        "Unique","Auto Increment","Default","Check","Descrição"};
    private Object[][] columnData;
    private LogicoGraphComponent graphComponent;
    
    public TabelaTableModel()
    {
        colunas = new ArrayList<Coluna>();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        Coluna coluna = (Coluna)getValueAt(rowIndex, 10);
        graphComponent.updateColuna(aValue, coluna, tabela, columnIndex);
        updateDataModel();
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

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex >= 3 && columnIndex <=6){
            return Boolean.class;
        }else if(columnIndex != 1){
            return String.class;
        }
        return super.getColumnClass(columnIndex);
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
            columnData[i][0] = coluna.getNome();
            columnData[i][1] = coluna.getTipo().toString();
            columnData[i][2] = coluna.getTamanho();
            columnData[i][3] = tabela.getConstraintByColuna(coluna, PK.class) != null;
            columnData[i][4] = tabela.getConstraintByColuna(coluna, NotNull.class) != null;
            columnData[i][5] = tabela.getConstraintByColuna(coluna, Unique.class) != null;
            columnData[i][6] = coluna.isAutoIncrement();
            Default constraintDefault = tabela.getConstraintByColuna(coluna, Default.class);
            columnData[i][7] = constraintDefault != null ? constraintDefault.getValor() : "";
            Check check = tabela.getConstraintByColuna(coluna, Check.class);
            columnData[i][8] = check != null ? check.getRegra() : "";
            columnData[i][9] = coluna.getDescricao();
            columnData[i][10] = coluna;
        }
    }

    public Tabela getTabela() {
        return tabela;
    }
    
    
    public LogicoGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void setGraphComponent(LogicoGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }

    public void setTabela(Tabela tabela) {
        this.tabela = tabela;
        this.colunas = tabela.getColunas();
    }
}
