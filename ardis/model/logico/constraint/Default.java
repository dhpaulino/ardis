package ardis.model.logico.constraint;

import ardis.model.logico.coluna.Coluna;
import java.util.ArrayList;
import java.util.List;

public class Default extends Constraint {

	private Coluna coluna;

	private String valor;

    public Coluna getColuna() {
        return coluna;
    }

    public void setColuna(Coluna coluna) {
        this.coluna = coluna;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
    @Override
    public List<Coluna> getColunas() {
        List<Coluna> lista = new ArrayList<Coluna>();
        lista.add(coluna);
        return lista;
    }

}
