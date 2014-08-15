/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conversao.entidadeFraca;

import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conversao.RegraUsuario;
import java.util.List;

/**
 *
 * @author Davisson
 */
public class RegrasUsuarioEntidadeFraca implements RegraUsuario<Atributo, Relacionamento> {

    private Relacionamento relacionamento;
    private List<Atributo> opcoesSelecionadas;

    public RegrasUsuarioEntidadeFraca(Relacionamento relacionamento) {
        this.relacionamento = relacionamento;
    }

    @Override
    public String getDescricao() {
        return "A entidade fraca \"" + relacionamento.getMembroFraco().getEntidade() + "\" para com o relacionamento \""
                + relacionamento + "\" foi encontrada!";
    }

    @Override
    public String getPegunta() {

        return "Quais dos seus atributos deseja devem compor a chave primária da tabela gerada?";
    }

    @Override
    public Atributo[] getOpcoes() {

        Atributo[] atributos = new Atributo[relacionamento.getMembroFraco().getEntidade().getAttributes().size()];

        int cont = 0;
        for (Atributo atributo : relacionamento.getMembroFraco().getEntidade().getAttributes()) {

            atributos[cont] = atributo;
            cont++;
        }

        return atributos;
    }

    @Override
    public Relacionamento getObjeto() {
        return relacionamento;
    }

    @Override
    public void setObjeto(Relacionamento objeto) {
        this.relacionamento = objeto;
    }

    @Override
    public void setOpcoesSelecionadas(List<Atributo> opcoes) {
        opcoesSelecionadas = opcoes;
    }

    @Override
    public Atributo[] getOpcoesSelecionadas() {
        return opcoesSelecionadas.toArray(new Atributo[opcoesSelecionadas.size()]);
    }

    @Override
    public boolean isMultiSelection() {
        return true;
    }
}
