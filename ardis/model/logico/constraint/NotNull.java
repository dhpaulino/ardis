package ardis.model.logico.constraint;

import ardis.model.logico.coluna.Coluna;
import java.util.ArrayList;
import java.util.List;

public class NotNull extends Constraint {

	private Coluna coluna;

    public Coluna getColuna() {
        return coluna;
    }

    public void setColuna(Coluna coluna) {
        this.coluna = coluna;
    }
        
    @Override
    public List<Coluna> getColunas() {
        List<Coluna> lista = new ArrayList<Coluna>();
        lista.add(coluna);
        return lista;
    }

}
