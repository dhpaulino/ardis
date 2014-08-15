/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conversao.especializacao;

import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conversao.RegraUsuario;
import java.util.List;

/**
 *
 * @author Davisson
 */
public class RegrasUsuarioEspecializacao implements RegraUsuario<OpcoesEspecializacao, Especializacao> {

    private Especializacao especializacao;
    private OpcoesEspecializacao opcaoSelecionada;

    public RegrasUsuarioEspecializacao(Especializacao especializacao) {
        this.especializacao = especializacao;
    }

    private String entidadeFilhasToString(List<Entidade> entidades) {
        StringBuilder string = new StringBuilder();

        int count = 0;
        for (Entidade entidade : entidades) {

            //TODO:APAGAR VIRGULA AO FINAL 
            count++;
            string.append(entidade.getNome());
            if (count != entidades.size()) {
                string.append(",");
            }


        }

        return string.toString();
    }

    @Override
    public String getDescricao() {
        StringBuilder descricao = new StringBuilder();

        descricao.append("A especialização \"").append(especializacao.getNome())
                .append("\" foi encontrada!").append("\n Ela parte da entidade \"")
                .append(especializacao.getEntidadePai().getNome())
                .append("\", sendo especializada nas entidades:\n")
                .append(entidadeFilhasToString(especializacao.getEntidadesFihas())).append(".");

        return descricao.toString();
    }

    @Override
    public String getPegunta() {
        String pergunta = "Como deseja converter esta especialização?";

        return pergunta;
    }

    @Override
    public Especializacao getObjeto() {
        return this.especializacao;
    }

    @Override
    public OpcoesEspecializacao[] getOpcoes() {
        return OpcoesEspecializacao.values();
    }

    @Override
    public void setObjeto(Especializacao especializacao) {
        this.especializacao = especializacao;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOpcoesSelecionadas(List<OpcoesEspecializacao> opcoes) {
        opcaoSelecionada = opcoes.get(0);
    }

    @Override
    public OpcoesEspecializacao[] getOpcoesSelecionadas() {
        return new OpcoesEspecializacao[]{opcaoSelecionada};
    }

    @Override
    public boolean isMultiSelection() {
        return false;
    }

    public OpcoesEspecializacao getOpcaoSelecionada() {
        return opcaoSelecionada;
    }

    public void setOpcaoSelecionada(OpcoesEspecializacao opcaoSelecionada) {
        this.opcaoSelecionada = opcaoSelecionada;
    }
}
