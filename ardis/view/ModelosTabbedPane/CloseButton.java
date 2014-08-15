/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.ModelosTabbedPane;

import ardis.control.modelo.ModeloFacade;
import ardis.view.GraphComponent;
import ardis.view.JanelaPrincipal;
import ardis.view.ModeloPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author Israel
 */
public class CloseButton extends JPanel {

    private final JTabbedPane pane;
    private JanelaPrincipal janelaPrincipal;
    private ModeloFacade modeloFacade;
    private JButton button;

    public CloseButton(final JTabbedPane pane, JanelaPrincipal janelaPrincipal, Icon icone) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.janelaPrincipal = janelaPrincipal;
        modeloFacade = new ModeloFacade();
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        setOpaque(false);

        //make JLabel read titles from JTabbedPane
        JLabel label = new JLabel() {
            public String getText() {
                int i = pane.indexOfTabComponent(CloseButton.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };
        label.setIcon(icone);
        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        //tab button
        button = new TabButton();
        button.setIcon(new CloseIcon());
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
    }

    public JButton getButton() {
        return button;
    }

    private class TabButton extends JButton implements ActionListener {

        public TabButton() {
            int size = 16;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Fechar modelo");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(CloseButton.this);
            if (i != -1) {
                GraphComponent graphComponent = ((ModeloPanel) pane.getComponentAt(i)).getGraphComponent();
                if ((!graphComponent.isSalvo() || graphComponent.isModified())
                        && !graphComponent.getModelo().getObjetos().isEmpty()) {
                    int confirmar = JOptionPane.showConfirmDialog(janelaPrincipal,
                            "Deseja salvar o modelo " + graphComponent.getModelo().getNome() + "?", "",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    if (confirmar == JOptionPane.YES_OPTION) {
                        if (!graphComponent.isSalvo()) {
                            if (janelaPrincipal.salvarComo(graphComponent.getModelo())) {
                                pane.remove(i);
                            }
                        } else {
                            modeloFacade.salvar(graphComponent.getModelo(), graphComponent.getGraph());
                            pane.remove(i);
                        }
                    } else if (confirmar == JOptionPane.NO_OPTION) {
                        pane.remove(i);
                    }
                } else {
                    pane.remove(i);
                }
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }
    }
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}
