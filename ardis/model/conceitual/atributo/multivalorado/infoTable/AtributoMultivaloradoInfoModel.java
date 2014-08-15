/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual.atributo.multivalorado.infoTable;

import ardis.model.conceitual.atributo.multivalorado.AtributoMultivalorado;
import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import ardis.model.logico.coluna.TipoAtributo;
import ardis.view.conceitual.infoTable.AbstractInfoTableModel;

/**
 *
 * @author Davisson
 */
public class AtributoMultivaloradoInfoModel extends AbstractInfoTableModel {

    public AtributoMultivaloradoInfoModel() {

        nomesColunas = new String[]{
            "Nome",
            "Tipo",
            "Cardinalidade",
            "Descrição"};

        columnData = new Object[1][nomesColunas.length];

    }

    public AtributoMultivalorado getObjeto() {
        return (AtributoMultivalorado) objeto;
    }

    public void update() {
        AtributoMultivalorado atributo = getObjeto();
        columnData[0][0] = atributo.getNome();
        columnData[0][1] = atributo.getTipo();
        columnData[0][2] = atributo.getCardinalidade();
        columnData[0][3] = atributo.getDescricao();
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        if (objeto != null) {

            if (rowIndex == 0 && columnIndex == 0) {
                objeto.setNome(aValue.toString());
            } else if (rowIndex == 0 && columnIndex == 1) {
                getObjeto().setTipo((TipoAtributo) aValue);
            } else if (rowIndex == 0 && columnIndex == 2) {
                getObjeto().setCardinalidade((CardinalidadeType) aValue);
            } else if (rowIndex == 0 && columnIndex == 3) {
                objeto.setDescricao(aValue.toString());
            }

        }
        update();
    }
}
