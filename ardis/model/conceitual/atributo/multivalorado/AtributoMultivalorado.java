package ardis.model.conceitual.atributo.multivalorado;

import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.atributo.composto.AtributoComposto;
import ardis.model.conceitual.atributo.composto.AtributoCompostoImpl;
import ardis.model.conceitual.atributo.multivalorado.infoTable.AtributoMultiValoradoInfoTable;
import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.List;

public class AtributoMultivalorado extends Atributo implements AtributoComposto {

    private CardinalidadeType cardinalidade;
    private AtributoComposto atributoComposto;
    private boolean activeComposto;

    public AtributoMultivalorado() {
        super();
        cardinalidade = CardinalidadeType.ZEROPARAMUITOS;
        atributoComposto = new AtributoCompostoImpl();
    }

    public CardinalidadeType getCardinalidade() {
        return cardinalidade;
    }

    public void setCardinalidade(CardinalidadeType cardinalidade) {
        this.cardinalidade = cardinalidade;
    }

    public static CardinalidadeType[] getCardinalidadeTypes() {
        CardinalidadeType[] cardinalidadesTypes = new CardinalidadeType[]{
            CardinalidadeType.ZEROPARAMUITOS, CardinalidadeType.UMPARAMUITOS};

        return cardinalidadesTypes;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {
        return super.initialRender(graph, parent, xPosition, yPosition);
    }

    @Override
    public AtributoMultivalorado clone() throws CloneNotSupportedException {
        AtributoMultivalorado atributoMultiValoradoClone = (AtributoMultivalorado) super.clone();

        //atributoMultiValoradoClone.setCardinalidade(atributoMultiValoradoClone.getCardinalidade().clone());
        return atributoMultiValoradoClone;
    }

    @Override
    public Class getModelInfo() {
        return AtributoMultiValoradoInfoTable.class;
    }

    @Override
    public String toString() {
        return this.getNome() + " (" + this.getCardinalidade().toString() + ")";
    }

    @Override
    public void addAttribute(Atributo attribute) {
        atributoComposto.addAttribute(attribute);
    }

    @Override
    public void removeAttibute(Atributo attribute) {
        atributoComposto.removeAttibute(attribute);
    }

    @Override
    public List<Atributo> getAttributes() {
        return atributoComposto.getAttributes();
    }
}
