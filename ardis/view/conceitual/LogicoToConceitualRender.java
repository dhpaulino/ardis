/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual;

import ardis.model.ObjetoGrafico;
import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.logico.ModeloLogico;
import ardis.model.logico.tabela.Tabela;
import ardis.view.logico.LogicoGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Israel
 */
public class LogicoToConceitualRender {

    private ConceitualGraph conceitualGraph;
    private LogicoGraph logicoGraph;
    private ObjetosPositionHandler objetosPositionHandler;
    private ModeloLogico modeloLogico;

    public LogicoToConceitualRender(mxGraphComponent conceitualGraphComponent, LogicoGraph logicoGraph) {
        this.logicoGraph = logicoGraph;
        conceitualGraph = (ConceitualGraph) conceitualGraphComponent.getGraph();
        objetosPositionHandler = new ObjetosPositionHandler(conceitualGraphComponent);
    }

    private mxCell render(ObjetoGrafico objeto, Point posicao) {
        return objeto.initialRender(conceitualGraph, (mxCell) conceitualGraph.getDefaultParent(), posicao.getX(), posicao.getY());
    }

    private mxCell renderEntidade(Entidade entidade) {
        mxCell entidadeCell;
        conceitualGraph.getModel().beginUpdate();
        try {
            Point posicao = getPosicaoTabela(getTabela(entidade.getNome()));
            entidadeCell = render(entidade, posicao);
        } finally {
            conceitualGraph.getModel().endUpdate();
        }
        for (Atributo atributo : entidade.getAttributes()) {
            renderAtributo(atributo, entidadeCell);
        }
        return entidadeCell;
    }

    private void renderAtributo(Atributo atributo, mxCell objetoWithAttribute) {


        conceitualGraph.getModel().beginUpdate();
        try {
            mxCell atributoCell = render(atributo, new Point(0, 0));
            objetosPositionHandler.calculatePositionAtributo(atributoCell, objetoWithAttribute);
            conceitualGraph.insertEdge(objetoWithAttribute, null, null, objetoWithAttribute, atributoCell);
        } finally {
            conceitualGraph.getModel().endUpdate();
        }
    }

    public void mostrarModelo(ModeloConceitual modeloConceitual, ModeloLogico modeloLogico) {
        conceitualGraph.setGenerateCellName(false);
        this.modeloLogico = modeloLogico;
        HashMap<Entidade, mxCell> entidadesRenderizadas = new HashMap<>();

        for (Entidade entidade : modeloConceitual.getObjetosByClass(Entidade.class)) {
            entidadesRenderizadas.put(entidade, renderEntidade(entidade));
        }

        for (Especializacao especializacao : modeloConceitual.getObjetosByClass(Especializacao.class)) {
            conceitualGraph.getModel().beginUpdate();
            try {
                mxCell paiCell = entidadesRenderizadas.get(especializacao.getEntidadePai());
                mxCell especializacaoCell = render(especializacao, new Point(0, 0));
                conceitualGraph.insertEdge(conceitualGraph.getDefaultParent(), null, null, especializacaoCell, paiCell);
                List<mxCell> filhas = new ArrayList<>();
                for (Entidade filha : especializacao.getEntidadesFihas()) {
                    mxCell filhaCell = entidadesRenderizadas.get(filha);
                    filhas.add(filhaCell);
                    conceitualGraph.insertEdge(conceitualGraph.getDefaultParent(), null, null, especializacaoCell, filhaCell);
                }
                objetosPositionHandler.calculatePositionEspecializacao(especializacaoCell, paiCell, filhas);
            } finally {
                conceitualGraph.getModel().endUpdate();
            }
        }

        for (Relacionamento relacionamento : modeloConceitual.getObjetosByClass(Relacionamento.class)) {
            mxCell relacionamentoCell;
            conceitualGraph.getModel().beginUpdate();
            try {
                relacionamentoCell = render(relacionamento, new Point(0, 0));
                List<MembroRelacionamento> membrosRelacionamento = relacionamento.getMembros();
                List<mxCell> entidades = new ArrayList<>();
                for (MembroRelacionamento membroRelacionamento : membrosRelacionamento) {

                    membroRelacionamento.initialRender(conceitualGraph, relacionamentoCell,
                            entidadesRenderizadas.get(membroRelacionamento.getEntidade()));
                    entidades.add(entidadesRenderizadas.get(membroRelacionamento.getEntidade()));
                }
                objetosPositionHandler.calculatePositionRelacionamento(relacionamentoCell, entidades);
            } finally {
                conceitualGraph.getModel().endUpdate();
            }
            for (Atributo atributo : relacionamento.getAttributes()) {
                renderAtributo(atributo, relacionamentoCell);
            }
        }
        conceitualGraph.setGenerateCellName(true);
        conceitualGraph.refresh();
    }

    private Tabela getTabela(String nomeTabela) {
        for (Tabela tabela : modeloLogico.getTabelas()) {
            if (tabela.getNome().equals(nomeTabela)) {
                return tabela;
            }
        }
        return null;
    }

    private Point getPosicaoTabela(Tabela tabela) {
        mxCell celula = logicoGraph.getCellByValue(tabela);
        return celula != null ? celula.getGeometry().getPoint() : new Point(0, 0);
    }
}
