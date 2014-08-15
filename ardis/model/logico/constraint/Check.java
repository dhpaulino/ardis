package ardis.model.logico.constraint;

import ardis.model.logico.coluna.Coluna;
import java.util.ArrayList;
import java.util.List;

public class Check extends Constraint {

	private Coluna coluna;

	private String regra;

    public Coluna getColuna() {
        return coluna;
    }

    public void setColuna(Coluna coluna) {
        this.coluna = coluna;
    }

    public String getRegra() {
        return regra;
    }

    public void setRegra(String regra) {
        this.regra = regra;
    }

    @Override
    public List<Coluna> getColunas() {
        List<Coluna> lista = new ArrayList<Coluna>();
        lista.add(coluna);
        return lista;
    }

}
