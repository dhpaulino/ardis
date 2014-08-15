/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual;

import ardis.model.Modelo;
import ardis.model.Objeto;
import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.ObjetoWithAttribute;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;

import ardis.view.GraphComponent;
import ardis.view.adders.Adder;
import ardis.view.adders.AtributoAdder.AtributoAdder;
import ardis.view.adders.EntidadeAdder.EntidadeAdder;
import ardis.view.adders.EspecializacaoAdder.EspecializacaoAdder;
import ardis.view.adders.RelacionamentoAdder.RelacionamentoAddType;
import ardis.view.adders.RelacionamentoAdder.RelacionamentoAdder;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.swing.JOptionPane;
import org.w3c.dom.Document;

/**
 *
 * @author Davisson
 */
public class ConceitualGraphComponent extends GraphComponent {

    /**
     *
     */
    private static final long serialVersionUID = -6833603133512882012L;
    private ObjetosPositionHandler objetosPositionHandler;
    private ConceitualLayout conceitualLayout;
    private Adder adder;

    /**
     *
     */
    public ConceitualGraphComponent(mxGraph graph) {
        super(graph);
        setModelo(createModelo());
    }

    public ConceitualGraphComponent(mxGraph graph, ModeloConceitual modeloConceitual) {
        super(graph);
        setModelo(modeloConceitual);
    }

    @Override
    public Modelo createModelo() {
        return new ModeloConceitual();
    }

    @Override
    protected void initObjects() {
        super.initObjects();
        this.objetosPositionHandler = new ObjetosPositionHandler(this);
        //modellingHandler = new ModellingHandler(graph);
        conceitualLayout = new ConceitualLayout(graph, this);
    }

    protected void configureGraphComponent() {
        super.configureGraphComponent();
        setFoldingEnabled(false);
        setConnectable(false);
        getConnectionHandler().getMarker().setEnabled(false);

        // Loads the defalt stylesheet from an external file
        mxCodec codec = new mxCodec();
        Document doc = mxUtils.loadDocument(getClass().getResource(
                "/ardis/resources/erModel-style.xml")
                .toString());
        codec.decode(doc.getDocumentElement(), graph.getStylesheet());

        installListeners();
    }

    /**
     * Overrides drop behaviour to set the cell style if the target is not a
     * valid drop target and the cells are of the same type (eg. both vertices
     * or both edges).
     */
    public Object[] importCells(Object[] cells, double dx, double dy,
            Object target, Point location) {


        boolean allowImport = true;
        Set<Object> cellsSet = new CopyOnWriteArraySet<>(Arrays.asList(cells));
        List<Object> values = getValues(cells);
        List<HashMap<String, Object>> edges = new ArrayList();

        for (Object objectCell : cellsSet) {
            mxCell cell = (mxCell) objectCell;

            Objeto value = (Objeto) ((mxCell) cell).getValue();

            if (cell.isEdge()) {
                cellsSet.remove(cell);
            } else if (value != null) {

                if (value instanceof ObjetoWithAttribute) {

                    ObjetoWithAttribute owa = (ObjetoWithAttribute) value;

                    for (Atributo atributo : owa.getAttributes()) {
                        mxCell atributoCell = getGraph().getCellByValue(atributo);

                        if (getGraph().getCellByValue(atributo, cells) == null) {
                            cellsSet.add(atributoCell);
                        }
                    }
                    modelo.addObjeto(value);

                    if (value instanceof Relacionamento) {
                        Relacionamento relacionamento = (Relacionamento) value;


                        for (Iterator<MembroRelacionamento> it = relacionamento.getMembros().iterator(); it.hasNext();) {
                            MembroRelacionamento membro = it.next();

                            mxCell cellEntidade = getGraph().getCellByValue(membro.getEntidade());
                            if (cellEntidade != null) {

                                HashMap<String, Object> drawParams = new HashMap<>();

                                drawParams.put("source", cellEntidade);
                                drawParams.put("target", cell);
                                drawParams.put("value", membro);
                                drawParams.put("style", "cardinalidade");
                                edges.add(drawParams);
                            } else {
                                it.remove();
                            }
                        }

                    }

                } else if (value instanceof Especializacao) {

                    Especializacao especializacao = (Especializacao) value;
                    mxCell cellPai = getGraph().getCellByValue(especializacao.getEntidadePai(), cells);
                    if (cellPai == null) {
                        allowImport = false;
                        break;
                    } else {

                        modelo.addObjeto(especializacao);

                        for (Iterator<Entidade> it = especializacao.getEntidadesFihas().iterator(); it.hasNext();) {
                            Entidade entidadeFilha = it.next();

                            mxCell cellFilha = getGraph().getCellByValue(entidadeFilha, cells);

                            if (cellFilha == null) {
                                it.remove();
                            } else {

                                HashMap<String, Object> drawParams = new HashMap<>();
                                drawParams.put("target", cellFilha);
                                drawParams.put("source", cell);
                                edges.add(drawParams);
                            }
                        }

                        HashMap<String, Object> drawParams = new HashMap<>();
                        drawParams.put("source", getGraph().getCellByValue(especializacao.getEntidadePai(), cells));
                        drawParams.put("target", cell);
                        edges.add(drawParams);
                    }
                }
            }
        }

        for (Object cell : cellsSet) {

            Objeto value = (Objeto) ((mxCell) cell).getValue();

            if (value instanceof Atributo) {

                Atributo atributo = (Atributo) value;
                mxCell selectionCell = (mxCell) getGraph().getSelectionCell();
                mxCell ownerCell =
                        (mxCell) ((mxCell) graph.getEdges(getGraph().getCellByValue(atributo))[0]).getSource();
                mxCell ownerCellOnImport = getGraph().getCellByValue(ownerCell.getValue(), cells);
                mxCell source = ownerCell;

                HashMap<String, Object> drawParams = new HashMap<>();
                if (ownerCellOnImport == null) {

                    if (selectionCell != null && selectionCell.getValue() instanceof ObjetoWithAttribute) {
                        ObjetoWithAttribute owa = (ObjetoWithAttribute) selectionCell.getValue();
                        source = selectionCell;
                        drawParams.put("trueSource", source);
                        owa.addAttribute(atributo);
                    } else {
                        allowImport = false;
                        break;
                    }
                } else {
                    drawParams.put("source", source);
                }

                drawParams.put("target", cell);
                edges.add(drawParams);

                if (getQtdCopias(atributo.getNome(), ((Entidade) source.getValue()).getAttributes()) > 0) {
                    atributo.setNome(atributo.getNome() + "_copia_" + getQtdCopias(value.getNome(), modelo.getObjetos()) + 1);
                }

            } else {
                value.setNome(value.getNome() + "_copia_" + (getQtdCopias(value.getNome(), modelo.getObjetos()) + 1));
            }
        }


        if (!allowImport) {
            JOptionPane.showMessageDialog(this, "Cópia inválida!", "Ação inválida", JOptionPane.ERROR_MESSAGE);
        }
        Object[] importedCells = super.importCells(cellsSet.toArray(), dx, dy, target, location);

        afterImport(importedCells, edges);

        for (Object object : importedCells) {

            Objeto value = (Objeto) ((mxCell) object).getValue();

            if (value != null) {
                value.setOriginalHash(value.hashCode());
            }
        }
        return importedCells;
    }

