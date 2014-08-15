/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.Modelo;
import ardis.model.Objeto;
import ardis.model.logico.ModeloLogico;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.coluna.TipoAtributo;
import ardis.model.logico.constraint.Check;
import ardis.model.logico.constraint.Constraint;
import ardis.model.logico.constraint.Default;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.constraint.NotNull;
import ardis.model.logico.constraint.PK;
import ardis.model.logico.constraint.Unique;
import ardis.model.logico.indice.Indice;
import ardis.model.logico.indice.TipoIndice;
import ardis.model.logico.tabela.Tabela;
import ardis.view.Graph;
import ardis.view.GraphComponent;
import ardis.view.JanelaPrincipal;
import ardis.view.SeparateEdgesLayout;
import com.mxgraph.io.mxCodec;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.w3c.dom.Document;

/**
 *
 * @author Israel
 */
public class LogicoGraphComponent extends GraphComponent {

    private JanelaPrincipal janelaPrincipal;
    private RelacionamentoHandler relacionamentoHandler;
    private MouseAdapter mouseAdapter;

    public LogicoGraphComponent(mxGraph graph, JanelaPrincipal janela) {
        super(graph);
        setModelo(createModelo());
        janelaPrincipal = janela;
    }

    public LogicoGraphComponent(mxGraph graph, JanelaPrincipal janela, ModeloLogico modeloLogico) {
        super(graph);
        setModelo(modeloLogico);
        janelaPrincipal = janela;
    }

    public LogicoGraphComponent(mxGraph graph, JanelaPrincipal janela, ModeloLogico modeloLogico, HashMap<Object, Point> positions) {
        super(graph);
        setModelo(modeloLogico);
        janelaPrincipal = janela;
        renderModeloLogico(positions);
    }

    @Override
    public Modelo createModelo() {
        return new ModeloLogico();
    }

    @Override
    protected void initObjects() {
        super.initObjects();
        relacionamentoHandler = new RelacionamentoHandler(this);
    }

    private void renderModeloLogico(HashMap<Object, Point> positions) {

        LogicoGraph logicoGraph = ((LogicoGraph) getGraph());
        logicoGraph.setGenerateCellName(false);
        Map<Tabela, mxCell> cellsTabelas = new HashMap<>();

        for (Iterator<Tabela> it = modelo.getObjetosByClass(Tabela.class).iterator(); it.hasNext();) {
            Tabela tabela = (Tabela) it.next();

            mxCell tabelaCell = null;
            for (Map.Entry<Object, Point> entry : positions.entrySet()) {
                Object object = entry.getKey();
                Point point = entry.getValue();

                if (object == tabela) {
                    tabelaCell = tabela.initialRender(graph, null, point.getX(), point.getY());
                }
            }


            cellsTabelas.put(tabela, tabelaCell);

            for (Coluna coluna : tabela.getColunas()) {

                drawColuna(coluna, tabelaCell);
            }
        }

        for (Map.Entry<Tabela, mxCell> tabelaCell : cellsTabelas.entrySet()) {
            Tabela tabela = tabelaCell.getKey();
            mxCell cell = tabelaCell.getValue();

            for (Constraint constraint : tabela.getConstraints()) {

                if (constraint instanceof FK) {

                    FK fk = (FK) constraint;

                    mxCell cellTabelaReferenciada = cellsTabelas.get(fk.getTabelaReferenciada());

                    fk.render(graph, cell, cellTabelaReferenciada);

                }
            }



        }
        
        mxOrganicLayout layout = new mxOrganicLayout(graph);
        
        graph.getModel().beginUpdate();
        try {
            layout.setDisableEdgeStyle(false);
            layout.setApproxNodeDimensions(false);
            layout.execute(graph.getDefaultParent());
        } finally {
            mxMorphing morph = new mxMorphing(this, 20, 1.2, 20);

            morph.addListener(mxEvent.DONE, new mxIEventListener() {
                @Override
                public void invoke(Object arg0, mxEventObject arg1) {
                    graph.getModel().endUpdate();
                    // fitViewport();
                }
            });

            morph.startAnimation();
        }
        SeparateEdgesLayout edgeLayout = new SeparateEdgesLayout(graph);
        edgeLayout.execute(graph.getDefaultParent());


        logicoGraph.setGenerateCellName(true);
    }

