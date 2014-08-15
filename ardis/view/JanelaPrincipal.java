/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.view.ModelosTabbedPane.ModelosTabbedPane;
import ardis.view.conceitual.ObjetosConceitualMenu;
import ardis.control.conversao.LogicoToDicionarioDados;
import ardis.control.conversao.LogicoToSql;
import ardis.control.impressao.Impressao;
import ardis.view.logico.ObjetosLogicoMenu;
import ardis.view.conceitual.ConceitualPanel;
import ardis.control.modelo.ModeloFacade;
import ardis.model.Modelo;
import ardis.model.Objeto;
import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.atributo.multivalorado.AtributoMultivalorado;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conversao.RegraUsuario;
import ardis.model.logico.ModeloLogico;
import ardis.view.ModelosTabbedPane.CloseButton;
import ardis.view.ModelosTabbedPane.ModeloIcon;
import ardis.view.conceitual.ConceitualGraph;
import ardis.view.conceitual.ConceitualGraphComponent;
import ardis.view.conceitual.DetalhesPanel;
import ardis.view.conceitual.LogicoToConceitualRender;
import ardis.view.conceitual.RegrasUsuarioDialog;
import ardis.view.logico.EditTabelaPanel;
import ardis.view.logico.LogicoGraph;
import ardis.view.logico.LogicoGraphComponent;
import ardis.view.logico.LogicoPanel;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.view.mxGraph;
import java.awt.AWTException;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.w3c.dom.Document;

/**
 *
 * @author Davisson
 */
public class JanelaPrincipal extends javax.swing.JFrame {

    private ModeloFacade controllerModelo;
    private ObjetosConceitualMenu objetosConceitualMenu;
    private ObjetosLogicoMenu objetosLogicoMenu;
    private EditTabelaPanel editTabelaPanel;
    private DetalhesPanel detalhesPanel;
    private Modelo modeloAberto;
    private RegrasUsuarioDialog regrasUsuarioDialog;
    private mxGraphOutline birdsEye;
    private SobreDialog sobre;

