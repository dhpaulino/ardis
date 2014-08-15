/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual.relacionamento.cardinalidade.infoTable;

import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import ardis.view.conceitual.infoTable.AbstractInfoTableModel;

/**
 *
 * @author Davisson
 */
public class CardinalidadeInfoModel extends AbstractInfoTableModel {

    public CardinalidadeInfoModel() {

        nomesColunas = new String[]{
            "Cardinalidade",
            "Fraco",
            "Descrição"};

        columnData = new Object[1][nomesColunas.length];
    }

    @Override
    public void update() {

        columnData[0][0] = getObjeto().getCardinalidadeType();
        columnData[0][1] = getObjeto().isFraco() ? "Sim" : "Não";
        columnData[0][2] = getObjeto().getDescricao();
        fireTableDataChanged();
    }

    public MembroRelacionamento getObjeto() {
        return (MembroRelacionamento) objeto;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        if (objeto != null) {

            if (rowIndex == 0 && columnIndex == 0) {
                getObjeto().setCardinalidadeType((CardinalidadeType) aValue);
            } else if (rowIndex == 0 && columnIndex == 1) {
                getObjeto().setFraco(aValue.toString().toLowerCase().equals("sim"));
            } else if (rowIndex == 0 && columnIndex == 2) {
                objeto.setDescricao(aValue.toString());
            }
        }
        update();
    }
}
