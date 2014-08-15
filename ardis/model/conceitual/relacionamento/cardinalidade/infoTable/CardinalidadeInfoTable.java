/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual.relacionamento.cardinalidade.infoTable;

import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import ardis.view.conceitual.infoTable.AbstractInfoTable;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableColumn;

/**
 *
 * @author Davisson
 */
public class CardinalidadeInfoTable extends AbstractInfoTable {

    public CardinalidadeInfoTable() {
        super(new CardinalidadeInfoModel());

        initComboBox();

    }

    private void initComboBox() {
        TableColumn cardinalidadeTypeColumn = this.getColumnModel().getColumn(0);
        JComboBox comboBox = new JComboBox(CardinalidadeType.values());
        cardinalidadeTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));

        TableColumn fracoColumn = this.getColumnModel().getColumn(1);
        JComboBox comboBoxFraco = new JComboBox(new String[]{"Sim", "Não"});
        fracoColumn.setCellEditor(new DefaultCellEditor(comboBoxFraco));
    }
}
