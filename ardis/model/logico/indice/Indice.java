/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.logico.indice;

import ardis.model.ObjetoImpl;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.tabela.Tabela;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Israel
 */
public class Indice extends ObjetoImpl {

    private List<Coluna> colunas;
    private TipoIndice tipo;

    public Indice() {
        super();
        colunas = new ArrayList<Coluna>();
    }

    public Indice(String nome, List<Coluna> colunas, TipoIndice tipo) {
        this.nome = nome;
        this.colunas = colunas;
        this.tipo = tipo;
    }

    @Override
    public String getNome() {
        String nomeIdx = "IDX";
        for(Coluna coluna : colunas){
            nomeIdx += "_"+coluna.getNome();
        }
        this.nome = nomeIdx;
        return this.nome;
    }
    
    public List<Coluna> getColunas() {
        return colunas;
    }

    public void setColunas(List<Coluna> colunas) {
        this.colunas = colunas;
    }

    public TipoIndice getTipo() {
        return tipo;
    }

    public void setTipo(TipoIndice tipo) {
        this.tipo = tipo;
    }

    public static Indice generateFKIndice(FK fk, Tabela tabela, boolean unique) {
        List colunas = new ArrayList(fk.getColunasComReferencias().keySet());
        TipoIndice tipo = unique ? TipoIndice.UNIQUE : TipoIndice.INDEX; 
        Indice indice = new Indice("IDX_" + tabela.getNome() + "_" + fk.getTabelaReferenciada().getNome(),
                colunas, tipo);

        return indice;

    }
}
