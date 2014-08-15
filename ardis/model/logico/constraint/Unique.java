package ardis.model.logico.constraint;

import java.util.List;
import ardis.model.logico.coluna.Coluna;
import java.util.ArrayList;

public class Unique extends Constraint {

    private Coluna coluna;

    public Coluna getColuna() {
        return coluna;
    }

    public void setColuna(Coluna coluna) {
        this.coluna = coluna;
    }
    
    public List<Coluna> getColunas() {
        List<Coluna> lista = new ArrayList<Coluna>();
        lista.add(coluna);
        return lista;
    }

}