    protected void configureGraphComponent() {
        super.configureGraphComponent();
        // Loads the defalt stylesheet from an external file
        mxCodec codec = new mxCodec();
        Document doc = mxUtils.loadDocument(getClass().getResource(
                "/ardis/resources/relModel-style.xml")
                .toString());
        codec.decode(doc.getDocumentElement(), graph.getStylesheet());

        getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mxCell cell = (mxCell) getCellAt(e.getX(), e.getY(), false);
                if (cell == null) {
                    janelaPrincipal.getEditTabelaPanel().hidePanel();
                } else {
                    if (janelaPrincipal.getEditTabelaPanel().isShowing()) {
                        if (cell.getValue() instanceof Tabela) {
                            janelaPrincipal.getEditTabelaPanel().showPanel((Tabela) cell.getValue());
                        } else if (cell.getValue() instanceof Coluna) {
                            janelaPrincipal.getEditTabelaPanel().showPanel((Tabela) cell.getParent().getValue());
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void installDoubleClickHandler() {
        graphControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isConsumed() && isEditEvent(e)) {
                    mxCell celula = (mxCell) getCellAt(e.getX(), e.getY(), false);
                    if (celula != null) {
                        if (!getVerticalScrollBar().isShowing()) {
                            setScaledPreferredSizeForGraph(getSize());
                        }
                        if (celula.getValue() instanceof Tabela) {
                            janelaPrincipal.getEditTabelaPanel().showPanel((Tabela) celula.getValue());
                        } else if (celula.getValue() instanceof Coluna) {
                            janelaPrincipal.getEditTabelaPanel().showPanel((Tabela) celula.getParent().getValue());
                        }
                        getGraphControl().updatePreferredSize();
                    }
                }

            }
           
        });
    }

