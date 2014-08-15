/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual.atributo.infoTable;

import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import ardis.model.logico.coluna.TipoAtributo;
import ardis.view.conceitual.infoTable.AbstractInfoTable;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableColumn;

/**
 *
 * @author Davisson
 */
public class AtributoInfoTable extends AbstractInfoTable {

    public AtributoInfoTable() {
        super(new AtributoInfoModel());

        initTipoColumn();
    }

    private void initTipoColumn() {
        TableColumn tipoColumn = this.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox(TipoAtributo.getSortedVaules());
        tipoColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }
}