    public void afterImport(Object[] importedCells, List<HashMap<String, Object>> edges) {

        for (HashMap<String, Object> edge : edges) {

            Object value = (MembroRelacionamento) edge.get("value");
            mxCell source = (mxCell) (edge.get("trueSource") != null ? edge.get("trueSource") : getGraph().getCellByValue(
                    ((mxCell) edge.get("source")).getValue(), importedCells));
            mxCell target = getGraph().getCellByValue(
                    ((mxCell) edge.get("target")).getValue(), importedCells);
            String style = String.valueOf(edge.get("style"));


            graph.insertEdge(graph.getDefaultParent(), null, value, source, target, style);


        }
    }

    private boolean containsCollection(Object object, Collection collection) {
        for (Object objectCollection : collection) {
            if (object.equals(objectCollection)) {
                return true;
            }
        }
        return false;
    }

    private void installListeners() {
        getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                try {

                    mxCell cell = (mxCell) getCellAt(e.getX(), e.getY());
                    if (adder != null) {
                        boolean added = adder.add(graph, (ModeloConceitual) modelo, e.getX(), e.getY());

                        if (added) {
                            adder = null;
                        }
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(null, "Operação Inválida", "Operação Inválida", JOptionPane.ERROR_MESSAGE);
                    adder = null;

                    ex.printStackTrace();
                }

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                mxCell cell = (mxCell) getCellAt(e.getX(), e.getY());
                if (cell != null) {
                    List<Object> selectionCells = new ArrayList<>();
                    selectionCells.add(cell);
                    if (cell.getValue() instanceof ObjetoWithAttribute) {
                        ObjetoWithAttribute objeto = (ObjetoWithAttribute) cell.getValue();
                        for (Atributo atributo : objeto.getAttributes()) {
                            mxCell atributoCell = ((ConceitualGraph) graph).getCellByValue(atributo);
                            selectionCells.add(atributoCell);
                            selectionCells.addAll(Arrays.asList(graph.getEdgesBetween(cell, atributoCell)));
                        }

                    }
                    if (cell.getValue() instanceof Atributo) {
                        selectionCells.addAll(Arrays.asList(graph.getEdges(cell)));
                    }
                    graph.setSelectionCells(selectionCells);
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        });

    }

    public void chooseEntidade(Class classe) {
        EntidadeAdder adder = EntidadeAdder.getInstance();
        adder.prepare(classe);
        this.adder = adder;
    }

    public void chooseRelacionamento(RelacionamentoAddType addType) {
        RelacionamentoAdder adder = RelacionamentoAdder.getInstance(objetosPositionHandler);
        adder.prepare(addType);
        this.adder = adder;
    }

    public void chooseAtributo(Atributo atributo) {
        mxCell selectionCell = (mxCell) graph.getSelectionCell();
        AtributoAdder adder = AtributoAdder.getInstance(objetosPositionHandler);
        adder.prepare(atributo);
        this.adder = adder;
        if (selectionCell != null && selectionCell.getValue() != null
                && selectionCell.getValue() instanceof ObjetoWithAttribute) {

            boolean added;
            try {
                added = this.adder.add(graph, (ModeloConceitual) modelo, selectionCell.getGeometry().getCenterX(),
                        selectionCell.getGeometry().getCenterY());
                if (added) {
                    this.adder = null;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Operação Inválida", "Operação Inválida", JOptionPane.ERROR_MESSAGE);
                this.adder = null;

                ex.printStackTrace();
            }


        }
    }

    public void chooseEspecializacao(int qtdEntidadesFilhas) {
        EspecializacaoAdder adder = EspecializacaoAdder.getInstance(objetosPositionHandler);
        adder.prepare(qtdEntidadesFilhas);
        this.adder = adder;
    }

    @Override
    protected Objeto encontrarObjetoIgual(Objeto referencia) {

        if (referencia instanceof Atributo) {

            for (ObjetoWithAttribute objeto : modelo.getObjetosByClass(ObjetoWithAttribute.class)) {

                for (Atributo atributo : objeto.getAttributes()) {

                    if (atributo.equals(referencia)) {
                        return atributo;
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
    }
}
