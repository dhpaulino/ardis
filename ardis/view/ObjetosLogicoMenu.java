/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.view.logico.LogicoPanel;

/**
 *
 * @author Davisson
 */
public class ObjetosLogicoMenu extends javax.swing.JPanel {

    /**
     * Creates new form ObjetosLogicoMenu
     */
    
    private LogicoPanel logicoPanel;
    
    public ObjetosLogicoMenu() {
        initComponents();
    }

    public void setLogicoPanel(LogicoPanel logicoPanel) {
        this.logicoPanel = logicoPanel;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableaBt = new javax.swing.JButton();
        colunaBt = new javax.swing.JButton();
        relacionamento1to1 = new javax.swing.JButton();
        relacionamento1toN = new javax.swing.JButton();

        tableaBt.setText("Tabela");
        tableaBt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tableaBt.setContentAreaFilled(false);
        tableaBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableaBtActionPerformed(evt);
            }
        });

        colunaBt.setText("Coluna");
        colunaBt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        colunaBt.setContentAreaFilled(false);
        colunaBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colunaBtActionPerformed(evt);
            }
        });

        relacionamento1to1.setText("Relacionamento 1:1");
        relacionamento1to1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        relacionamento1to1.setContentAreaFilled(false);
        relacionamento1to1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relacionamento1to1ActionPerformed(evt);
            }
        });

        relacionamento1toN.setText("Relacionamento 1:N");
        relacionamento1toN.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        relacionamento1toN.setContentAreaFilled(false);
        relacionamento1toN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relacionamento1toNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableaBt, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(colunaBt, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(relacionamento1to1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(relacionamento1toN, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableaBt, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colunaBt, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(relacionamento1to1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(relacionamento1toN, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(163, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tableaBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableaBtActionPerformed
        

        logicoPanel.addTabela();
    }//GEN-LAST:event_tableaBtActionPerformed

    private void colunaBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colunaBtActionPerformed
        
        logicoPanel.addColuna();
    }//GEN-LAST:event_colunaBtActionPerformed

    private void relacionamento1to1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relacionamento1to1ActionPerformed

        logicoPanel.addRelacionamento1to1();
    }//GEN-LAST:event_relacionamento1to1ActionPerformed

    private void relacionamento1toNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relacionamento1toNActionPerformed
       
        logicoPanel.addRelacionamento1toN();
    }//GEN-LAST:event_relacionamento1toNActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton colunaBt;
    private javax.swing.JButton relacionamento1to1;
    private javax.swing.JButton relacionamento1toN;
    private javax.swing.JButton tableaBt;
    // End of variables declaration//GEN-END:variables
}