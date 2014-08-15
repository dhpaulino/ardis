/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conversao.especializacao;

/**
 *
 * @author Davisson
 */
public enum OpcoesEspecializacao {

    APENASPAI("Criando uma tabela apenas para a entidade pai"),
    APENASFILHAS("Criando tabelas apenas para as entidades filhas"),
    FILHASEPAI("Criando uma tabela para cada entidade (tanto entidade pai, quanto filhas)");

    private OpcoesEspecializacao(String opcao) {
        this.opcao = opcao;
    }
    private final String opcao;

    @Override
    public String toString() {
        return opcao;
    }
}
