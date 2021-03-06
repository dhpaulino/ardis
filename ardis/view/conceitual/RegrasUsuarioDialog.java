/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual;

import ardis.model.conversao.RegraUsuario;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Davisson
 */
public class RegrasUsuarioDialog extends javax.swing.JDialog {

    private RegraUsuario regra;
    private Map<Object, AbstractButton> opcoesButtons;

    public RegrasUsuarioDialog(java.awt.Frame parent, boolean modal) {
        super(parent, "Preferências de conversão", modal);
        //initComponents();

        opcoesButtons = new HashMap<>();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


        optionsPanel = new JPanel(new GridLayout(0, 1));
        add(optionsPanel, BorderLayout.LINE_START);


        setSize(700, 200);

        setLocationRelativeTo(parent);
    }

    public void render(RegraUsuario regraUsuario) {

        opcoesButtons.clear();
        optionsPanel.removeAll();
        descricaoLabel = new JLabel();
        optionsPanel.add(descricaoLabel, BorderLayout.LINE_START);
        perguntaLabel = new JLabel();
        optionsPanel.add(perguntaLabel, BorderLayout.BEFORE_LINE_BEGINS);

        this.regra = regraUsuario;
        putDados(regraUsuario);

        proximoBt = new JButton("Confirmar");

        optionsPanel.add(proximoBt, BorderLayout.LINE_END);

        proximoBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRegra();
                dispose();
            }
        });

        setVisible(true);
    }

    private void putDados(RegraUsuario regraUsuario) {

        descricaoLabel.setText(regraUsuario.getDescricao());
        perguntaLabel.setText(regraUsuario.getPegunta());

        generateOptions(regraUsuario.getOpcoes());
    }

    private void generateOptions(Object[] opcoes) {



        opcoesBG = new ButtonGroup();


        for (Object opcao : opcoes) {

            AbstractButton button = null;

            if (regra.isMultiSelection()) {
                button = new JCheckBox(opcao.toString());
            } else {

                button = new JRadioButton(opcao.toString());
                opcoesBG.add(button);
            }



            optionsPanel.add(button);
            button.setVisible(true);
            button.setSelected(true);

            opcoesButtons.put(opcao, button);

        }


    }

    public JButton getProximoBt() {
        return proximoBt;
    }

    private void setRegra() {

        List opcoesSelecionadas = new ArrayList();

        int cont = 0;
        for (Map.Entry<Object, AbstractButton> opcaoRadio : opcoesButtons.entrySet()) {
            Object object = opcaoRadio.getKey();
            AbstractButton button = opcaoRadio.getValue();

            if (button.isSelected()) {

                opcoesSelecionadas.add(object);

            }

        }

        //Enum opcao = Enum.valueOf(regra.getOpcoes(), opcoesBG.getSelection().toString());

        regra.setOpcoesSelecionadas(opcoesSelecionadas);
    }

  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        opcoesBG = new javax.swing.ButtonGroup();
        contentPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        descricaoLabel = new javax.swing.JLabel();
        optionsPanel = new javax.swing.JPanel();
        perguntaLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        finalizarBt = new javax.swing.JButton();
        cancelarBt = new javax.swing.JButton();
        decidirPrograma = new javax.swing.JButton();
        anteriorBt = new javax.swing.JButton();
        proximoBt = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(descricaoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(descricaoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addComponent(perguntaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addComponent(perguntaLabel)
                .addGap(0, 163, Short.MAX_VALUE))
        );

        finalizarBt.setText("Finalizar");

        cancelarBt.setText("Cancelar");

        decidirPrograma.setText("Deixe o ARDIS decidir");

        anteriorBt.setText("<<");

        proximoBt.setText(">>");
        proximoBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proximoBtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addComponent(decidirPrograma)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelarBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(finalizarBt))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(anteriorBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(proximoBt)))
                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anteriorBt)
                    .addComponent(proximoBt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(finalizarBt)
                    .addComponent(cancelarBt)
                    .addComponent(decidirPrograma)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void proximoBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proximoBtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_proximoBtActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton anteriorBt;
    private javax.swing.JButton cancelarBt;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton decidirPrograma;
    private javax.swing.JLabel descricaoLabel;
    private javax.swing.JButton finalizarBt;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.ButtonGroup opcoesBG;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JLabel perguntaLabel;
    private javax.swing.JButton proximoBt;
    // End of variables declaration//GEN-END:variables
}
