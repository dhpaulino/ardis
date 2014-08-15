package ardis.model.logico.constraint;

import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import java.util.HashMap;
import ardis.model.logico.tabela.Tabela;
import ardis.model.logico.coluna.Coluna;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FK extends Constraint {

    private Map<Coluna, Coluna> colunasComReferencias;
    private CardinalidadeType cardinalidadeTabelaFK;
    private CardinalidadeType cardinalidadeTabelaReferenciada;
    private Tabela tabelaReferenciada;

    public FK() {
        super();
        colunasComReferencias = new HashMap<>();
    }

    public Map<Coluna, Coluna> getColunasComReferencias() {
        return colunasComReferencias;
    }

    public void setColunasComReferencias(Map<Coluna, Coluna> colunas) {
        colunasComReferencias = colunas;
    }

    public Tabela getTabelaReferenciada() {
        return tabelaReferenciada;
    }

    public void setTabelaReferenciada(Tabela tabelaReferenciada) {
        this.tabelaReferenciada = tabelaReferenciada;
    }

    public CardinalidadeType getCardinalidadeTabelaFK() {
        return cardinalidadeTabelaFK;
    }

    public void setCardinalidadeTabelaFK(CardinalidadeType cardinalidadeTabelaFK) {
        this.cardinalidadeTabelaFK = cardinalidadeTabelaFK;
    }

    public CardinalidadeType getCardinalidadeTabelaReferenciada() {
        return cardinalidadeTabelaReferenciada;
    }

    public void setCardinalidadeTabelaReferenciada(CardinalidadeType cardinalidadeTabelaReferenciada) {
        this.cardinalidadeTabelaReferenciada = cardinalidadeTabelaReferenciada;
    }

    @Override
    public List<Coluna> getColunas() {

        List<Coluna> lista = new ArrayList<>();
        Set colunas = new HashSet(colunasComReferencias.keySet());
        lista.addAll(colunas);
        return lista;
    }

    public void putColunaComReferencia(Coluna coluna, Coluna colunaReferenciada) {
        colunasComReferencias.put(coluna, colunaReferenciada);
    }

    public mxCell render(mxGraph graph, mxCell source, mxCell target) {
        return (mxCell) graph.insertEdge(graph.getDefaultParent(), null, this,
                source, target, "cardinality");
    }
}
