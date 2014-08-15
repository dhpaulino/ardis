package ardis.model.conceitual.relacionamento;

import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conceitual.ObjetoWithAttributeImpl;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Relacionamento extends ObjetoWithAttributeImpl {

    private List<MembroRelacionamento> membros;

    public Relacionamento() {

        this.nome = "Relacionamento";
        this.membros = new ArrayList<>();
    }

    public List<MembroRelacionamento> getMembros() {
        return membros;
    }

    public void setMembros(List<MembroRelacionamento> membros) {
        this.membros = membros;
    }

    public void addMembro(MembroRelacionamento membro) {
        this.membros.add(membro);
    }

    public MembroRelacionamento getMembro(Entidade entidade) {
        for (MembroRelacionamento membro : membros) {
            if (entidade == membro.getEntidade()) {
                return membro;
            }
        }
        return null;
    }

    public List<MembroRelacionamento> getMembro(CardinalidadeType cardinalidade) {

        List<MembroRelacionamento> membrosWithCardinalidade = new ArrayList<>();
        for (MembroRelacionamento membro : membros) {

            if (cardinalidade.equals(membro.getCardinalidadeType())) {

                membrosWithCardinalidade.add(membro);
            }
        }

        return membrosWithCardinalidade;
    }

    public boolean isTernario() {
        return this.membros.size() > 2;
    }

    public boolean isMuitosParaMuitos() {
        Collection<MembroRelacionamento> membros = this.membros;
        if (membros.size() > 1) {
            for (MembroRelacionamento membro : membros) {

                if (!membro.getCardinalidadeType().isMuitos()) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    public boolean isUmParaUm() {

        if (membros.size() > 1) {
            for (MembroRelacionamento membro : membros) {

                if (!membro.getCardinalidadeType().isMaxUm()) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Retorna true se o relacionamento for 1:N ou 0:N
     *
     * @return
     */
    public boolean isUmParaMuitos() {
        boolean onlyParaUm = true;
        boolean onlyMuitos = true;

        for (MembroRelacionamento membro : membros) {

            if (!membro.getCardinalidadeType().isMaxUm()) {
                onlyMuitos = false;
            }
            if (!membro.getCardinalidadeType().isMuitos()) {
                onlyParaUm = false;
            }
        }
        return (!onlyMuitos && !onlyParaUm);
    }

    public List<MembroRelacionamento> getMembrosMuitos() {

        List<MembroRelacionamento> membrosMuitos = new ArrayList<>();

        for (MembroRelacionamento membro : this.membros) {


            if (membro.getCardinalidadeType().isMuitos()) {
                membrosMuitos.add(membro);
            }
        }

        return membrosMuitos;
    }

    public MembroRelacionamento getMembroFraco() {
        for (MembroRelacionamento membro : membros) {
            if (membro.isFraco()) {
                return membro;
            }
        }
        return null;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {
        return (mxCell) graph.insertVertex(parent, null, this, xPosition, yPosition, 100, 50, "relationship");
    }

    @Override
    public Relacionamento clone() throws CloneNotSupportedException {
        Relacionamento relacionamento = (Relacionamento) super.clone();

        List<MembroRelacionamento> newMembros = new ArrayList<>();
        for (MembroRelacionamento membro : membros) {
            newMembros.add(membro.clone());
        }

        relacionamento.membros = newMembros;

        return relacionamento;
    }

    public boolean isAutoRelacionamento() {

        return membros.size() > 2
                && membros.get(0).getEntidade().equals(membros.get(1).getEntidade());
    }

    public void removeMembro(MembroRelacionamento membro) {
        membros.remove(membro);
    }
}
