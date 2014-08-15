/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import ardis.view.JanelaPrincipal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Davisson
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JanelaPrincipal janelaPrincipal = new JanelaPrincipal();
            janelaPrincipal.setVisible(true);
            janelaPrincipal.setExtendedState(JFrame.MAXIMIZED_BOTH);

            janelaPrincipal.addModeloConceitual();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }
}
