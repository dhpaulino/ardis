/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.adders.RelacionamentoAdder;

import ardis.model.ObjetoGrafico;
import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.view.adders.Adder;
import ardis.view.conceitual.ObjetosPositionHandler;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Davisson
 */
public final class RelacionamentoAdder implements Adder {

    private List<mxCell> cellsClicked;
    private RelacionamentoAddType addType;
    private HashMap<Class, Class> classesPermitidasToRelacionar;
    private static RelacionamentoAdder instance;
    private static ObjetosPositionHandler positionHandler;

    private RelacionamentoAdder(ObjetosPositionHandler positionHandler) {
        this.initClassesPermitidasToRelacionar();
        this.positionHandler = positionHandler;
    }

    public void prepare(RelacionamentoAddType addType) {

        this.addType = addType;
        cellsClicked = new ArrayList<>();
    }

    @Override
    public boolean add(mxGraph graph, ModeloConceitual modelo, double x, double y) throws Exception {

        mxCell cell = (mxCell) graph.getSelectionCell();
        graph.getModel().beginUpdate();
        try {
            if (addType == RelacionamentoAddType.ALONE) {
                Relacionamento relacionamento = new Relacionamento();
                relacionamento.initialRender(graph, (mxCell) graph.getDefaultParent(), x, y);
                modelo.addObjeto(relacionamento);
            } else if (cell != null) {
                cellsClicked.add(cell);

                if (addType == RelacionamentoAddType.CONNECT) {
                    if (cellsClicked.size() == 2) {

                        if (this.isJustEntidade() && cellsClicked.get(0) != cellsClicked.get(1)) {

                            Relacionamento relacionamento = new Relacionamento();


                            mxCell relacionamentoCell = relacionamento.initialRender(graph, (mxCell) graph.getDefaultParent(), x, y);

                            for (mxCell cellClicked : cellsClicked) {
                                relacionar(graph, cellClicked, relacionamentoCell);
                            }


                            positionHandler.calculatePositionRelacionamento(relacionamentoCell, cellsClicked);
                            modelo.addObjeto(relacionamento);

                        } else if (isRelacionamentoEntidade()) {
                            relacionarEntidade(graph);
                            //Relacionamento rel = getRelacionamento();
                            // rel.addEntity(null, cardinalities, graph);
                        } else if (isEspecializacaoEntidade()) {

                            especializarEntidade(graph);
                        } else {
                            throw new Exception("OPERAÇÃO INVÁLIDA");
                        }
                    } else {
                        return false;
                    }

                } else if (addType == RelacionamentoAddType.AUTO) {

                    mxCell cellClicked = cellsClicked.get(0);

                    if (cellClicked.getValue() instanceof Entidade) {
                        Relacionamento relacionamento = new Relacionamento();
                        mxCell relCell = relacionamento.initialRender(graph, (mxCell) graph.getDefaultParent(), x, y);

                        relacionar(graph, cellClicked, relCell);
                        relacionar(graph, cellClicked, relCell);

                        positionHandler.calculatePositionRelacionamento(relCell, cellsClicked);

                        modelo.addObjeto(relacionamento);



                    } else {
                        throw new Exception("OPERAÇÃO INVÁLIDA");
                    }
                }
            } else {
                throw new Exception("Nenhum objeto selecionado");
            }
        } finally {
            graph.getModel().endUpdate();
        }

        if (addType.equals(RelacionamentoAddType.AUTO)) {
            graph.getModel().beginUpdate();
            try {
                //TODO: PENSAR COMO APLICAR O PARALLEL LAYPOUT NO RELACIONAMENTO ADICIONADO
                mxIGraphLayout edgeLayout = new mxParallelEdgeLayout(graph);
                edgeLayout.execute(graph.getDefaultParent());
            } finally {
                graph.getModel().endUpdate();
            }
        }

        return true;
    }

    private void relacionar(mxGraph graph, mxCell entidadeCell, mxCell relacionamentoCell) {
        MembroRelacionamento membroRelacionamento = new MembroRelacionamento((Entidade) entidadeCell.getValue());

        ((Relacionamento) relacionamentoCell.getValue()).addMembro(membroRelacionamento);

        membroRelacionamento.initialRender(graph, relacionamentoCell, entidadeCell);
    }

    private mxCell connect(mxGraph graph, mxCell source, mxCell target) {

        return (mxCell) graph.insertEdge(graph.getDefaultParent(), null, null, source, target);
    }

    private boolean isJustEntidade() {
        for (mxCell cell : cellsClicked) {

            if (!(cell.getValue() instanceof Entidade)) {
                return false;
            }
        }
        return true;
    }

    private boolean isRelacionamentoEntidade() {
        mxCell cellUm = cellsClicked.get(0);
        mxCell cellDois = cellsClicked.get(1);

        return ((cellUm.getValue() instanceof Entidade && cellDois.getValue() instanceof Relacionamento)
                || (cellUm.getValue() instanceof Relacionamento && cellDois.getValue() instanceof Entidade));
    }

    private boolean isEspecializacaoEntidade() {
        mxCell cellUm = cellsClicked.get(0);
        mxCell cellDois = cellsClicked.get(1);

        return ((cellUm.getValue() instanceof Entidade && cellDois.getValue() instanceof Especializacao)
                || (cellUm.getValue() instanceof Especializacao && cellDois.getValue() instanceof Entidade));
    }

    private void relacionarEntidade(mxGraph graph) {

        mxCell relCell = null;
        mxCell entidadeCell = null;
        for (mxCell cell : cellsClicked) {
            if (cell.getValue() instanceof Relacionamento) {
                relCell = cell;
            } else if (cell.getValue() instanceof Entidade) {
                entidadeCell = cell;
            }
        }
        MembroRelacionamento membroRelacionamento = new MembroRelacionamento((Entidade) entidadeCell.getValue());
        ((Relacionamento) relCell.getValue()).addMembro(membroRelacionamento);

        membroRelacionamento.initialRender(graph, relCell, entidadeCell);
    }

    private void especializarEntidade(mxGraph graph) {

        mxCell especializacaoCell = null;
        mxCell entidadeCell = null;
        for (mxCell cell : cellsClicked) {
            if (cell.getValue() instanceof Especializacao) {
                especializacaoCell = cell;
            } else if (cell.getValue() instanceof Entidade) {
                entidadeCell = cell;
            }
        }
        ((Especializacao) especializacaoCell.getValue()).addEntitidadeFilha(
                (Entidade) entidadeCell.getValue());

        connect(graph, especializacaoCell, entidadeCell);
    }

    public void initClassesPermitidasToRelacionar() {

        classesPermitidasToRelacionar = new HashMap();


        classesPermitidasToRelacionar.put(Relacionamento.class, Entidade.class);
        classesPermitidasToRelacionar.put(Especializacao.class, Entidade.class);

    }

    private boolean hasPair(ObjetoGrafico objUm, ObjetoGrafico objDois) {

        for (Map.Entry<Class, Class> entry : classesPermitidasToRelacionar.entrySet()) {
            Class key = entry.getKey();
            Class value = entry.getValue();

            if ((key.isInstance(objUm) && value.isInstance(objDois))
                    || (key.isInstance(objDois) && value.isInstance(objUm))) {

                return true;
            }
        }

        return false;
    }

    //TODO:PASSAR MODELO COMO PARAMETRO PARA ADICIONAR OS OBJETOS NELE
    public static RelacionamentoAdder getInstance(ObjetosPositionHandler positionHandler) {

        if (instance == null) {
            instance = new RelacionamentoAdder(positionHandler);
        }
        return instance;
    }
}