    public void addTabela(final Tabela tabela) {
        removerMouseListener();
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    graph.getModel().beginUpdate();
                    mxPoint point = getPointForEvent(e);
                    tabela.initialRender(graph, (mxCell) graph.getDefaultParent(), point.getX(), point.getY());


                    modelo.addObjeto(tabela);
                    removerMouseListener();
                } finally {
                    graph.getModel().endUpdate();
                }
            }
        };
        getGraphControl().addMouseListener(mouseAdapter);
    }

    public void addColuna(Coluna coluna) {
        removerMouseListener();
        mxCell cell = (mxCell) graph.getSelectionCell();
        if (cell != null && cell.getValue() instanceof Tabela) {
            Tabela tabela = (Tabela) cell.getValue();
            tabela.addColuna(coluna);
            LogicoGraph logicoGraph = ((LogicoGraph) getGraph());
            if (((Tabela) cell.getValue()).getConstraintByColuna(coluna, FK.class) != null) {
                logicoGraph.setGenerateCellName(false);
            }

            logicoGraph.setGenerateCellName(true);
            drawColuna(coluna, cell);
        } else {
            JOptionPane.showMessageDialog(janelaPrincipal, "Você precisa selecionar uma tabela primeiro.", "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void drawColuna(Coluna coluna, mxCell tabelaCell) {

        Tabela tabela = (Tabela) tabelaCell.getValue();
        try {
            graph.getModel().beginUpdate();
            coluna.render(graph, tabelaCell);


        } finally {
            graph.getModel().endUpdate();
        }
        if (janelaPrincipal.getEditTabelaPanel().isShowing()) {
            janelaPrincipal.getEditTabelaPanel().showPanel(tabela);
        }
    }

    public void drawRelacionamento(FK foreignKey, mxCell source, mxCell target) {

        LogicoGraph logicoGraph = ((LogicoGraph) getGraph());
        try {
            graph.getModel().beginUpdate();

            logicoGraph.setGenerateCellName(false);

            foreignKey.render(graph, source, target);
        } finally {
            graph.getModel().endUpdate();
        }

        Tabela tabelaTarget = (Tabela) target.getValue();
        for (Coluna coluna : foreignKey.getColunas()) {
            tabelaTarget.addColuna(coluna);
            drawColuna(coluna, target);
        }
        logicoGraph.setGenerateCellName(true);
    }

    public void addRelacionamento1N() {
        removerMouseListener();
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mxCell cell = (mxCell) getCellAt(e.getX(), e.getY(), false);
                if (cell != null) {
                    if (cell.getValue() instanceof Tabela || cell.getValue() instanceof Coluna) {
                        Tabela tabela = cell.getValue() instanceof Tabela
                                ? (Tabela) cell.getValue() : (Tabela) cell.getParent().getValue();
                        mxCell tabelaCell = cell.getValue() instanceof Tabela ? cell : (mxCell) cell.getParent();
                        if (relacionamentoHandler.getTabelaReferenciadaCell() == null) {

                            if (!tabela.getPrimaryKey().getColunas().isEmpty()) {
                                relacionamentoHandler.setTabelaReferenciadaCell(tabelaCell);
                            } else {
                                removerMouseListener();
                                JOptionPane.showMessageDialog(janelaPrincipal, "Selecione uma tabela que tenha Primary Key.", "",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                        } else if (relacionamentoHandler.getTabelaFKCell() == null && relacionamentoHandler.getTabelaReferenciadaCell() != null) {
                            relacionamentoHandler.setTabelaFKCell(tabelaCell);
                            relacionamentoHandler.createRelacionamento1N();
                            removerMouseListener();
                        }
                    } else {
                        JOptionPane.showMessageDialog(janelaPrincipal, "Selecione uma tabela.", "", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        getGraphControl().addMouseListener(mouseAdapter);
    }

    public void addRelacionamento11() {
        removerMouseListener();
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mxCell cell = (mxCell) getCellAt(e.getX(), e.getY(), false);
                if (cell != null) {
                    if (cell.getValue() instanceof Tabela || cell.getValue() instanceof Coluna) {
                        Tabela tabela = cell.getValue() instanceof Tabela
                                ? (Tabela) cell.getValue() : (Tabela) cell.getParent().getValue();
                        mxCell tabelaCell = cell.getValue() instanceof Tabela ? cell : (mxCell) cell.getParent();
                        if (relacionamentoHandler.getTabelaReferenciadaCell() == null) {
                            if (!tabela.getPrimaryKey().getColunas().isEmpty()) {
                                relacionamentoHandler.setTabelaReferenciadaCell(tabelaCell);
                            } else {
                                JOptionPane.showMessageDialog(janelaPrincipal, "Selecione uma tabela que tenha Primary Key.", "",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (relacionamentoHandler.getTabelaFKCell() == null && relacionamentoHandler.getTabelaReferenciadaCell() != null) {
                            relacionamentoHandler.setTabelaFKCell(tabelaCell);
                            relacionamentoHandler.createRelacionamento11();
                            removerMouseListener();
                        }
                    } else {
                        JOptionPane.showMessageDialog(janelaPrincipal, "Selecione uma tabela.", "", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        getGraphControl().addMouseListener(mouseAdapter);
    }

    private void removerMouseListener() {
        if (mouseAdapter != null) {
            getGraphControl().removeMouseListener(mouseAdapter);
        }
    }

    private boolean colunaNomeExiste(Tabela tabela, String nome) {
        for (Coluna col : tabela.getColunas()) {
            if (col.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    public void updateColuna(Object valor, Coluna coluna, Tabela tabela, int indexColuna) {
        boolean saveChange = true;
        if (indexColuna == 0) {
            if (colunaNomeExiste(tabela, valor.toString())) {
                saveChange = false;
                JOptionPane.showMessageDialog(janelaPrincipal, "Colunas de uma mesma tabela não podem ter o mesmo nome.", "",
                        JOptionPane.ERROR_MESSAGE);
            } else if (valor.toString().trim().isEmpty()) {
                saveChange = false;
                JOptionPane.showMessageDialog(janelaPrincipal, "O nome da coluna não pode ficar vazio.", "", JOptionPane.ERROR_MESSAGE);
            } else {
                coluna.setNome(valor.toString());
            }
        } else if (indexColuna == 1) {
            coluna.setTipo(TipoAtributo.valueOf(valor.toString()));
        } else if (indexColuna == 2) {
            if (coluna.getTipo() == TipoAtributo.DECIMAL || coluna.getTipo() == TipoAtributo.NUMERIC) {
                if (valor.toString().matches("^\\d+,\\d+$")) {
                    coluna.setTamanho(valor.toString());
                } else {
                    saveChange = false;
                    JOptionPane.showMessageDialog(janelaPrincipal,
                            "Digite apenas números inteiros separados por vírgula para o tamanho da coluna. Ex: 6,2", "",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (valor.toString().matches("^\\d+$")) {
                coluna.setTamanho(valor.toString());
            } else {
                saveChange = false;
                JOptionPane.showMessageDialog(janelaPrincipal, "Digite apenas números inteiros para o tamanho da coluna.", "",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else if (indexColuna == 3) {
            updatePK((Boolean) valor, coluna, tabela);
        } else if (indexColuna == 4) {
            updateNotNull((Boolean) valor, coluna, tabela);
        } else if (indexColuna == 5) {
            updateUnique((Boolean) valor, coluna, tabela);
        } else if (indexColuna == 6) {
            coluna.setAutoIncrement((Boolean) valor);
        } else if (indexColuna == 7) {
            updateDefault(valor.toString(), coluna, tabela);
        } else if (indexColuna == 8) {
            updateCheck(valor.toString(), coluna, tabela);
        } else if (indexColuna == 9) {
            coluna.setDescricao(valor.toString());
        }
        graph.refresh();

        if (saveChange) {
            newUndoableEdit();
        }
    }

    private void updateFK(Tabela tabela) {
        for (FK fk : modelo.getObjetosByClass(FK.class)) {
            if (fk.getTabelaReferenciada().equals(tabela)) {
                for (Coluna coluna : tabela.getPrimaryKey().getColunas()) {
                    if (!fk.getColunasComReferencias().containsValue(coluna)) {
                        Coluna colunaFk = new Coluna();
                        colunaFk.setNome(tabela.getNome() + "_" + coluna.getNome());
                        colunaFk.setTipo(coluna.getTipo());
                        colunaFk.setTamanho(coluna.getTamanho());
                        
                        mxCell fkCell = ((LogicoGraph) graph).getCellByValue(fk);
                        Tabela tabelaFk = (Tabela) fkCell.getTarget().getValue();
                        tabelaFk.addColuna(colunaFk);
                        findIndex(tabelaFk, fk.getColunas().get(0)).getColunas().add(colunaFk);
                        fk.getColunasComReferencias().put(colunaFk, coluna);
                        drawColuna(colunaFk, (mxCell) fkCell.getTarget());
                    }
                }
            }
        }
    }

    private Indice findIndex(Tabela tabela, Coluna coluna) {
        for (Indice indice : tabela.getIndices()) {
            if (indice.getColunas().contains(coluna)) {
                return indice;
            }
        }
        return null;
    }

    private void removePKReferences(Coluna colunaPK, Tabela tabela) {
        for (FK fk : modelo.getObjetosByClass(FK.class)) {
            if (fk.getTabelaReferenciada().equals(tabela)) {
                for (Map.Entry<Coluna, Coluna> entry : fk.getColunasComReferencias().entrySet()) {
                    if (entry.getValue().equals(colunaPK)) {
                        mxCell colunaFk = ((LogicoGraph) graph).getCellByValue(entry.getKey());
                        graph.removeCells(new Object[]{colunaFk});
                    }
                }
            }
        }
    }

    private boolean removeIndexes(Tabela tabela, List<Coluna> colunas, TipoIndice excecao) {
        Iterator<Indice> indicesIterator = tabela.getIndices().iterator();
        while (indicesIterator.hasNext()) {
            Indice indice = indicesIterator.next();
            if (indice.getColunas().equals(colunas) && indice.getTipo() != excecao) {
                indicesIterator.remove();
            } else if (indice.getTipo() == excecao) {
                return false;
            }
        }
        return true;
    }

    private void updatePK(boolean isPk, Coluna coluna, Tabela tabela) {
        PK pk = tabela.getPrimaryKey();
        if (isPk) {
            pk.addColuna(coluna);
            removeIndexes(tabela, pk.getColunas(), TipoIndice.PRIMARY);
            updateFK(tabela);
        } else {
            pk.getColunas().remove(coluna);
            pk.getIndice().getColunas().remove(coluna);
            removePKReferences(coluna, tabela);
            if (pk.getColunas().isEmpty()) {
                tabela.getIndices().remove(pk.getIndice());
                tabela.getConstraints().remove(pk);
            }
        }
        janelaPrincipal.getEditTabelaPanel().showIndicePanel(tabela);
    }

    private void updateNotNull(boolean isNotNull, Coluna coluna, Tabela tabela) {
        if (isNotNull) {
            NotNull constraint = new NotNull();
            constraint.setColuna(coluna);
            tabela.getConstraints().add(constraint);
        } else {
            NotNull notNull = tabela.getConstraintByColuna(coluna, NotNull.class);
            if (notNull != null) {
                tabela.getConstraints().remove(notNull);
            }
        }
    }

    private void updateUnique(boolean isUnique, Coluna coluna, Tabela tabela) {
        if (isUnique) {
            Unique constraint = new Unique();
            constraint.setColuna(coluna);
            tabela.getConstraints().add(constraint);

            if (removeIndexes(tabela, constraint.getColunas(), TipoIndice.PRIMARY)) {
                Indice indice = new Indice();
                indice.getColunas().add(coluna);
                indice.setTipo(TipoIndice.UNIQUE);
                tabela.getIndices().add(indice);
            }

            janelaPrincipal.getEditTabelaPanel().showIndicePanel(tabela);
        } else {
            Unique unique = tabela.getConstraintByColuna(coluna, Unique.class);
            if (unique != null) {
                tabela.getConstraints().remove(unique);
            }
        }
    }

    private void updateDefault(String defaultValue, Coluna coluna, Tabela tabela) {
        if (!defaultValue.isEmpty()) {
            Default constraint = new Default();
            constraint.setColuna(coluna);
            constraint.setValor(defaultValue);
            tabela.getConstraints().add(constraint);
        } else {
            Default constraintDefault = tabela.getConstraintByColuna(coluna, Default.class);
            if (constraintDefault != null) {
                tabela.getConstraints().remove(constraintDefault);
            }
        }
    }

    private void updateCheck(String checkValue, Coluna coluna, Tabela tabela) {
        if (!checkValue.isEmpty()) {
            Check constraint = new Check();
            constraint.setColuna(coluna);
            constraint.setRegra(checkValue);
            tabela.getConstraints().add(constraint);
        } else {
            Check check = tabela.getConstraintByColuna(coluna, Check.class);
            if (check != null) {
                tabela.getConstraints().remove(check);
            }
        }
    }

    public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {

        ((Graph) getGraph()).setGenerateCellName(false);
        List<Object> values = getValues(cells);
        List<HashMap<String, Object>> relacionamentos = new ArrayList<>();
        boolean allowImport = true;
        for (Object cell : cells) {
            mxCell celula = (mxCell) cell;
            if (celula.getValue() != null && celula.getValue() instanceof Objeto) {
                ((Objeto) celula.getValue()).setOriginalHash(celula.getValue().hashCode());
                if (celula.getValue() instanceof Tabela) {
                    Tabela tabela = (Tabela) celula.getValue();
                    tabela.setNome(tabela.getNome() + "_copia_" + (getQtdCopias(tabela.getNome(), ((ModeloLogico) modelo).getTabelas()) + 1));
                    List<FK> fks = tabela.getConstraintsByClass(FK.class);
                    for (FK foreignKey : fks) {
                        if (!(values.contains(foreignKey.getTabelaReferenciada()))) {
                            HashMap<String, Object> drawParams = new HashMap<>();
                            mxCell source = getCellByValueName(foreignKey.getTabelaReferenciada().getNome());
                            drawParams.put("fk", foreignKey);
                            drawParams.put("source", source);
                            drawParams.put("justEdge", false);
                            foreignKey.setTabelaReferenciada((Tabela) source.getValue());
                            int count = 0;
                            for (Map.Entry<Coluna, Coluna> entry : foreignKey.getColunasComReferencias().entrySet()) {
                                entry.setValue(foreignKey.getTabelaReferenciada().getPrimaryKey().getColunas().get(count));
                                count++;
                            }
                            relacionamentos.add(drawParams);
                        }
                    }
                    modelo.addObjeto(tabela);
                } else if (celula.getValue() instanceof FK) {
                    if (celula.getTarget() == null || celula.getSource() == null) {
                        cells = removeFromArray(cells, cell);
                    } else {
                        HashMap<String, Object> drawParams = new HashMap<>();
                        drawParams.put("fk", celula.getValue());
                        drawParams.put("source", celula.getSource());
                        drawParams.put("target", celula.getTarget());
                        drawParams.put("justEdge", true);
                        relacionamentos.add(drawParams);
                    }
                } else if (celula.getValue() instanceof Coluna) {
                    mxCell cellSelecionada = (mxCell) graph.getSelectionCell();
                    if (cellSelecionada != null
                            && (cellSelecionada.getValue() instanceof Tabela || cellSelecionada.getValue() instanceof Coluna)) {
                        cellSelecionada = cellSelecionada.getValue() instanceof Tabela
                                ? cellSelecionada : (mxCell) cellSelecionada.getParent();
                        Tabela pai = (Tabela) cellSelecionada.getValue();
                        Coluna coluna = (Coluna) celula.getValue();
                        if (colunaNomeExiste(pai, coluna.getNome())) {
                            coluna.setNome(coluna.getNome() + "_copia_" + (getQtdCopias(coluna.getNome(), pai.getColunas()) + 1));
                        }
                        pai.addColuna(coluna);
                        coluna.render(graph, cellSelecionada);

                    }
                    cells = removeFromArray(cells, cell);
                } else {
                    allowImport = false;
                }
            }
        }
        ((Graph) getGraph()).setGenerateCellName(true);
        if (allowImport) {
            Object[] importedCells = super.importCells(cells, dx, dy, target, location);
            afterImport(importedCells, relacionamentos);
            return importedCells;
        } else {
            return null;
        }
       
    }

    private void afterImport(Object[] importedCells, List<HashMap<String, Object>> relacionamentos) {
        for (HashMap<String, Object> drawParams : relacionamentos) {
            Object targetCell = null;
            Object sourceCell = null;
            if ((Boolean) drawParams.get("justEdge")) {
                for (Object importedCell : importedCells) {
                    if (graph.getModel().isVertex(importedCell)) {
                        Tabela tabelaImported = (Tabela) ((mxCell) importedCell).getValue();
                        Tabela tabelaSource = (Tabela) ((mxCell) drawParams.get("source")).getValue();
                        Tabela tabelaTarget = (Tabela) ((mxCell) drawParams.get("target")).getValue();
                        if (tabelaImported.getNome().equals(tabelaSource.getNome())) {
                            sourceCell = importedCell;
                        } else if (tabelaImported.getNome().equals(tabelaTarget.getNome())) {
                            targetCell = importedCell;
                        }
                    }
                }
                ((FK) drawParams.get("fk")).render(graph, (mxCell) sourceCell, (mxCell) targetCell);
            } else {
                Object relacionamentoTarget = null;
                FK foreignKey = (FK) drawParams.get("fk");
                for (Object importedCell : importedCells) {
                    Tabela tabela = (Tabela) ((mxCell) importedCell).getValue();
                    if (tabela.getConstraintsByClass(FK.class).contains(foreignKey)) {
                        relacionamentoTarget = importedCell;
                    }
                }
                drawRelacionamento(foreignKey, (mxCell) drawParams.get("source"), (mxCell) relacionamentoTarget);
            }
        }
    }

    private Object[] removeFromArray(Object[] array, Object object) {
        List<Object> list = new ArrayList<>(Arrays.asList(array));
        list.remove(object);
        return list.toArray(new Object[list.size()]);
    }

    private mxCell getCellByValueName(String nome) {
        return getCellByValueName(nome, (mxCell) graph.getDefaultParent());
    }

    private mxCell getCellByValueName(String nome, mxCell parent) {
        mxCell celula = null;
        for (Object cell : graph.getChildCells(parent)) {
            if (graph.getModel().isVertex(cell)) {
                if (((Objeto) ((mxCell) cell).getValue()).getNome().equals(nome)) {
                    celula = (mxCell) cell;
                    break;
                } else {
                    celula = getCellByValueName(nome, (mxCell) cell);
                }
            }
        }
        return celula;
    }
    @Override
    protected Objeto encontrarObjetoIgual(Objeto referencia) {

        if (referencia instanceof Coluna) {

            for (Tabela objeto : modelo.getObjetosByClass(Tabela.class)) {

                for (Coluna coluna : objeto.getColunas()) {

                    if (coluna.equals(referencia)) {
                        return coluna;
                    }
                }
            }
            return null;
        } else {

            return super.encontrarObjetoIgual(referencia);
        }
    }

    @Override
    public void afterUndoChange() {
        if (janelaPrincipal.getEditTabelaPanel().isShowing()) {
            mxCell cell = (mxCell) graph.getSelectionCell();
            if (cell != null && cell.getValue() != null) {
                if (cell.getValue() instanceof Tabela) {
                    janelaPrincipal.getEditTabelaPanel().showPanel((Tabela) cell.getValue());
                } else if (cell.getValue() instanceof Coluna) {
                    janelaPrincipal.getEditTabelaPanel().showPanel((Tabela) cell.getParent().getValue());
                } else {
                    janelaPrincipal.getEditTabelaPanel().hidePanel();
                }
            } else {
                janelaPrincipal.getEditTabelaPanel().hidePanel();
            }
        }
    }
}
