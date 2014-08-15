/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.adders.EspecializacaoAdder;

import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.view.adders.Adder;
import ardis.view.conceitual.ObjetosPositionHandler;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davisson
 */
public class EspecializacaoAdder implements Adder {

    private static EspecializacaoAdder instance;
    private int qtdEntidadesFilhas;
    private ObjetosPositionHandler positionHandler;

    public EspecializacaoAdder(ObjetosPositionHandler positionHandler) {
        this.positionHandler = positionHandler;
    }

    public void prepare(int qtdEntidadesFilhas) {
        this.qtdEntidadesFilhas = qtdEntidadesFilhas;
    }

    @Override
    public boolean add(mxGraph graph, ModeloConceitual modelo, double x, double y) throws Exception {

        mxCell cell = (mxCell) graph.getSelectionCell();

        if (cell == null) {
            throw new Exception("Selecione um objeto");
        }
        if (!(cell.getValue() instanceof Entidade)) {

            throw new Exception("Objeto inválido");
        }
        Entidade entidade = (Entidade) cell.getValue();

        Especializacao especializacao = new Especializacao();

        graph.getModel().beginUpdate();
        try {

            mxCell especializacaoCell = especializacao.initialRender(graph, (mxCell)graph.getDefaultParent(), x, y);

            especializacao.setEntidadePai(entidade);

            connect(graph, especializacaoCell, cell);
            List<mxCell> entidadesFilhas = new ArrayList<>();
            for (int i = 0; i < qtdEntidadesFilhas; i++) {

                mxCell cellEntidadeFilha = createEntidadeFilha(graph, especializacaoCell);
                entidadesFilhas.add(cellEntidadeFilha);

                modelo.addObjeto((Entidade) cellEntidadeFilha.getValue());
            }

            positionHandler.calculatePositionEspecializacao(especializacaoCell, cell, entidadesFilhas);

            modelo.addObjeto(especializacao);

        } finally {
            graph.getModel().endUpdate();
        }
        return true;


    }

    private mxCell connect(mxGraph graph, mxCell source, mxCell target) {

        return (mxCell) graph.insertEdge(graph.getDefaultParent(), null, null, source, target);
    }

    private mxCell createEntidadeFilha(mxGraph graph, mxCell especializacaoCell) {

        Entidade entidade = new Entidade();
        mxCell entidadeCell = entidade.initialRender(graph, (mxCell)graph.getDefaultParent(), 0, 0);
        ((Especializacao) especializacaoCell.getValue()).addEntitidadeFilha(entidade);
        connect(graph, especializacaoCell, entidadeCell);

        return entidadeCell;
    }

    public static EspecializacaoAdder getInstance(ObjetosPositionHandler positionHandler) {
        if (instance == null) {
            instance = new EspecializacaoAdder(positionHandler);
        }
        return instance;
    }
}
