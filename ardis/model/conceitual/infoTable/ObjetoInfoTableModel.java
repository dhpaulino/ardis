/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual.infoTable;

import ardis.view.conceitual.infoTable.AbstractInfoTableModel;

/**
 *
 * @author Davisson
 */
public class ObjetoInfoTableModel extends AbstractInfoTableModel {

    public ObjetoInfoTableModel() {

        nomesColunas = new String[]{
            "Nome",
            "Descrição"};

        columnData = new Object[1][nomesColunas.length];

    }

    public void update() {
        columnData[0][0] = objeto.getNome();
        columnData[0][1] = objeto.getDescricao();
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        if (objeto != null) {

            if (rowIndex == 0 && columnIndex == 0) {
                objeto.setNome(aValue.toString());
            } else if (rowIndex == 0 && columnIndex == 1) {

                objeto.setDescricao(aValue.toString());
            }

        }
        update();
    }
}
