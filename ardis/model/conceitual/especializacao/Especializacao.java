package ardis.model.conceitual.especializacao;

import ardis.model.ObjetoGraficoImpl;
import ardis.model.conceitual.entidade.Entidade;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.List;

public class Especializacao extends ObjetoGraficoImpl {

    private Entidade entidadePai;
    private List<Entidade> entidadesFihas;

    public Especializacao() {
        nome = "Especialização";
        entidadesFihas = new ArrayList<>();
    }

    public Entidade getEntidadePai() {
        return entidadePai;
    }

    public List<Entidade> getEntidadesFihas() {
        return entidadesFihas;
    }

    public void addEntitidadeFilha(Entidade entidade) {
        this.entidadesFihas.add(entidade);
    }

    public void setEntidadePai(Entidade entidadePai) {
        this.entidadePai = entidadePai;
    }

    public void setEntidadesFihas(List<Entidade> entidadesFihas) {
        this.entidadesFihas = entidadesFihas;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {

        return (mxCell) graph.insertVertex(parent, null, this, xPosition, yPosition, 50, 40, "especialization");
    }
}
