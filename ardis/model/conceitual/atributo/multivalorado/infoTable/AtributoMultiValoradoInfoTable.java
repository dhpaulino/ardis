/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual.atributo.multivalorado.infoTable;

import ardis.model.conceitual.atributo.multivalorado.AtributoMultivalorado;
import ardis.model.conceitual.atributo.infoTable.AtributoInfoModel;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.logico.coluna.TipoAtributo;
import ardis.view.conceitual.infoTable.AbstractInfoTable;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author Davisson
 */
public class AtributoMultiValoradoInfoTable extends AbstractInfoTable {

    public AtributoMultiValoradoInfoTable() {
        super(new AtributoMultivaloradoInfoModel());

        initComboBoxColumns();
    }

    private void initComboBoxColumns() {
        TableColumn tipoColumn = this.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox(TipoAtributo.values());
        tipoColumn.setCellEditor(new DefaultCellEditor(comboBox));

        TableColumn cardinalidadeColumn = this.getColumnModel().getColumn(2);
        JComboBox comboBoxCardinalidade = new JComboBox(AtributoMultivalorado.getCardinalidadeTypes());
        cardinalidadeColumn.setCellEditor(new DefaultCellEditor(comboBoxCardinalidade));
    }
}
