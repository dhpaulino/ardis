package ardis.model.conceitual.entidade.entidadeAssociativa;

import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class EntidadeAssociativa extends Entidade {

    private Relacionamento relacionamento;

    public EntidadeAssociativa() {

        this.nome = "Entidade";
        relacionamento = new Relacionamento();

        relacionamento.addMembro(new MembroRelacionamento(this, CardinalidadeType.UMPARAUM));

    }

    public Relacionamento getRelacionamento() {
        return relacionamento;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {

        mxCell cell = (mxCell) graph.insertVertex(parent, null,
                this, xPosition, yPosition, 110, 60, "associativeEntity");

        mxCell relCell = (mxCell) graph.insertVertex(cell, null, relacionamento, 5, 15, 100, 40, "associativeRelation");

        //relCell.setConnectable(false);


        return cell;

    }
}
