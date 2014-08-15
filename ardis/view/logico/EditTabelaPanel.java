/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.logico.ModeloLogico;
import ardis.model.logico.coluna.TipoAtributo;
import ardis.model.logico.indice.Indice;
import ardis.model.logico.indice.TipoIndice;
import ardis.model.logico.tabela.Tabela;
import ardis.view.JanelaPrincipal;
import ardis.view.ToolTipHeader;
import com.mxgraph.model.mxCell;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author Israel
 */
public class EditTabelaPanel extends javax.swing.JPanel implements WindowListener {

    private TabelaTableModel tabelaTableModel;
    private IndiceTableModel indiceTableModel;
    private JanelaPrincipal janelaPrincipal;
    private IndiceForm indiceForm;

    /**
     * Creates new form EditTabelaPanel
     */
    public EditTabelaPanel(JanelaPrincipal pai) {
        this.janelaPrincipal = pai;
        tabelaTableModel = new TabelaTableModel();
        indiceTableModel = new IndiceTableModel();
        indiceForm = new IndiceForm(janelaPrincipal, true);
        indiceForm.setLocationRelativeTo(janelaPrincipal);
        indiceForm.addWindowListener(this);
        indiceForm.setVisible(false);
        initComponents();
        configureTable();
        this.setVisible(false);
    }

    private void configureTable() {
      
        setComboBoxOnTable(tabelaColunas, 1, TipoAtributo.getSortedVaules());
        setFormattedTextOnTable(tabelaColunas);
        ToolTipHeader tooltipHeader = new ToolTipHeader(tabelaColunas.getColumnModel());
        tabelaColunas.setTableHeader(tooltipHeader);
        tooltipHeader.addToolTipToColumn(2, "<html>Tamanho da coluna.<br>"
                + "Caso a coluna seja Decimal ou Numeric, digite o número total de dígitos <br>"
                + "separado por vírgula pelo número de dígitos decimais.<br>"
                + "Ex: Para o tamanho 6,2 o número máximo aceito pela coluna seria 9999,99.<html>");
    }

