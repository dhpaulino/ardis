/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.constraint.Unique;
import ardis.model.logico.indice.Indice;
import ardis.model.logico.indice.TipoIndice;
import ardis.model.logico.tabela.Tabela;
import com.mxgraph.model.mxCell;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author Israel
 */
public class RelacionamentoHandler {

    private mxCell tabelaFKCell;
    private mxCell tabelaReferenciadaCell;
    private Tabela tabelaFK;
    private Tabela tabelaReferenciada;
    private LogicoGraphComponent graphComponent;

    public RelacionamentoHandler(LogicoGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }

    public mxCell getTabelaFKCell() {
        return tabelaFKCell;
    }

    public void setTabelaFKCell(mxCell tabelaFKCell) {
        this.tabelaFKCell = tabelaFKCell;
        this.tabelaFK = (Tabela) tabelaFKCell.getValue();
    }

    public mxCell getTabelaReferenciadaCell() {
        return tabelaReferenciadaCell;
    }

    public void setTabelaReferenciadaCell(mxCell tabelaReferenciadaCell) {
        this.tabelaReferenciadaCell = tabelaReferenciadaCell;
        this.tabelaReferenciada = (Tabela) tabelaReferenciadaCell.getValue();
    }

    public void createRelacionamento1N() {
        FK foreignKey = createRelacionamento();
        foreignKey.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAMUITOS);
        foreignKey.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAUM);
        Indice indiceFk = Indice.generateFKIndice(foreignKey, tabelaFK, false);
        tabelaFK.addIndice(indiceFk);
        graphComponent.drawRelacionamento(foreignKey, tabelaReferenciadaCell, tabelaFKCell);
        limparTabelas();
    }

    private void limparTabelas() {
        tabelaFK = null;
        tabelaReferenciada = null;
        tabelaFKCell = null;
        tabelaReferenciadaCell = null;
    }

    private FK createRelacionamento() {
        FK foreignKey = new FK();
        HashMap<Coluna, Coluna> hashMap = new LinkedHashMap<>();
        for (Coluna coluna : tabelaReferenciada.getPrimaryKey().getColunas()) {
            Coluna colunaFk = new Coluna();
            colunaFk.setNome(tabelaReferenciada.getNome() + "_" + coluna.getNome());
            colunaFk.setTamanho(coluna.getTamanho());
            colunaFk.setTipo(coluna.getTipo());
            hashMap.put(colunaFk, coluna);
        }
        foreignKey.setColunasComReferencias(hashMap);
        foreignKey.setTabelaReferenciada(tabelaReferenciada);
        tabelaFK.getConstraints().add(foreignKey);

        graphComponent.getModelo().addObjeto(foreignKey);
        return foreignKey;
    }

    public void createRelacionamento11() {
        FK foreignKey = createRelacionamento();
        foreignKey.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAUM);
        foreignKey.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAUM);
        for (Coluna coluna : foreignKey.getColunas()) {
            Unique unique = new Unique();
            unique.setColuna(coluna);
            tabelaFK.getConstraints().add(unique);
        }
        Indice indiceUnique = Indice.generateFKIndice(foreignKey, tabelaFK, true);
        tabelaFK.addIndice(indiceUnique);
        graphComponent.drawRelacionamento(foreignKey, tabelaReferenciadaCell, tabelaFKCell);
        limparTabelas();
    }
}
