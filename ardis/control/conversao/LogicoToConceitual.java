package ardis.control.conversao;

import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import ardis.model.logico.ModeloLogico;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.Constraint;
import ardis.model.logico.constraint.FK;
import java.util.HashMap;
import ardis.model.logico.tabela.Tabela;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogicoToConceitual {

    private ModeloConceitual conceitual;
    private HashMap<Tabela, Entidade> entidades;
    private HashMap<Tabela, Especializacao> especializacoes;
    private List<Tabela> entidadesList;
    private List<Tabela> relacionamentosList;
    private List<Tabela> especializacoesList;

    public LogicoToConceitual() {
        init();
    }

    private void init() {
        this.entidades = new HashMap<>();
        this.especializacoes = new HashMap<>();
        entidadesList = new ArrayList<>();
        relacionamentosList = new ArrayList<>();
        especializacoesList = new ArrayList<>();
    }

    public ModeloConceitual converter(ModeloLogico modeloLogico) {
        conceitual = new ModeloConceitual();
        init();
        converterTabelas(modeloLogico.getTabelas());
        return conceitual;
    }

    private void converterTabelas(List<Tabela> tabelas) {
        for (Tabela tabela : tabelas) {
            String tipoTabela = getTipoTabela(tabela);
            if (tipoTabela.equals("Entidade")) {
                entidadesList.add(tabela);
            } else if (tipoTabela.equals("Relacionamento")) {
                relacionamentosList.add(tabela);
            } else if (tipoTabela.equals("Especialização")) {
                especializacoesList.add(tabela);
            }
        }
        criarEntidades(entidadesList);
        criarEspecializacoes(especializacoesList);
        criarRelacionamentosNN(relacionamentosList);
    }

    private void criarEntidades(List<Tabela> tabelas) {
        for (Tabela tabela : tabelas) {
            criarEntidade(tabela);
        }
        for(Tabela tabela : tabelas){
            criarRelacionamentos(tabela);
        }
    }

    private Entidade criarEntidade(Tabela tabela) {
        Entidade entidade = new Entidade();
        entidade.setNome(tabela.getNome());
        entidade.setDescricao(tabela.getDescricao());
        conceitual.addObjeto(entidade);
        this.entidades.put(tabela, entidade);
        criarAtributos(tabela);
        return entidade;
    }

    private void criarRelacionamentosNN(List<Tabela> tabelas) {
        for (Tabela tabela : tabelas) {
            List<MembroRelacionamento> membrosRelacionamento = new ArrayList<>();
            for (Entidade entidade : getEntidadesReferenciadas(tabela)) {
                membrosRelacionamento.add(new MembroRelacionamento(entidade, CardinalidadeType.UMPARAMUITOS));
            }
            Relacionamento relacionamento = criarRelacionamento(membrosRelacionamento);
            relacionamento.setNome(tabela.getNome());
            relacionamento.setDescricao(tabela.getDescricao());
            criarAtributos(relacionamento, tabela);
        }
    }

    private Relacionamento criarRelacionamento(List<MembroRelacionamento> membrosRelacionamento) {
        Relacionamento relacionamento = new Relacionamento();
        relacionamento.setMembros(membrosRelacionamento);
        conceitual.addObjeto(relacionamento);
        return relacionamento;
    }

    private void criarEspecializacoes(List<Tabela> tabelas) {
        for (Tabela tabela : tabelas) {
            Coluna pk = tabela.getPrimaryKey().getColunas().get(0);
            FK fk = tabela.getConstraintByColuna(pk, FK.class);
            Tabela pai = fk.getTabelaReferenciada();
            Especializacao especializacao;

            if (this.especializacoes.containsKey(pai)) {
                especializacao = this.especializacoes.get(pai);
            } else {
                especializacao = new Especializacao();
                this.especializacoes.put(pai, especializacao);
                especializacao.setEntidadePai(this.entidades.get(pai));
                conceitual.addObjeto(especializacao);
            }
            especializacao.addEntitidadeFilha(criarEntidade(tabela));
        }
    }

    private List<Entidade> getEntidadesReferenciadas(Tabela tabela) {
        List<Coluna> pks = tabela.getPrimaryKey().getColunas();
        List<Entidade> entidadesReferenciadas = new ArrayList<>();
        for (Coluna coluna : pks) {
            FK foreignKey = tabela.getConstraintByColuna(coluna, FK.class);
            if (foreignKey != null) {
                Tabela tabelaReferenciada = foreignKey.getTabelaReferenciada();
                entidadesReferenciadas.add(this.entidades.get(tabelaReferenciada));
            }
        }
        return entidadesReferenciadas;
    }

    private String getTipoTabela(Tabela tabela) {
        List<Coluna> pks = tabela.getPrimaryKey().getColunas();
        Set<Tabela> tabelasReferenciadas = new HashSet<>();
        if (pks.isEmpty()) {
            return "Entidade";
        }
        for (Coluna coluna : pks) {
            FK foreignKey = tabela.getConstraintByColuna(coluna, FK.class);
            if (foreignKey == null) {
                return "Entidade";
            } else {
                tabelasReferenciadas.add(foreignKey.getTabelaReferenciada());
            }
        }
        return tabelasReferenciadas.size() == 1 ? "Especialização" : "Relacionamento";
    }

    private void criarAtributos(Tabela tabela) {
        for (Coluna coluna : tabela.getColunas()) {
            if (tabela.getConstraintByColuna(coluna, FK.class) == null) {
                Atributo atributo = new Atributo();
                atributo.setPrimaryKey(tabela.getPrimaryKey().getColunas().contains(coluna));
                atributo.setNome(coluna.getNome());
                atributo.setDescricao(coluna.getDescricao());
                this.entidades.get(tabela).addAttribute(atributo);
                conceitual.addObjeto(atributo);
            }
        }
    }

    private void criarAtributos(Relacionamento relacionamento, Tabela tabela) {
        for (Coluna coluna : tabela.getColunas()) {
            if (tabela.getConstraintByColuna(coluna, FK.class) == null) {
                Atributo atributo = new Atributo();
                atributo.setNome(coluna.getNome());
                atributo.setDescricao(coluna.getDescricao());
                relacionamento.addAttribute(atributo);
            }
        }
    }

    private void criarRelacionamentos(Tabela tabela) {
        for (Constraint constraint : tabela.getConstraints()) {
            if (constraint instanceof FK) {
                FK foreignKey = (FK) constraint;
                if (!(especializacoesList.contains(tabela)
                        && tabela.getPrimaryKey().getColunas().contains(foreignKey.getColunas().get(0)))) {
                    List<MembroRelacionamento> membrosRelacionamento = new ArrayList<>();
                    membrosRelacionamento.add(new MembroRelacionamento(this.entidades.get(tabela), foreignKey.getCardinalidadeTabelaFK()));
                    membrosRelacionamento.add(
                            new MembroRelacionamento(this.entidades.get(foreignKey.getTabelaReferenciada()),
                            foreignKey.getCardinalidadeTabelaReferenciada()));
                    Relacionamento relacionamento = criarRelacionamento(membrosRelacionamento);
                    relacionamento.setNome(tabela.getNome() + "_" + foreignKey.getTabelaReferenciada().getNome());
                }
            }
        }

    }
}
