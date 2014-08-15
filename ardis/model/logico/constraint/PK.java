package ardis.model.logico.constraint;

import java.util.List;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.indice.Indice;
import ardis.model.logico.indice.TipoIndice;
import java.util.ArrayList;

public class PK extends Constraint {

    private List<Coluna> colunas;
    private Indice indice;

    public PK() {
        colunas = new ArrayList<Coluna>();
        indice = new Indice();
        indice.setTipo(TipoIndice.PRIMARY);
        indice.setColunas(colunas);
    }

    public PK(List<Coluna> colunas) {
        this.colunas = colunas;
        this.indice = new Indice();
    }

    public List<Coluna> getColunas() {
        return colunas;
    }

    public void setColunas(List<Coluna> colunas) {
        indice.setColunas(colunas);
        this.colunas = colunas;
    }

    public Indice getIndice() {
        return indice;
    }

    public void setIndice(Indice indice) {
        this.indice = indice;
    }

    public void addColuna(Coluna coluna) {
        this.colunas.add(coluna);
    }
}
