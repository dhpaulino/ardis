/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conversao.UmParaUm;

import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conversao.RegraUsuario;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Davisson
 */
public class RegraUsuarioUmParaUm implements RegraUsuario<Entidade, Relacionamento>, Serializable, Cloneable {

    private Relacionamento relacionamento;
    private Entidade opcaoSelecionada;

    public RegraUsuarioUmParaUm(Relacionamento relacionamento) {
        this.relacionamento = relacionamento;
    }

    @Override
    public String getDescricao() {

        StringBuilder descricao = new StringBuilder();

        descricao.append("O relacionamento 1:1 \"").append(relacionamento.getNome())
                .append("\" foi encontrado! As entidades que participam do relacionamento são:")
                .append("\n");

        int count = 0;
        for (MembroRelacionamento membroRelacionamento : relacionamento.getMembros()) {

            count++;
            descricao.append(" ").append(membroRelacionamento.getEntidade().getNome());
            if (count != relacionamento.getMembros().size()) {
                descricao.append(",");
            }

        }
        descricao.append(".");
        return descricao.toString();
    }

    @Override
    public String getPegunta() {

        String pergunta = "A tabela gerada por qual das entidade deve receber a chave estrangeira?";

        return pergunta;
    }

    @Override
    public Entidade[] getOpcoes() {
        Entidade[] entidades = new Entidade[relacionamento.getMembros().size()];

        int cont = 0;
        for (MembroRelacionamento membroRelacionamento : relacionamento.getMembros()) {

            entidades[cont] = membroRelacionamento.getEntidade();
            cont++;
        }

        return entidades;
    }

    @Override
    public Relacionamento getObjeto() {
        return relacionamento;
    }

    @Override
    public void setObjeto(Relacionamento relacionamento) {
        this.relacionamento = relacionamento;
    }

    public RegraUsuarioUmParaUm clone() throws CloneNotSupportedException {
        return (RegraUsuarioUmParaUm) super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOpcoesSelecionadas(List<Entidade> opcoes) {
        opcaoSelecionada = opcoes.get(0);
    }

    @Override
    public Entidade[] getOpcoesSelecionadas() {
        return new Entidade[]{opcaoSelecionada};
    }

    @Override
    public boolean isMultiSelection() {
        return false;
    }

    public Entidade getOpcaoSelecionada() {
        return opcaoSelecionada;
    }

    public void setOpcaoSelecionada(Entidade opcaoSelecionada) {
        this.opcaoSelecionada = opcaoSelecionada;
    }
    
}