    public JanelaPrincipal() {
        super("ARDIS");

        Icon icon = new ModeloIcon("A");
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(this, bufferedImage.getGraphics(), 0, 0);
        setIconImage(bufferedImage);
        initComponents();
        sobre = new SobreDialog(this, false);
        sobre.setVisible(false);
        rightSplitPanel.getBottomComponent().setVisible(false);
        birdsEye = new mxGraphOutline(null);
        birdsEye.setZoomHandleVisible(false);
        leftSplitPanel.setBottomComponent(birdsEye);
        leftSplitPanel.getBottomComponent().setVisible(false);
        initObjetosMenu();

        controllerModelo = new ModeloFacade();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int count = rightPanel.getTabCount();
                for (int i = 0; i < count; i++) {
                    int index = count == rightPanel.getTabCount() ? i : i - (count - rightPanel.getTabCount());
                    CloseButton closeButton = (CloseButton) rightPanel.getTabComponentAt(index);
                    closeButton.getButton().doClick();
                }
                if (rightPanel.getTabCount() == 0) {
                    System.exit(0);
                }
            }
        });
    }

    private void initObjetosMenu() {
        objetosConceitualMenu = new ObjetosConceitualMenu();
        objetosLogicoMenu = new ObjetosLogicoMenu();
        objetosPanel.add("conceitual", objetosConceitualMenu);
        objetosPanel.add("logico", objetosLogicoMenu);
        objetosPanel.add("blank", new JPanel());
        ((CardLayout) objetosPanel.getLayout()).show(objetosPanel, "blank");
    }

    public ConceitualGraphComponent addModeloConceitual() {
        Graph graph = new ConceitualGraph();
        ConceitualGraphComponent graphComponent = new ConceitualGraphComponent(graph);
        graphComponent.getModelo().setNome("MODELO_CONCEITUAL_" + (getGraphCount(ConceitualGraphComponent.class) + 1));
        graph.setGraphComponent(graphComponent);
        newConceitualTab(graphComponent);
        return graphComponent;
    }

    private void newConceitualTab(ConceitualGraphComponent graphComponent) {
        rightPanel.addTab(graphComponent.getModelo().getNome(), new ConceitualPanel(graphComponent));
        rightPanel.setTabComponentAt(rightPanel.getTabCount() - 1, new CloseButton(rightPanel, this, new ModeloIcon("C")));
        getDetalhesPanel();

        if (graphComponent.getModelo().getPath() != null && !graphComponent.getModelo().getPath().isEmpty()) {
            rightPanel.setToolTipTextAt(rightPanel.getTabCount() - 1, graphComponent.getModelo().getPath());
        }
    }

    private ConceitualGraphComponent addModeloConceitual(ModeloConceitual modeloConceitual) {
        if (modeloConceitual.getNome() == null || modeloConceitual.getNome().isEmpty()) {
            modeloConceitual.setNome("MODELO_CONCEITUAL_" + (getGraphCount(ConceitualGraphComponent.class) + 1));
        }
        Graph graph = new ConceitualGraph();
        ConceitualGraphComponent graphComponent = new ConceitualGraphComponent(graph, modeloConceitual);
        graph.setGraphComponent(graphComponent);
        newConceitualTab(graphComponent);
        return graphComponent;
    }

    private LogicoGraphComponent addModeloLogico() {
        Graph graph = new LogicoGraph();
        LogicoGraphComponent graphComponent = new LogicoGraphComponent(graph, this);
        graphComponent.getModelo().setNome("MODELO_LOGICO_" + (getGraphCount(LogicoGraphComponent.class) + 1));
        graph.setGraphComponent(graphComponent);
        newLogicoTab(graphComponent);

        return graphComponent;
    }

    private LogicoGraphComponent addModeloLogico(ModeloLogico modeloLogico) {
        if (modeloLogico.getNome() == null || modeloLogico.getNome().isEmpty()) {
            modeloLogico.setNome("MODELO_LOGICO_" + (getGraphCount(LogicoGraphComponent.class) + 1));
        }
        Graph graph = new LogicoGraph();
        LogicoGraphComponent graphComponent = new LogicoGraphComponent(graph, this, modeloLogico);
        graph.setGraphComponent(graphComponent);
        newLogicoTab(graphComponent);

        return graphComponent;
    }

    private LogicoGraphComponent addModeloLogico(ModeloLogico modeloLogico, HashMap<Object, Point> positions) {
        if (modeloLogico.getNome() == null || modeloLogico.getNome().isEmpty()) {
            modeloLogico.setNome("MODELO_LOGICO_" + (getGraphCount(LogicoGraphComponent.class) + 1));
        }
        Graph graph = new LogicoGraph();
        LogicoGraphComponent graphComponent = new LogicoGraphComponent(graph, this, modeloLogico, positions);
        graph.setGraphComponent(graphComponent);
        newLogicoTab(graphComponent);

        return graphComponent;
    }

    private int getGraphCount(Class graphType) {
        int count = 0;
        for (int i = 0; i < rightPanel.getTabCount(); i++) {
            if (((ModeloPanel) rightPanel.getComponentAt(i)).getGraphComponent().getClass().isAssignableFrom(graphType)) {
                count++;
            }
        }
        return count;
    }

    private void newLogicoTab(LogicoGraphComponent graphComponent) {
        rightPanel.addTab(graphComponent.getModelo().getNome(), new LogicoPanel(graphComponent));
        rightPanel.setTabComponentAt(rightPanel.getTabCount() - 1, new CloseButton(rightPanel, this, new ModeloIcon("L")));
        getEditTabelaPanel();

        if (graphComponent.getModelo().getPath() != null && !graphComponent.getModelo().getPath().isEmpty()) {
            rightPanel.setToolTipTextAt(rightPanel.getTabCount() - 1, graphComponent.getModelo().getPath());
        }
    }

    public ModeloPanel getModeloPanel() {
        return (ModeloPanel) rightPanel.getSelectedComponent();
    }

    public EditTabelaPanel getEditTabelaPanel() {
        if (editTabelaPanel == null) {
            editTabelaPanel = new EditTabelaPanel(this);
        }
        if (!rightSplitPanel.getBottomComponent().equals(editTabelaPanel)) {
            rightSplitPanel.setBottomComponent(editTabelaPanel);
        }
        return editTabelaPanel;
    }

    public DetalhesPanel getDetalhesPanel() {
        if (detalhesPanel == null) {
            detalhesPanel = new DetalhesPanel();
        }
        return detalhesPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        mainPanel = new javax.swing.JSplitPane();
        rightSplitPanel = new javax.swing.JSplitPane();
        rightPanel = new ModelosTabbedPane();
        leftSplitPanel = new javax.swing.JSplitPane();
        objetosPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        arquivoMenu = new javax.swing.JMenu();
        novoConceitualMI = new javax.swing.JMenuItem();
        novoLogicoMI = new javax.swing.JMenuItem();
        abrirMI = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        salvarMI = new javax.swing.JMenuItem();
        salvarComoMI = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exportarMI = new javax.swing.JMenuItem();
        imprimirMI = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        sairMI = new javax.swing.JMenuItem();
        editarMI = new javax.swing.JMenu();
        desfazerMI = new javax.swing.JMenuItem();
        refazerMI = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        copiarMI = new javax.swing.JMenuItem();
        colarMI = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        excluirMI = new javax.swing.JMenuItem();
        ferramentasMI = new javax.swing.JMenu();
        converterMI = new javax.swing.JMenuItem();
        organizarMI = new javax.swing.JMenuItem();
        dicionarioMI = new javax.swing.JMenuItem();
        sqlMI = new javax.swing.JMenuItem();
        ajudaMI = new javax.swing.JMenu();
        sobreMI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setDividerLocation(153);
        mainPanel.setDividerSize(8);
        mainPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        mainPanel.setOneTouchExpandable(true);

        rightSplitPanel.setDividerLocation(500);
        rightSplitPanel.setDividerSize(8);
        rightSplitPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        rightSplitPanel.setOneTouchExpandable(true);

        rightPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rightPanelStateChanged(evt);
            }
        });
        rightSplitPanel.setTopComponent(rightPanel);

        mainPanel.setRightComponent(rightSplitPanel);

        leftSplitPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        objetosPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Elementos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP)));
        objetosPanel.setLayout(new java.awt.CardLayout());
        leftSplitPanel.setLeftComponent(objetosPanel);

        mainPanel.setLeftComponent(leftSplitPanel);

        arquivoMenu.setText("Arquivo");

        novoConceitualMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        novoConceitualMI.setText("Novo Modelo Conceitual...");
        novoConceitualMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novoConceitualMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(novoConceitualMI);

        novoLogicoMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        novoLogicoMI.setText("Novo Modelo Lógico...");
        novoLogicoMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                novoLogicoMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(novoLogicoMI);

        abrirMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        abrirMI.setText("Abrir...");
        abrirMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(abrirMI);
        arquivoMenu.add(jSeparator1);

        salvarMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        salvarMI.setText("Salvar");
        salvarMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salvarMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(salvarMI);

        salvarComoMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        salvarComoMI.setText("Salvar Como...");
        salvarComoMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salvarComoMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(salvarComoMI);
        arquivoMenu.add(jSeparator2);

        exportarMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        exportarMI.setText("Exportar...");
        exportarMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportarMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(exportarMI);

        imprimirMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        imprimirMI.setText("Imprimir...");
        imprimirMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imprimirMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(imprimirMI);
        arquivoMenu.add(jSeparator3);

        sairMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        sairMI.setText("Sair");
        sairMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairMIActionPerformed(evt);
            }
        });
        arquivoMenu.add(sairMI);

        menuBar.add(arquivoMenu);

        editarMI.setText("Editar");

        desfazerMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        desfazerMI.setText("Desfazer");
        desfazerMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desfazerMIActionPerformed(evt);
            }
        });
        editarMI.add(desfazerMI);

        refazerMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        refazerMI.setText("Refazer");
        refazerMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refazerMIActionPerformed(evt);
            }
        });
        editarMI.add(refazerMI);
        editarMI.add(jSeparator5);

        copiarMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copiarMI.setText("Copiar");
        copiarMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copiarMIActionPerformed(evt);
            }
        });
        editarMI.add(copiarMI);

        colarMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        colarMI.setText("Colar");
        colarMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colarMIActionPerformed(evt);
            }
        });
        editarMI.add(colarMI);
        editarMI.add(jSeparator6);

        excluirMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        excluirMI.setText("Excluir");
        excluirMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excluirMIActionPerformed(evt);
            }
        });
        editarMI.add(excluirMI);

        menuBar.add(editarMI);

        ferramentasMI.setText("Ferramentas");

        converterMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        converterMI.setText("Gerar Modelo Lógico");
        converterMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                converterMIActionPerformed(evt);
            }
        });
        ferramentasMI.add(converterMI);

        organizarMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        organizarMI.setText("Organizar");
        organizarMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                organizarMIActionPerformed(evt);
            }
        });
        ferramentasMI.add(organizarMI);

        dicionarioMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        dicionarioMI.setText("Gerar Dicionário de Dados");
        dicionarioMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dicionarioMIActionPerformed(evt);
            }
        });
        ferramentasMI.add(dicionarioMI);

        sqlMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        sqlMI.setText("Gerar SQL");
        sqlMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sqlMIActionPerformed(evt);
            }
        });
        ferramentasMI.add(sqlMI);

        menuBar.add(ferramentasMI);

        ajudaMI.setText("Ajuda");

        sobreMI.setText("Sobre");
        sobreMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sobreMIActionPerformed(evt);
            }
        });
        ajudaMI.add(sobreMI);

        menuBar.add(ajudaMI);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1336, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void novoConceitualMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_novoConceitualMIActionPerformed
        addModeloConceitual();
        int index = rightPanel.getTabCount() - 1;
        rightPanel.setSelectedIndex(index);
    }//GEN-LAST:event_novoConceitualMIActionPerformed

    private void novoLogicoMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_novoLogicoMIActionPerformed
        addModeloLogico();
        int index = rightPanel.getTabCount() - 1;
        rightPanel.setSelectedIndex(index);

    }//GEN-LAST:event_novoLogicoMIActionPerformed

    private void salvarComoMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salvarComoMIActionPerformed
        salvarComo(modeloAberto);
        getModeloPanel().getGraphComponent().setSalvo(true);
    }//GEN-LAST:event_salvarComoMIActionPerformed

    private void exportarMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportarMIActionPerformed
        boolean confirmar = false;
        fileChooser = new JFileChooser();
        
        fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getName().endsWith(".png") || f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "*.png";
                }
            });
        
        int i = fileChooser.showSaveDialog(this);

        if (i == JFileChooser.APPROVE_OPTION) {
            
            String caminho = fileChooser.getSelectedFile().getAbsolutePath();
                File arquivo = fileChooser.getSelectedFile();
                if (!caminho.contains(".png")) {
                    arquivo = new File(caminho + ".png");
                }
                if (arquivo.exists()) {
                    int confirmar1 = JOptionPane.showConfirmDialog(fileChooser, "O arquivo já existe, deseja sobrescrevê-lo?", "",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmar1 == JOptionPane.YES_OPTION) {
                        confirmar = true;
                    }
                } else {
                    confirmar = true;
                }
                if (confirmar) {
                mxGraphComponent graphComponent = getModeloPanel().getGraphComponent();

                graphComponent.setGridVisible(false);
                graphComponent.getGraphControl().setBackground(Color.white);
                graphComponent.getGraph().setSelectionCell(null);
                ((GraphComponent)graphComponent).setShowLine(false);
                if (graphComponent instanceof LogicoGraphComponent) {
                    ((LogicoGraph) graphComponent.getGraph()).setShowLine(false);
                } else if (graphComponent instanceof ConceitualGraphComponent) {
                    ((ConceitualGraph) graphComponent.getGraph()).setShowLine(false);
                }
                graphComponent.getGraph().refresh();
                Dimension d = graphComponent.getGraphControl().getSize();
                BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();
                graphComponent.setBackground(Color.WHITE);
                graphComponent.getGraphControl().paint(g);
                /*BufferedImage image = mxCellRenderer.createBufferedImage(graphComponent.getGraph(),
                 null, 1, Color.WHITE, graphComponent.isAntiAlias(), null,
                 graphComponent.getCanvas());*/

                mxPngEncodeParam param = mxPngEncodeParam
                        .getDefaultEncodeParam(image);

                graphComponent.setGridVisible(true);
                ((GraphComponent)graphComponent).setShowLine(true);
                if (graphComponent instanceof LogicoGraphComponent) {
                    ((LogicoGraph) graphComponent.getGraph()).setShowLine(true);
                }
                if (graphComponent instanceof ConceitualGraphComponent) {
                    ((ConceitualGraph) graphComponent.getGraph()).setShowLine(true);
                }

                try {
                    FileOutputStream outputStream;
                    outputStream = new FileOutputStream(fileChooser.getSelectedFile() + ".png");
                    mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
                            param);
                    encoder.encode(image);
                    outputStream.close();
                    int abrir = JOptionPane.showConfirmDialog(this, "Imagem exportada com sucesso!"
                            + "\nDeseja Abrir a Imagem?", "Imagem",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (abrir == JOptionPane.OK_OPTION) {
                        java.awt.Desktop.getDesktop().open(new File(fileChooser.getSelectedFile().getPath() + ".png"));
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Destino de exportação ou Nome do Arquivo Invalido!");
                } catch (IOException ex) {
                    Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Não foi possível Exportar a Imagem!");
                }
            }
        }

    }//GEN-LAST:event_exportarMIActionPerformed

    private void imprimirMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imprimirMIActionPerformed
        try {
            GraphComponent panelSelecionado = getModeloPanel().getGraphComponent();

            if (panelSelecionado != null) {

                if (panelSelecionado instanceof ConceitualGraphComponent) {
                    ConceitualGraphComponent graphComponent = (ConceitualGraphComponent) panelSelecionado;

                    ((ConceitualGraph) graphComponent.getGraph()).setShowLine(false);
                    ((ConceitualGraph) graphComponent.getGraph()).setShowCardinalidade(false);
                    graphComponent.getGraph().refresh();

                    Impressao print = new Impressao();
                    print.print(graphComponent);

                    ((ConceitualGraph) graphComponent.getGraph()).setShowLine(true);
                    ((ConceitualGraph) graphComponent.getGraph()).setShowCardinalidade(true);
                    ((ConceitualGraph) graphComponent.getGraph()).setMudaY(false);
                    graphComponent.getGraph().refresh();

                } else if (panelSelecionado instanceof LogicoGraphComponent) {
                    LogicoGraphComponent graphComponent = (LogicoGraphComponent) panelSelecionado;

                    ((LogicoGraph) graphComponent.getGraph()).setShowLine(false);
                    ((LogicoGraph) graphComponent.getGraph()).setShowCardinalidade(false);
                    graphComponent.getGraph().refresh();

                    Impressao print = new Impressao();
                    print.print(graphComponent);

                    ((LogicoGraph) graphComponent.getGraph()).setShowLine(true);
                    ((LogicoGraph) graphComponent.getGraph()).setShowCardinalidade(true);
                    ((LogicoGraph) graphComponent.getGraph()).setMudaY(false);
                    graphComponent.getGraph().refresh();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um Diagrama");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Não foi possível Imprimir o Diagrama");
        }

    }//GEN-LAST:event_imprimirMIActionPerformed

    private void converterMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_converterMIActionPerformed
        if (getModeloPanel() instanceof LogicoPanel) {
            modeloAberto = getModeloPanel().getGraphComponent().getModelo();
            ModeloConceitual modeloConceitual = controllerModelo.gerarConceitual((ModeloLogico) modeloAberto);
            LogicoToConceitualRender render =
                    new LogicoToConceitualRender(addModeloConceitual(modeloConceitual), (LogicoGraph) getModeloPanel().getGraphComponent().getGraph());
            render.mostrarModelo(modeloConceitual, (ModeloLogico) modeloAberto);

        } else {

            List<RegraUsuario> regrasUsuario =
                    controllerModelo.analisarConceitual((ConceitualGraph) getModeloPanel().getGraphComponent().getGraph());

            if (!regrasUsuario.isEmpty()) {
                regrasUsuarioDialog = new RegrasUsuarioDialog(this, true);


                for (Iterator<RegraUsuario> it = regrasUsuario.iterator(); it.hasNext();) {
                    RegraUsuario regraUsuario = it.next();

                    regrasUsuarioDialog.render(regraUsuario);

                }

            }

            ModeloLogico modeloLogico = controllerModelo.
                    gerarLogico((ModeloConceitual) modeloAberto, regrasUsuario);

            List<Objeto> objetosSemPk = controllerModelo.getConceitualToLogico().getObjetosSemPk();

            if (objetosSemPk.size() > 0) {


                StringBuilder msg = new StringBuilder();

                msg.append("Os seguintes objetos não puderam ser convertidos,")
                        .append("pois existêm entidades sem chave primária")
                        .append("\n no modelo:");

                int contador = 0;
                for (Objeto objeto : objetosSemPk) {
                    contador++;
                    msg.append("\n");
                    msg.append(contador).append(".");
                    if (objeto instanceof Especializacao) {

                        Especializacao especializacao = ((Especializacao) objeto);
                        msg.append("A especialização \"").append(objeto)
                                .append("\", partindo da entidade \"")
                                .append(especializacao.getEntidadePai())
                                .append("\", sendo especializada em :");
                        msg.append("\n");
                        for (Object entidade : especializacao.getEntidadesFihas()) {

                            msg.append(entidade);
                            if (especializacao.getEntidadesFihas().indexOf(entidade) + 1
                                    != especializacao.getEntidadesFihas().size()) {
                                msg.append(",");

                            }
                        }
                    } else if (objeto instanceof Relacionamento) {

                        Relacionamento relacionamento = ((Relacionamento) objeto);
                        msg.append("O relacionamento \"").append(objeto)
                                .append("\", entre as entidades:");
                        msg.append("\n");
                        for (MembroRelacionamento membro : relacionamento.getMembros()) {
                            msg.append(membro.getEntidade());

                            if (relacionamento.getMembros().indexOf(membro) + 1
                                    != relacionamento.getMembros().size()) {
                                msg.append(",");

                            }

                        }
                    } else if (objeto instanceof AtributoMultivalorado) {
                        //TODO:Mostrar de qual entidade o atributo multivalorado és
                        msg.append("O atributo multivalorado \"").append(objeto.getNome())
                                .append("\"");
                    }
                }

                JOptionPane.showMessageDialog(this, msg, "Problemas na conversão", JOptionPane.WARNING_MESSAGE);
            }

            LogicoGraphComponent logicoGraphComponent = addModeloLogico(modeloLogico, controllerModelo.getConceitualToLogico()
                    .getPositions());
        }
        int index = rightPanel.getTabCount() - 1;
        rightPanel.setSelectedIndex(index);

    }//GEN-LAST:event_converterMIActionPerformed

    private void rightPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rightPanelStateChanged
        ModeloPanel panelSelecionado = getModeloPanel();

        if (panelSelecionado != null) {
            habilitarMenus();
            rightSplitPanel.getBottomComponent().setVisible(false);
            birdsEye.setGraphComponent(panelSelecionado.getGraphComponent());
            birdsEye.repaintTripleBuffer(null);
            birdsEye.updateFinder(true);
            leftSplitPanel.getBottomComponent().setVisible(true);
            leftSplitPanel.resetToPreferredSizes();

            if (panelSelecionado instanceof ConceitualPanel) {
                objetosConceitualMenu.setConceitualPanel((ConceitualGraphComponent) panelSelecionado.getGraphComponent());
                ((CardLayout) objetosPanel.getLayout()).show(objetosPanel, "conceitual");
                rightPanel.setVisible(true);
                dicionarioMI.setEnabled(false);
                sqlMI.setEnabled(false);
                converterMI.setText("Converter para Lógico");
                modeloAberto = panelSelecionado.getGraphComponent().getModelo();
            } else if (panelSelecionado instanceof LogicoPanel) {
                objetosLogicoMenu.setGraphComponent((LogicoGraphComponent) panelSelecionado.getGraphComponent());
                ((CardLayout) objetosPanel.getLayout()).show(objetosPanel, "logico");
                detalhesPanel.setVisible(false);
                converterMI.setText("Converter para Conceitual");
                modeloAberto = panelSelecionado.getGraphComponent().getModelo();
            }
            panelSelecionado.focusGained();
        } else {
            ((CardLayout) objetosPanel.getLayout()).show(objetosPanel, "blank");
            desabilitarMenus();
            leftSplitPanel.getBottomComponent().setVisible(false);
        }


    }//GEN-LAST:event_rightPanelStateChanged

    private void salvarMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salvarMIActionPerformed
        if (modeloAberto.getPath() != null) {
            controllerModelo.salvar(modeloAberto, getModeloPanel().getGraphComponent().getGraph());
        } else {
            salvarComo(modeloAberto);
        }
        getModeloPanel().getGraphComponent().setSalvo(true);
    }//GEN-LAST:event_salvarMIActionPerformed

    private void abrirMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirMIActionPerformed
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.getName().endsWith(".ardis") || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "*.ardis";
            }
        });
        int i = fileChooser.showOpenDialog(this);

        if (i == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            int index = isModeloAlreadyOpen(file.getPath());
            if (index != -1) {
                rightPanel.setSelectedIndex(index);
            } else {
                try {
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String xml = "";
                    try {
                        xml += bufferedReader.readLine();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Erro ao abrir arquivo.", "",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                    Document document = mxXmlUtils.parseXml(xml);
                    mxCodec codec = new mxCodec(document);

                    mxCell defaultParent = (mxCell) codec.getObject("1");
                    modeloAberto = (Modelo) defaultParent.getValue();

                    GraphComponent graphComponent = modeloAberto instanceof ModeloConceitual
                            ? addModeloConceitual((ModeloConceitual) modeloAberto) : addModeloLogico((ModeloLogico) modeloAberto);
                    graphComponent.setSalvo(true);

                    codec.decode(document.getDocumentElement(), graphComponent.getGraph().getModel());
                    modeloAberto.setPath(file.getPath());
                    graphComponent.setModified(false);
                    rightPanel.setSelectedIndex(rightPanel.getTabCount() - 1);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "Arquivo não encontrado.", "",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }

        }
        fileChooser.resetChoosableFileFilters();
    }//GEN-LAST:event_abrirMIActionPerformed

    private void desfazerMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desfazerMIActionPerformed
        getModeloPanel().getGraphComponent().getUndoManager().undo();
    }//GEN-LAST:event_desfazerMIActionPerformed

    private void refazerMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refazerMIActionPerformed
        getModeloPanel().getGraphComponent().getUndoManager().redo();
    }//GEN-LAST:event_refazerMIActionPerformed

    private void excluirMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excluirMIActionPerformed
        mxGraph openGraph = getModeloPanel().getGraphComponent().getGraph();
        Object[] cellSelecteds = openGraph.getSelectionCells();
        openGraph.removeCells(cellSelecteds);
    }//GEN-LAST:event_excluirMIActionPerformed

    private void copiarMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copiarMIActionPerformed
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_C);
            robot.keyRelease(KeyEvent.VK_C);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_copiarMIActionPerformed

    private void colarMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colarMIActionPerformed
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_colarMIActionPerformed

    private void dicionarioMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dicionarioMIActionPerformed
        try {
            boolean confirmar = false;
            fileChooser = new JFileChooser();

            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getName().endsWith(".doc") || f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "*.doc";
                }
            });

            ModeloLogico modeloLogico = (ModeloLogico) getModeloPanel().getGraphComponent().getModelo();

            int i = fileChooser.showSaveDialog(this);

            if (i == JFileChooser.APPROVE_OPTION) {
                String caminho = fileChooser.getSelectedFile().getAbsolutePath();
                File arquivo = fileChooser.getSelectedFile();
                if (!caminho.contains(".doc")) {
                    arquivo = new File(caminho + ".doc");
                }
                if (arquivo.exists()) {
                    int confirmar1 = JOptionPane.showConfirmDialog(fileChooser, "O arquivo já existe, deseja sobrescrevê-lo?", "",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmar1 == JOptionPane.YES_OPTION) {
                        confirmar = true;
                    }
                } else {
                    confirmar = true;
                }
                if (confirmar) {
                    LogicoToDicionarioDados dicionario = new LogicoToDicionarioDados();
                    dicionario.setTabelas(modeloLogico.getTabelas());
                    dicionario.createDoc(fileChooser.getSelectedFile().getPath());

                    try {
                        int abrir = JOptionPane.showConfirmDialog(this, "Dicionário de Dados criado com sucesso!"
                                + "\nDeseja Abrir o Dicionario de Dados?", "Dicionario de Dados",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (abrir == JOptionPane.OK_OPTION) {
                            java.awt.Desktop.getDesktop().open(new File(fileChooser.getSelectedFile().getPath() + ".doc"));
                        }
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(null, "Não foi possível abrir o Dicionário de Dados");
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Local inválido para criar o Dicionário de Dados");
                }
            }
            fileChooser.resetChoosableFileFilters();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Não foi possível criar o Dicionário de Dados");
            fileChooser.resetChoosableFileFilters();
        }
    }//GEN-LAST:event_dicionarioMIActionPerformed

    private void sqlMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sqlMIActionPerformed
        try {
            boolean confirmar = false;
            fileChooser = new JFileChooser();

            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getName().endsWith(".sql") || f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "*.sql";
                }
            });

            ModeloLogico modeloLogico = (ModeloLogico) getModeloPanel().getGraphComponent().getModelo();

            int i = fileChooser.showSaveDialog(this);

            if (i == JFileChooser.APPROVE_OPTION) {
                String caminho = fileChooser.getSelectedFile().getAbsolutePath();
                File arquivo = fileChooser.getSelectedFile();
                if (!caminho.contains(".sql")) {
                    arquivo = new File(caminho + ".sql");
                }
                if (arquivo.exists()) {
                    int confirmar1 = JOptionPane.showConfirmDialog(fileChooser, "O arquivo já existe, deseja sobrescrevê-lo?", "",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmar1 == JOptionPane.YES_OPTION) {
                        confirmar = true;
                    }
                } else {
                    confirmar = true;
                }
                if (confirmar) {
                    LogicoToSql sql = new LogicoToSql();
                    sql.setTabelas(modeloLogico.getTabelas());
                    sql.createDoc(fileChooser.getSelectedFile().getPath());

                    try {
                        int abrir = JOptionPane.showConfirmDialog(this, "SQL criado com sucesso!"
                                + "\nDeseja Abrir o SQL?", "SQL",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (abrir == JOptionPane.OK_OPTION) {
                            java.awt.Desktop.getDesktop().open(new File(fileChooser.getSelectedFile().getPath() + ".sql"));
                        }
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(null, "Não foi possivel abrir o arquivo SQL");
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "O Scripts SQL não foi salvo! ");
                }
            }
            fileChooser.resetChoosableFileFilters();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Não foi possível criar o SQL");
            fileChooser.resetChoosableFileFilters();
        }
    }//GEN-LAST:event_sqlMIActionPerformed

    private void sairMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairMIActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_sairMIActionPerformed

    private void sobreMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sobreMIActionPerformed
        sobre.setVisible(true);
    }//GEN-LAST:event_sobreMIActionPerformed

    private void organizarMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_organizarMIActionPerformed
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_O);
            robot.keyRelease(KeyEvent.VK_O);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_organizarMIActionPerformed

    private void desabilitarMenus() {
        dicionarioMI.setEnabled(false);
        sqlMI.setEnabled(false);
        converterMI.setEnabled(false);
        copiarMI.setEnabled(false);
        colarMI.setEnabled(false);
        desfazerMI.setEnabled(false);
        refazerMI.setEnabled(false);
        excluirMI.setEnabled(false);
        salvarMI.setEnabled(false);
        salvarComoMI.setEnabled(false);
        exportarMI.setEnabled(false);
        imprimirMI.setEnabled(false);
        organizarMI.setEnabled(false);
    }

    private void habilitarMenus() {
        dicionarioMI.setEnabled(true);
        sqlMI.setEnabled(true);
        converterMI.setEnabled(true);
        copiarMI.setEnabled(true);
        colarMI.setEnabled(true);
        desfazerMI.setEnabled(true);
        refazerMI.setEnabled(true);
        excluirMI.setEnabled(true);
        salvarMI.setEnabled(true);
        salvarComoMI.setEnabled(true);
        exportarMI.setEnabled(true);
        imprimirMI.setEnabled(true);
        organizarMI.setEnabled(true);
    }

    private int isModeloAlreadyOpen(String path) {
        for (int i = 0; i < rightPanel.getTabCount(); i++) {
            GraphComponent graphComponent = (GraphComponent) ((ModeloPanel) rightPanel.getComponentAt(i)).getGraphComponent();
            if (graphComponent.isSalvo() && String.valueOf(graphComponent.getModelo().getPath()).equals(path)) {
                return i;
            }
        }
        return -1;
    }


    public boolean salvarComo(Modelo modelo) {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.getName().endsWith(".ardis") || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "*.ardis";
            }
        });
        if (modelo.getPath() != null) {
            fileChooser.setSelectedFile(new File(modelo.getPath()));
        }
        int i = fileChooser.showSaveDialog(this);

        if (i == JFileChooser.APPROVE_OPTION) {
            String caminho = fileChooser.getSelectedFile().getAbsolutePath();
            File arquivo = fileChooser.getSelectedFile();
            modelo.setNome(arquivo.getName());
            if (!caminho.contains(".ardis")) {
                arquivo = new File(caminho + ".ardis");
            }
            if (arquivo.exists()) {
                int confirmar = JOptionPane.showConfirmDialog(fileChooser, "O arquivo já existe, deseja sobrescrevê-lo?", "",
                        JOptionPane.YES_NO_OPTION);
                if (confirmar != JOptionPane.YES_OPTION) {
                    return salvarComo(modelo);
                }
            }

            modelo.setPath(arquivo.getAbsolutePath());
            controllerModelo.salvar(getModeloPanel().getGraphComponent().getGraph(), modelo, arquivo);

            rightPanel.setTitleAt(rightPanel.getSelectedIndex(), modelo.getNome());
            rightPanel.setToolTipTextAt(rightPanel.getSelectedIndex(), modelo.getPath());
            return true;
        }
        return false;
    }
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem abrirMI;
    private javax.swing.JMenu ajudaMI;
    private javax.swing.JMenu arquivoMenu;
    private javax.swing.JMenuItem colarMI;
    private javax.swing.JMenuItem converterMI;
    private javax.swing.JMenuItem copiarMI;
    private javax.swing.JMenuItem desfazerMI;
    private javax.swing.JMenuItem dicionarioMI;
    private javax.swing.JMenu editarMI;
    private javax.swing.JMenuItem excluirMI;
    private javax.swing.JMenuItem exportarMI;
    private javax.swing.JMenu ferramentasMI;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenuItem imprimirMI;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JSplitPane leftSplitPanel;
    private javax.swing.JSplitPane mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem novoConceitualMI;
    private javax.swing.JMenuItem novoLogicoMI;
    private javax.swing.JPanel objetosPanel;
    private javax.swing.JMenuItem organizarMI;
    private javax.swing.JMenuItem refazerMI;
    private javax.swing.JTabbedPane rightPanel;
    private javax.swing.JSplitPane rightSplitPanel;
    private javax.swing.JMenuItem sairMI;
    private javax.swing.JMenuItem salvarComoMI;
    private javax.swing.JMenuItem salvarMI;
    private javax.swing.JMenuItem sobreMI;
    private javax.swing.JMenuItem sqlMI;
    // End of variables declaration//GEN-END:variables
}