    private void setFormattedTextOnTable(JTable table) {
        final JFormattedTextField formattedTextField = new JFormattedTextField();
        formattedTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                formattedTextField.selectAll();
            }
        });
        DefaultCellEditor editor = new DefaultCellEditor(formattedTextField);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnClass(i) == String.class) {
                table.getColumnModel().getColumn(i).setCellEditor(editor);
            }
        }
    }

    private void setComboBoxOnTable(JTable table, int column, Object[] values) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        JComboBox comboBox = new JComboBox(values);
        tableColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    public void showPanel(Tabela tabela) {
        showTabelaPanel(tabela);
        showIndicePanel(tabela);
        this.setVisible(true);
        JSplitPane splitPane = (JSplitPane) this.getParent();
        splitPane.setDividerLocation(500);
    }

    public void showTabelaPanel(Tabela tabela) {
        nomeTabelaTf.setText(tabela.getNome());
        tabelaTableModel.setGraphComponent((LogicoGraphComponent) janelaPrincipal.getModeloPanel().getGraphComponent());
        tabelaTableModel.setTabela(tabela);
        tabelaTableModel.updateDataModel();
    }

    public void showIndicePanel(Tabela tabela) {
        indiceTableModel.setIndices(tabela.getIndices());
        indiceTableModel.updateDataModel();
        indiceForm.setTabela(tabela);
        desabilitarBotoes();
    }

    public void hidePanel() {
        if (this.isShowing()) {
            nomeTabelaTf.setText("");
            this.setVisible(false);
        }
    }

    private Tabela getTabela() {
        LogicoGraphComponent graphComponent = (LogicoGraphComponent) janelaPrincipal.getModeloPanel().getGraphComponent();
        mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
        Tabela tabela = tabelaTableModel.getTabela();
        return tabela;
    }

    private void habilitarBotoes() {
        editarBt.setEnabled(true);
        excluirBt.setEnabled(true);
    }

    private void desabilitarBotoes() {
        editarBt.setEnabled(false);
        excluirBt.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabelaTP = new javax.swing.JTabbedPane();
        tabelaPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nomeTabelaTf = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaColunas = new javax.swing.JTable();
        indexPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelaIndices = new javax.swing.JTable();
        editarBt = new javax.swing.JButton();
        excluirBt = new javax.swing.JButton();
        novoBt = new javax.swing.JButton();

        jLabel1.setText("Nome da Tabela:");

        nomeTabelaTf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomeTabelaTfActionPerformed(evt);
            }
        });

        tabelaColunas.setModel(tabelaTableModel);
        jScrollPane1.setViewportView(tabelaColunas);

        javax.swing.GroupLayout tabelaPanelLayout = new javax.swing.GroupLayout(tabelaPanel);
        tabelaPanel.setLayout(tabelaPanelLayout);
        tabelaPanelLayout.setHorizontalGroup(
            tabelaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabelaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabelaPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nomeTabelaTf))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabelaPanelLayout.setVerticalGroup(
            tabelaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabelaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nomeTabelaTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabelaTP.addTab("Colunas", tabelaPanel);

        tabelaIndices.setModel(indiceTableModel);
        tabelaIndices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelaIndicesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabelaIndices);

        editarBt.setText("Editar");
        editarBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarBtActionPerformed(evt);
            }
        });

        excluirBt.setText("Excluir");
        excluirBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excluirBtActionPerformed(evt);
            }
        });

        novoBt.setText("Novo");
        novoBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novoBtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout indexPanelLayout = new javax.swing.GroupLayout(indexPanel);
        indexPanel.setLayout(indexPanelLayout);
        indexPanelLayout.setHorizontalGroup(
            indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(indexPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                    .addGroup(indexPanelLayout.createSequentialGroup()
                        .addComponent(novoBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editarBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(excluirBt)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        indexPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {editarBt, excluirBt, novoBt});

        indexPanelLayout.setVerticalGroup(
            indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(indexPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editarBt)
                    .addComponent(excluirBt)
                    .addComponent(novoBt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabelaTP.addTab("Índices", indexPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabelaTP)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabelaTP)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void excluirBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excluirBtActionPerformed
        int numLinha = tabelaIndices.getSelectedRow();
        Indice indice = (Indice) indiceTableModel.getValueAt(numLinha, 2);
        Tabela tabela = getTabela();
        int confirmar = JOptionPane.showConfirmDialog(janelaPrincipal, "Deseja excluir o índice " + indice.getNome() + " ?",
                "", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            tabela.getIndices().remove(indice);
            janelaPrincipal.getModeloPanel().getGraphComponent().newUndoableEdit();
        }
        desabilitarBotoes();
        indiceTableModel.updateDataModel();
    }//GEN-LAST:event_excluirBtActionPerformed

    private void novoBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_novoBtActionPerformed
        indiceForm.novo();
        indiceForm.setVisible(true);
    }//GEN-LAST:event_novoBtActionPerformed

    private void editarBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarBtActionPerformed
        int numLinha = tabelaIndices.getSelectedRow();
        Indice indice = (Indice) indiceTableModel.getValueAt(numLinha, 2);
        indiceForm.editar(indice);
        indiceForm.setVisible(true);
    }//GEN-LAST:event_editarBtActionPerformed

    private void nomeTabelaTfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomeTabelaTfActionPerformed
        LogicoGraphComponent graphComponent = (LogicoGraphComponent) janelaPrincipal.getModeloPanel().getGraphComponent();
        ModeloLogico modelo = (ModeloLogico) graphComponent.getModelo();
        boolean save = true;
        if (nomeTabelaTf.getText().trim().isEmpty()) {
            save = false;
            JOptionPane.showMessageDialog(janelaPrincipal, "O nome da tabela não pode estar vazio.", "", JOptionPane.ERROR_MESSAGE);
        } else {
            for (Tabela tabela : modelo.getObjetosByClass(Tabela.class)) {
                if (tabela.getNome().equals(nomeTabelaTf.getText())) {
                    save = false;
                    JOptionPane.showMessageDialog(janelaPrincipal, "Este nome já está sendo utilizado por outra tabela.",
                            "", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
        }
        if (save) {
            getTabela().setNome(nomeTabelaTf.getText());
            graphComponent.getGraph().refresh();
            graphComponent.newUndoableEdit();
        }
    }//GEN-LAST:event_nomeTabelaTfActionPerformed

    private void tabelaIndicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaIndicesMouseClicked
        int numLinha = tabelaIndices.getSelectedRow();
        if (numLinha >= 0 && ((Indice) indiceTableModel.getValueAt(numLinha, 2)).getTipo() != TipoIndice.PRIMARY) {
            habilitarBotoes();
        } else {
            desabilitarBotoes();
        }
    }//GEN-LAST:event_tabelaIndicesMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editarBt;
    private javax.swing.JButton excluirBt;
    private javax.swing.JPanel indexPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField nomeTabelaTf;
    private javax.swing.JButton novoBt;
    private javax.swing.JTable tabelaColunas;
    private javax.swing.JTable tabelaIndices;
    private javax.swing.JPanel tabelaPanel;
    private javax.swing.JTabbedPane tabelaTP;
    // End of variables declaration//GEN-END:variables

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (indiceForm.isSalvar()) {
            getTabela().getIndices().add(indiceForm.getIndice());
            janelaPrincipal.getModeloPanel().getGraphComponent().newUndoableEdit();
        }
        showIndicePanel(getTabela());
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
