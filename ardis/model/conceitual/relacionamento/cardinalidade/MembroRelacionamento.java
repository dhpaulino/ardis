/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual.relacionamento.cardinalidade;

import ardis.model.ObjetoGraficoImpl;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.relacionamento.cardinalidade.infoTable.CardinalidadeInfoTable;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author Davisson
 */
public class MembroRelacionamento extends ObjetoGraficoImpl {

    private Entidade entidade;
    private boolean fraco;
    private CardinalidadeType cardinalidadeType;

    public MembroRelacionamento() {

        this.nome = "Cardinalidade";
        cardinalidadeType = CardinalidadeType.ZEROPARAMUITOS;
    }

    public MembroRelacionamento(Entidade entidade) {

        this.nome = "Cardinalidade";
        this.entidade = entidade;
        cardinalidadeType = CardinalidadeType.ZEROPARAMUITOS;
    }

    public MembroRelacionamento(Entidade entidade, CardinalidadeType cardinalidadeType) {

        this.nome = "Cardinalidade";
        this.entidade = entidade;
        this.cardinalidadeType = cardinalidadeType;
    }

    public CardinalidadeType getCardinalidadeType() {
        return cardinalidadeType;
    }

    public void setCardinalidadeType(CardinalidadeType cardinalidadeType) {
        this.cardinalidadeType = cardinalidadeType;
    }

    public boolean isFraco() {
        return fraco;
    }

    public void setFraco(boolean fraco) {
        this.fraco = fraco;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public mxCell initialRender(mxGraph graph, mxCell source, mxCell target) {
        mxCell cell = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, this, source, target, "cardinalidade");

        return cell;
    }

    @Override
    public String toString() {
        return this.cardinalidadeType.toString();
    }

    @Override
    public Class getModelInfo() {
        return CardinalidadeInfoTable.class;
    }

    @Override
    public MembroRelacionamento clone() throws CloneNotSupportedException {
        MembroRelacionamento cardinalidadeClone = (MembroRelacionamento) super.clone(); //To change body of generated methods, choose Tools | Templates.


        return cardinalidadeClone;
    }

    @Override
    public boolean isLimitLabelSize() {
        return false;
    }
}
