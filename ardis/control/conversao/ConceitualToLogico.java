package ardis.control.conversao;

import ardis.model.Objeto;
import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.ObjetoWithAttribute;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.atributo.composto.AtributoCompostoImpl;
import ardis.model.conceitual.atributo.multivalorado.AtributoMultivalorado;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.entidade.entidadeAssociativa.EntidadeAssociativa;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.CardinalidadeType;
import ardis.model.conversao.RegraUsuario;
import ardis.model.logico.ModeloLogico;
import ardis.model.conversao.UmParaUm.RegraUsuarioUmParaUm;
import ardis.model.conversao.entidadeFraca.RegrasUsuarioEntidadeFraca;
import ardis.model.conversao.especializacao.OpcoesEspecializacao;
import ardis.model.conversao.especializacao.RegrasUsuarioEspecializacao;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.Constraint;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.constraint.PK;
import ardis.model.logico.indice.Indice;
import ardis.model.logico.tabela.Tabela;
import ardis.ultil.CloneHandler;
import ardis.view.conceitual.ConceitualGraph;
import com.mxgraph.model.mxCell;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConceitualToLogico {

    private ConceitualGraph conceitualGraph;
    private ModeloConceitual modeloConceitual;
    private ModeloLogico modeloLogico;
    private List<RegraUsuario> regrasUsuario;
    private HashMap<Objeto, Objeto> conceitualLogicoCorrespondecia;
    private List<RegraUsuarioUmParaUm> regrasUsuarioUmParaUmConfiguradas;
    private HashMap<ObjetoWithAttribute, AtributoMultivalorado> atributosMultivalorados;
    private List<Objeto> objetosSemPk;
    public HashMap<Object, Point> positions;

    public ConceitualToLogico() {

        conceitualLogicoCorrespondecia = new HashMap<>();
        regrasUsuarioUmParaUmConfiguradas = new ArrayList<>();
        atributosMultivalorados = new HashMap<>();
        positions = new HashMap<>();
        objetosSemPk = new ArrayList<>();

    }

    public List<RegraUsuario> analisar(ConceitualGraph conceitualGraph) {

        this.conceitualGraph = conceitualGraph;

        ModeloConceitual modeloConceitual = (ModeloConceitual) ((mxCell) conceitualGraph.getDefaultParent()).getValue();
        List<RegraUsuario> regrasUsuario = new ArrayList<>();
        try {
            this.cleanConversao();
            //TODO:CLONAR MODELO
            this.modeloConceitual = CloneHandler.clone(modeloConceitual);


            for (Relacionamento relacionamento : this.modeloConceitual.getObjetosByClass(Relacionamento.class)) {

                MembroRelacionamento membroRelacionamentoFraco = relacionamento.getMembroFraco();
                if (membroRelacionamentoFraco != null) {

                    RegrasUsuarioEntidadeFraca ruef = new RegrasUsuarioEntidadeFraca(relacionamento);

                    regrasUsuario.add(ruef);
                }
                if (relacionamento.isUmParaUm()) {

                    RegraUsuarioUmParaUm regraUsuarioUmParaUm = new RegraUsuarioUmParaUm(relacionamento);

                    if (membroRelacionamentoFraco != null) {
                        regraUsuarioUmParaUm.setOpcaoSelecionada(membroRelacionamentoFraco.getEntidade());
                        regrasUsuarioUmParaUmConfiguradas.add(regraUsuarioUmParaUm);

                    } else {

                        List< MembroRelacionamento> membrosZeroPraUm = relacionamento.getMembro(CardinalidadeType.ZEROPARAUM);
                        if (membrosZeroPraUm.size() == 1) {
                            regrasUsuarioUmParaUmConfiguradas.add(regraUsuarioUmParaUm);
                            regraUsuarioUmParaUm.setOpcaoSelecionada(membrosZeroPraUm.get(0).getEntidade());
                        } else {
                            regrasUsuario.add(regraUsuarioUmParaUm);
                        }
                    }
                }
            }

            for (Especializacao especializacao : this.modeloConceitual.getObjetosByClass(Especializacao.class)) {

                if (especializacao.getEntidadesFihas().size() > 0) {
                    RegrasUsuarioEspecializacao regrasUsuarioEspecializacao = new RegrasUsuarioEspecializacao(especializacao);
                    regrasUsuario.add(regrasUsuarioEspecializacao);
                }

            }

        } catch (Exception ex) {

            Logger.getLogger(ConceitualToLogico.class.getName()).log(Level.SEVERE, null, ex);

        }
        return regrasUsuario;
    }

    public ModeloLogico converter(List<RegraUsuario> regras) {
        try {

            this.regrasUsuario = new CopyOnWriteArrayList<>(regras);

            for (RegraUsuario regraUsuario : regrasUsuario) {

                if (regraUsuario instanceof RegrasUsuarioEspecializacao) {
                    converter((RegrasUsuarioEspecializacao) regraUsuario);
                }
            }
            for (Entidade entidade : this.modeloConceitual.getObjetosByClass(Entidade.class)) {
                converter(entidade);
            }
            for (RegraUsuario regraUsuario : regrasUsuario) {

                if (regraUsuario instanceof RegrasUsuarioEspecializacao
                        && regraUsuario.getOpcoesSelecionadas()[0].equals(OpcoesEspecializacao.FILHASEPAI)) {
                    converterEspecializacaoPaiFilha((RegrasUsuarioEspecializacao) regraUsuario);
                } else if (regraUsuario instanceof RegraUsuarioUmParaUm) {
                    converter((RegraUsuarioUmParaUm) regraUsuario);
                } else if (regraUsuario instanceof RegrasUsuarioEntidadeFraca) {
                    converterEntidadeFraca((RegrasUsuarioEntidadeFraca) regraUsuario);
                }
            }
            for (Relacionamento relacionamento : this.modeloConceitual.getObjetosByClass(Relacionamento.class)) {
                converter(relacionamento);
            }

            for (Map.Entry<ObjetoWithAttribute, AtributoMultivalorado> entry : atributosMultivalorados.entrySet()) {
                ObjetoWithAttribute objetoWhithAttribute = entry.getKey();
                AtributoMultivalorado atributoMultivalorado = entry.getValue();

                converterAtributoMultivalorado(objetoWhithAttribute, atributoMultivalorado);
            }

            for (RegraUsuarioUmParaUm regraUsuarioUmParaUm : regrasUsuarioUmParaUmConfiguradas) {

                converter(regraUsuarioUmParaUm);
            }


            for (Map.Entry<Objeto, Objeto> conceitualLogico : conceitualLogicoCorrespondecia.entrySet()) {
                Objeto objetoConceitual = conceitualLogico.getKey();
                Objeto objetoLogico = conceitualLogico.getValue();

                if (objetoLogico instanceof Tabela) {
                    modeloLogico.addObjeto(objetoLogico);


                    mxCell cell = conceitualGraph.getCellByValue(objetoConceitual);

                    if (cell == null || cell.getGeometry() == null) {
                        positions.put(objetoLogico, new Point(0, 0));
                    } else {
                        positions.put(objetoLogico, cell.getGeometry().getPoint());
                    }
                }
            }

            return modeloLogico;
        } catch (Exception ex) {
            Logger.getLogger(ConceitualToLogico.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void cleanConversao() {
        modeloLogico = new ModeloLogico();
        conceitualLogicoCorrespondecia.clear();
        regrasUsuarioUmParaUmConfiguradas.clear();
        atributosMultivalorados.clear();
        objetosSemPk.clear();
        positions.clear();
    }

    public HashMap<Object, Point> getPositions() {
        return positions;
    }

    private void converter(Entidade entidade) {

        Tabela tabela = new Tabela();
        tabela.setNome(entidade.getNome());
        tabela.addPrefixo();
        createAtributos(entidade, tabela);


        conceitualLogicoCorrespondecia.put(entidade, tabela);

        if (entidade instanceof EntidadeAssociativa) {
            EntidadeAssociativa entidadeAssociativa = (EntidadeAssociativa) entidade;

            this.modeloConceitual.addObjeto(entidadeAssociativa.getRelacionamento());
        }

    }

    private void converter(Relacionamento relacionamento) {

        //TODO: Implementar para não duplicar o nome das coluna fks de uma N:N em um auto relacionamento

        if (relacionamento.isAutoRelacionamento()) {
            relacionamento.getMembros().get(0).setCardinalidadeType(CardinalidadeType.UMPARAUM);
            relacionamento.getMembros().get(1).setCardinalidadeType(CardinalidadeType.UMPARAMUITOS);
        }
        if (relacionamento.isMuitosParaMuitos() || relacionamento.isTernario()) {
            converterRelacionamentosMuitosParaMuitos(relacionamento);
        } else if (relacionamento.isUmParaMuitos()) {
            converterRelacionamentoUmParaMuitos(relacionamento);
        }

    }

    private void converterEspecializacaoPaiFilha(RegrasUsuarioEspecializacao regra) {
        Especializacao especializacao = regra.getObjeto();

        Entidade entidadePai = especializacao.getEntidadePai();
        Tabela tabelaPai = (Tabela) findCorrespondencia(entidadePai);


        for (Entidade entidadeFilha : especializacao.getEntidadesFihas()) {

            Tabela tabelaFilha = (Tabela) findCorrespondencia(entidadeFilha);


            FK fk = createFK(tabelaFilha, tabelaPai, especializacao);
            if (fk != null) {
                fk.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAUM);
                fk.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAMUITOS);

                if (!tabelaFilha.hasPrimaryKey()) {

                    PK pk = tabelaFilha.getPrimaryKey();

                    pk.setColunas(fk.getColunas());
                }
            }

        }
    }

    private void converterRelacionamentosMuitosParaMuitos(Relacionamento relacionamento) {

        List<MembroRelacionamento> membrosRelacionamento = relacionamento.getMembros();
        Tabela tabelaRelacionamento = new Tabela();
        tabelaRelacionamento.setNome(getTabelaRelacionamentoNome(relacionamento));
        tabelaRelacionamento.addPrefixo();

        createAtributos(relacionamento, tabelaRelacionamento);

        for (MembroRelacionamento membro : membrosRelacionamento) {

            Tabela tabelaEntidade = (Tabela) findCorrespondencia(membro.getEntidade());

            FK fk = createFK(tabelaRelacionamento, tabelaEntidade, relacionamento);
            if (fk == null) {

                return;
            }
            fk.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAUM);
            fk.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAMUITOS);


        }

        if (!tabelaRelacionamento.hasPrimaryKey()) {

            PK pk = tabelaRelacionamento.getPrimaryKey();

            for (Constraint constraint : tabelaRelacionamento.getConstraints()) {
                if (constraint instanceof FK) {
                    FK fk = (FK) constraint;
                    for (Coluna coluna : fk.getColunasComReferencias().keySet()) {
                        pk.addColuna(coluna);
                    }

                }
            }
        }
        conceitualLogicoCorrespondecia.put(relacionamento, tabelaRelacionamento);
    }

    private void converterRelacionamentoUmParaMuitos(Relacionamento relacionamento) {

        MembroRelacionamento membroMuitos = null;

        MembroRelacionamento membroUm = null;

        for (MembroRelacionamento membroRelacionamento : relacionamento.getMembros()) {

            if (membroRelacionamento.getCardinalidadeType().isMuitos()) {
                membroMuitos = membroRelacionamento;
            } else {
                membroUm = membroRelacionamento;
            }
        }

        Tabela tabelaMuitos = (Tabela) findCorrespondencia(membroMuitos.getEntidade());

        createAtributos(relacionamento, tabelaMuitos);


        Tabela tabelaUm = (Tabela) findCorrespondencia(membroUm.getEntidade());



        FK fk = createFK(tabelaMuitos, tabelaUm, relacionamento);
        if (fk != null) {
            fk.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAUM);
            fk.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAMUITOS);



        }
    }

    private void converterEntidadeFraca(RegrasUsuarioEntidadeFraca regrasUsuarioEntidadeFraca) {


        MembroRelacionamento membroFraco = regrasUsuarioEntidadeFraca.getObjeto().getMembroFraco();

        Tabela tabelaFraco = (Tabela) findCorrespondencia(membroFraco.getEntidade());
        PK pk = tabelaFraco.getPrimaryKey();
        FK fk = null;

        for (MembroRelacionamento membro : regrasUsuarioEntidadeFraca.getObjeto().getMembros()) {
            //TODO: E se tiver 2 membros fracos no relacionamento???
            if (!membro.isFraco()) {

                Tabela tabelaNaoFraco = (Tabela) findCorrespondencia(membro.getEntidade());

                fk = createFK(tabelaFraco, tabelaNaoFraco, regrasUsuarioEntidadeFraca.getObjeto());
                if (fk != null) {
                    for (Coluna coluna : fk.getColunas()) {
                        pk.addColuna(coluna);
                    }
                    //TODO:Fazer pegar a cardinalidade corretamente
                    fk.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAUM);
                    fk.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAUM);
                }

            }
        }
        if (fk != null) {
            for (Atributo atributo : regrasUsuarioEntidadeFraca.getOpcoesSelecionadas()) {
                pk.addColuna((Coluna) findCorrespondencia(atributo));
            }
        }



    }

    private void converter(RegrasUsuarioEspecializacao regrasUsuarioEspecializacao) {


        Especializacao especializacao = regrasUsuarioEspecializacao.getObjeto();

        Entidade entidadePai = especializacao.getEntidadePai();
        if (regrasUsuarioEspecializacao.getOpcaoSelecionada().equals(OpcoesEspecializacao.APENASPAI)) {

            for (Entidade entidadeFilha : especializacao.getEntidadesFihas()) {
                List<Relacionamento> relacionamentos = modeloConceitual.getRelacionamentos(entidadeFilha);

                replaceRegrasUmPraUm(entidadeFilha, entidadePai, relacionamentos);
                for (Relacionamento relacionamento : relacionamentos) {
                    changeRelacionamento(entidadeFilha, entidadePai, relacionamento);
                }



                for (Atributo atributo : entidadeFilha.getAttributes()) {

                    atributo.setPrimaryKey(false);

                    entidadePai.addAttribute(atributo);
                }
                modeloConceitual.getObjetos().remove(entidadeFilha);
            }
        } else if (regrasUsuarioEspecializacao.getOpcaoSelecionada().equals(OpcoesEspecializacao.APENASFILHAS)) {

            List<Relacionamento> relacionamentosPai = modeloConceitual.getRelacionamentos(entidadePai);
            for (Entidade entidadeFilha : especializacao.getEntidadesFihas()) {

                List<Relacionamento> newRelacionamentos = new ArrayList<>();

                for (Relacionamento relacionamento : relacionamentosPai) {
                    try {
                        Relacionamento relacionamentoCopy = relacionamento.clone();
                        changeRelacionamento(entidadePai, entidadeFilha, relacionamentoCopy);
                        newRelacionamentos.add(relacionamentoCopy);
                    } catch (Exception ex) {
                        Logger.getLogger(ConceitualToLogico.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                modeloConceitual.getObjetos().addAll(newRelacionamentos);

                replaceRegrasUmPraUm(entidadePai, entidadeFilha, newRelacionamentos);


                boolean hasPk = entidadeFilha.hasPrimaryKey();
                for (Atributo atributo : entidadePai.getAttributes()) {

                    Atributo clonedAtributo = CloneHandler.clone(atributo);
                    clonedAtributo.setOriginalHash(atributo.getOriginalHash());
                    clonedAtributo.setPrimaryKey(hasPk ? false : clonedAtributo.isPrimaryKey());
                    entidadeFilha.addAttribute(clonedAtributo);


                }
                // copyAtributos(entidadePai, entidadeFilha, false);
            }

            modeloConceitual.removeRelacionamentos(entidadePai);
            modeloConceitual.getObjetos().remove(entidadePai);
        }
    }

    private void converter(RegraUsuarioUmParaUm regraUsuarioUmParaUm) {

        MembroRelacionamento membroReferenciado = null;
        MembroRelacionamento membroFK = null;

        for (MembroRelacionamento membroRelacionamento : regraUsuarioUmParaUm.getObjeto().getMembros()) {

            if (membroRelacionamento.getEntidade() == regraUsuarioUmParaUm.getOpcaoSelecionada()) {

                membroFK = membroRelacionamento;
            } else {
                membroReferenciado = membroRelacionamento;
            }

        }

        Tabela tabelaReferenciada = (Tabela) findCorrespondencia(membroReferenciado.getEntidade());
        Tabela tabelaFK = (Tabela) findCorrespondencia(membroFK.getEntidade());

        createAtributos(regraUsuarioUmParaUm.getObjeto(), tabelaFK);

        FK fk = createFK(tabelaFK, tabelaReferenciada, regraUsuarioUmParaUm.getObjeto());
        if (fk != null) {
            fk.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAUM);
            fk.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAUM);


        }
    }

    private void replaceRegrasUmPraUm(Entidade entidadeFrom, Entidade entidadeTo, List<Relacionamento> relacionamentosReplace) {

        List<RegraUsuario> regrasChanged = new ArrayList<>();
        for (RegraUsuario regraUsuario : regrasUsuario) {

            if (regraUsuario instanceof RegraUsuarioUmParaUm
                    && Arrays.asList(regraUsuario.getOpcoes()).contains(entidadeFrom)) {
                try {
                    RegraUsuarioUmParaUm regraUsuarioCopy = ((RegraUsuarioUmParaUm) regraUsuario).clone();

                    for (Relacionamento relacionamento : relacionamentosReplace) {

                        if (relacionamento.equals(regraUsuario.getObjeto())) {
                            regraUsuarioCopy.setObjeto(relacionamento);
                        }
                    }
                    if (regraUsuario.getOpcoesSelecionadas()[0] == entidadeFrom) {
                        regraUsuarioCopy.setOpcaoSelecionada(entidadeTo);
                    }
                    regrasUsuario.add(regraUsuarioCopy);
                    regrasChanged.add(regraUsuario);
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(ConceitualToLogico.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        regrasUsuario.removeAll(regrasChanged);
    }

    private void converterAtributoMultivalorado(ObjetoWithAttribute objetoWhithAttribute, AtributoMultivalorado atributo) {
        //TODO:IMPLEMENTAR PARA atributos multivalorados na tabela


        Tabela tabelaAMv = null;
        boolean existsTabelaAtributo = false;
        for (Map.Entry<Objeto, Objeto> entry : conceitualLogicoCorrespondecia.entrySet()) {
            Objeto objetoConceitual = entry.getKey();
            Objeto objetoLogico = entry.getValue();

            if (objetoConceitual.equals(atributo)) {
                existsTabelaAtributo = true;
                tabelaAMv = (Tabela) objetoLogico;
                break;

            }
        }
        Tabela tabelaObjeto = (Tabela) findCorrespondencia(objetoWhithAttribute);

        if (!existsTabelaAtributo) {
            tabelaAMv = new Tabela();
            tabelaAMv.setNome(atributo.getNome());
            tabelaAMv.addPrefixo();
            if (atributo.getAttributes().size() > 0) {

                createAtributos(atributo, tabelaAMv);
            } else {
                tabelaAMv.addColuna(createColuna(atributo));
            }
        }
        FK fk = createFK(tabelaAMv, tabelaObjeto, atributo);

        if (fk != null) {
            fk.setCardinalidadeTabelaFK(CardinalidadeType.UMPARAUM);
            fk.setCardinalidadeTabelaReferenciada(CardinalidadeType.UMPARAMUITOS);
        }
        PK pk = tabelaAMv.getPrimaryKey();
        if (pk.getColunas().size() == 0) {

            pk.setColunas(tabelaAMv.getColunas());
            tabelaAMv.addConstraint(pk);
        }


        conceitualLogicoCorrespondecia.put(atributo, tabelaAMv);
    }

    private void createAtributos(ObjetoWithAttribute objetoWithAttribute, Tabela tabelaObjeto) {
        for (Atributo atributo : objetoWithAttribute.getAttributes()) {

            if (atributo instanceof AtributoMultivalorado) {

                atributosMultivalorados.put(objetoWithAttribute, (AtributoMultivalorado) atributo);

            } else if (atributo instanceof AtributoCompostoImpl) {
                AtributoCompostoImpl atributoCompostoImpl = (AtributoCompostoImpl) atributo;

                createAtributos(atributoCompostoImpl, tabelaObjeto);
            } else {

                Coluna coluna = createColuna(atributo);
                if (atributo.isPrimaryKey()) {

                    PK pk = tabelaObjeto.getPrimaryKey();

                    pk.addColuna(coluna);
                }
                tabelaObjeto.addColuna(coluna);
                conceitualLogicoCorrespondecia.put(atributo, coluna);
            }


        }
    }

    private Coluna createColuna(Atributo atributo) {
        Coluna coluna = new Coluna();
        coluna.setNome(atributo.getNome());
        coluna.setTipo(atributo.getTipo());
        coluna.setTamanho(atributo.getTamanho());
        coluna.setDescricao(atributo.getDescricao());

        return coluna;
    }

    private void changeRelacionamento(Entidade entidadeFrom, Entidade entidadeTo, Relacionamento relacionamento) {

        MembroRelacionamento membroRelacionamento = relacionamento.getMembro(entidadeFrom);

        membroRelacionamento.setEntidade(entidadeTo);

    }

    /**
     *
     * @param tabela
     * @param tabelaRefenciada
     * @param objetoRelacionamento objeto responsável pelo relacionamento
     * @return
     */
    private FK createFK(Tabela tabela, Tabela tabelaRefenciada, Objeto objetoRelacionamento) {


        if (!tabelaRefenciada.hasPrimaryKey()) {
            objetosSemPk.add(objetoRelacionamento);
            return null;
        } else {
            FK fk = new FK();
            fk.setTabelaReferenciada(tabelaRefenciada);

            //TODO:CRIAR NOT NULL NOS RELACIONAMENTOS 0:N e 0:1

            for (Coluna colunaReferenciada : tabelaRefenciada.getPrimaryKey().getColunas()) {

                Coluna coluna = CloneHandler.clone(colunaReferenciada);
                coluna.setNome(tabelaRefenciada.getNome() + "_" + colunaReferenciada.getNome());
                coluna.setOriginalHash(coluna.hashCode());

                tabela.addColuna(coluna);

                fk.putColunaComReferencia(coluna, colunaReferenciada);


            }
            tabela.addConstraint(fk);

            Indice indice = Indice.generateFKIndice(fk, tabela, false);
            tabela.addIndice(indice);
            return fk;
        }

    }

    private Objeto findCorrespondencia(Objeto objetoConceitualToFind) {

        for (Map.Entry<Objeto, Objeto> entry : conceitualLogicoCorrespondecia.entrySet()) {
            Objeto objetoConceitual = entry.getKey();
            Objeto objetoLogico = entry.getValue();

            if (objetoConceitual == objetoConceitualToFind) {
                return objetoLogico;
            }
        }
        return null;

    }

    private String getTabelaRelacionamentoNome(Relacionamento relacionamento) {

        StringBuilder nome = new StringBuilder();

        for (MembroRelacionamento membroRelacionamento : relacionamento.getMembros()) {
            nome.append(membroRelacionamento.getEntidade().getNome());
        }

        return nome.toString();
    }

    public List<Objeto> getObjetosSemPk() {
        return objetosSemPk;
    }
}
