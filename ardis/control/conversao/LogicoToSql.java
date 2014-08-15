package ardis.control.conversao;

import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.coluna.TipoAtributo;
import ardis.model.logico.constraint.Check;
import ardis.model.logico.constraint.Constraint;
import ardis.model.logico.constraint.Default;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.constraint.NotNull;
import ardis.model.logico.constraint.PK;
import ardis.model.logico.constraint.Unique;
import ardis.model.logico.indice.Indice;
import ardis.model.logico.indice.TipoIndice;
import ardis.model.logico.tabela.Tabela;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogicoToSql {
    
    private List<Coluna> colunas;
    private List<Tabela> tabelas;
    private int nPK;
    private int nUnique;
    private int nFK;
    private int nCheck;
    private int qtdFK;
    private int nIndice;
    
    public LogicoToSql() {
        colunas = new ArrayList<Coluna>();
        tabelas = new ArrayList<Tabela>();
    }
    
    public void setColunas(List<Coluna> colunas) {
        this.colunas = colunas;
    }
    
    public void setTabelas(List<Tabela> tabelas) {
        this.tabelas = tabelas;
    }
    
    public int getTableCount() {
        return tabelas.size();
    }
    
    public int getRowCount() {
        return colunas.size();
    }
    
    public void createDoc(String path) throws IOException {
        
        try {
            FileWriter localSQL = new FileWriter(path + ".sql");
            PrintWriter gravarArquivo = new PrintWriter(localSQL);
            
            gravarArquivo.println("CREATE DATABASE db_ARDIS;");
            gravarArquivo.println("USE db_ARDIS;");
            gravarArquivo.println("");
            
            for (Tabela tabela : tabelas) {
                nPK = 0;
                addTabela(gravarArquivo, tabela);
            }
            
            for (Tabela tabela : tabelas) {
                addAlterFk(gravarArquivo, tabela);
            }
            
            for (Tabela tabela : tabelas) {
                nUnique = 0;
                addAlterTableUnique(gravarArquivo, tabela);
            }
            
            for (Tabela tabela : tabelas) {
                nIndice = 0;
                addIndice(gravarArquivo, tabela);
            }
            
            
            FileOutputStream outStream = null;
            gravarArquivo.flush();
            gravarArquivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void addAlterTableUnique(PrintWriter document, Tabela tabela) {
        boolean isUnique = false;
        int nRows = tabela.getColunas().size() + 1;
        for (int i = 1; i < nRows; i++) {
            Coluna coluna = tabela.getColunas().get(i - 1);
            String unique = tabela.getConstraintByColuna(coluna, Unique.class) != null ? "true" : "";
            if (unique.equals("true")) {
                isUnique = true;
            }
        }
        if (isUnique == true) {
            document.print("ALTER TABLE " + tabela.getNome() + " ADD CONSTRAINT unique_" + tabela.getNome() + " UNIQUE(");
            for (int i = 1; i < nRows; i++) {
                Coluna coluna = tabela.getColunas().get(i - 1);
                addColunaUnique(document, coluna, tabela);
            }
            document.println(");");
        }
    }
    
    private void addAlterFk(PrintWriter document, Tabela tabela) {
        
        List<Constraint> constraints = tabela.getConstraints();
        
        for (Constraint constraint : constraints) {
            
            if (constraint instanceof FK) {
                
                FK fk = (FK) constraint;
                qtdFK++;
                
                nFK = 0;
                for (Map.Entry<Coluna, Coluna> colunasComReferencia : fk.getColunasComReferencias().entrySet()) {
                    String nomeFk = colunasComReferencia.getKey().getNome();
                    addColunaFk(document, nomeFk, tabela);
                }
                
                nFK = 0;
                for (Map.Entry<Coluna, Coluna> colunasComReferencia : fk.getColunasComReferencias().entrySet()) {
                    String nomeReferenciada = colunasComReferencia.getValue().getNome();
                    String nomeTabela = fk.getTabelaReferenciada().getNome();
                    addColunaFk1(document, nomeReferenciada, tabela, nomeTabela);
                }
                document.println(");");
            }
        }
    }
    
    private void addTabela(PrintWriter document, Tabela tabela) {
        document.println("CREATE TABLE " + tabela.getNome() + "(");
        
        int nRows = tabela.getColunas().size() + 1;
        for (int i = 1; i < nRows; i++) {
            Coluna coluna = tabela.getColunas().get(i - 1);
            addColuna(document, coluna, tabela);
            if (i < nRows - 1) {
                document.println(",");
            }
            
        }
        int unicaPK = 0;
        List<Constraint> constraints = tabela.getConstraints();
        
        for (Constraint constraint : constraints) {
            
            if (constraint instanceof PK && unicaPK == 0) {
                
                unicaPK++;
                PK pk = (PK) constraint;
                
                nPK = 0;
                int numeroPK = pk.getColunas().size();
                for (Coluna colunasPk : pk.getColunas()) {
                    String nomePk = colunasPk.getNome();
                    addColunaPk(document, nomePk, tabela, numeroPK);
                }
            }
        }
        
        boolean isCheck = false;
        for (int i = 1; i < nRows; i++) {
            Coluna coluna = tabela.getColunas().get(i - 1);
            String check = tabela.getConstraintByColuna(coluna, Check.class) != null ? "true" : "";
            if (check.equals("true")) {
                isCheck = true;
            }
        }
        
        if (isCheck) {
            document.println(",");
            document.print("   CONSTRAINT check_" + tabela.getNome() + " CHECK(");
            for (int i = 1; i < nRows; i++) {
                Coluna coluna = tabela.getColunas().get(i - 1);
                addColunaCheck(document, coluna, tabela);
            }
            document.println(")");
        } else if (isCheck == false) {
            document.println("");
        }
        
        document.println(");");
        document.println("");
    }
    
    private void addColuna(PrintWriter row, Coluna coluna, Tabela tabela) {
        Default constraintDefault = tabela.getConstraintByColuna(coluna, Default.class);
        String constraint = tabela.getConstraintByColuna(coluna, Default.class) != null ? "true" : "";
        String notNull = tabela.getConstraintByColuna(coluna, NotNull.class) != null ? "not null" : "";
        
        row.print("   " + coluna.getNome());
        if (coluna.getTipo() != null) {
            row.print(" " + coluna.getTipo().toString());
        }
        if (coluna.getTipo() == TipoAtributo.NUMERIC || coluna.getTipo() == TipoAtributo.DECIMAL
                || coluna.getTipo() == TipoAtributo.FLOAT || coluna.getTipo() == TipoAtributo.CHAR
                || coluna.getTipo() == TipoAtributo.VARCHAR) {
            row.print("(" + coluna.getTamanho() + ")");
        }
        if (notNull == "not null") {
            row.print(" " + notNull);
        }
        if (constraint.equals("true")) {
            row.print(" DEFAULT " + constraintDefault.getValor());
        }
    }
    
    private void addColunaCheck(PrintWriter row, Coluna coluna, Tabela tabela) {
        Check chk = tabela.getConstraintByColuna(coluna, Check.class);
        String regra = chk != null ? chk.getRegra() : "";
        String check = regra != "" ? "true" : "";
        
        if (check.equals("true")) {
            if (nCheck > 0) {
                row.print(" AND ");
            }
            nCheck++;
            row.print("" + coluna.getNome() + regra);
        }
    }
    
    private void addColunaPk(PrintWriter row, String nomePk, Tabela tabela, int numeroPK) {
        
        if (nPK == 0) {
            row.println(",");
            row.print("   CONSTRAINT pk_" + tabela.getNome() + " primary key(");
        }
        if (nPK > 0) {
            row.print(",");
        }
        nPK++;
        row.print(nomePk);
        if (nPK == numeroPK) {
            row.print(")");
        }
    }
    
    private void addColunaFk1(PrintWriter row, String coluna, Tabela tabela, String nomeTabela) {
        
        if (nFK == 0) {
            row.print(") REFERENCES " + nomeTabela + "(");
        } else if (nFK > 0) {
            row.print(",");
        }
        nFK++;
        row.print(coluna);
    }
    
    private void addColunaFk(PrintWriter row, String coluna, Tabela tabela) {
        if (nFK == 0) {
            row.print("ALTER TABLE " + tabela.getNome() + " ADD CONSTRAINT fk_" + tabela.getNome() + "_" + qtdFK + " FOREIGN KEY(");
        } else if (nFK > 0) {
            row.print(",");
        }
        nFK++;
        row.print(coluna);
    }
    
    private void addColunaUnique(PrintWriter row, Coluna coluna, Tabela tabela) {
        String unique = tabela.getConstraintByColuna(coluna, Unique.class) != null ? "true" : "false";
        
        if (unique.equals("true")) {
            if (nUnique > 0) {
                row.print(",");
            }
            nUnique++;
            row.print("" + coluna.getNome());
        }
    }
    
    private void addIndice(PrintWriter document, Tabela tabela) {
        List<Indice> indices = tabela.getIndices();
        
        List<Constraint> constraints = tabela.getConstraints();
        
        for (Indice constraint : indices) {
            
            Indice indice = (Indice) constraint;
            
            nIndice = 0;
            int numeroIndice = indice.getColunas().size();
            if (numeroIndice > 0) {
                document.println("");
                document.println("/*Only works on MySQL*/");
                if (indice.getTipo() == TipoIndice.PRIMARY) {
                    document.print("/*");
                }
            }
            for (Coluna colunasIndice : indice.getColunas()) {
                String nomeColunaIndice = colunasIndice.getNome();
                String nomeIndice = indice.getNome();
                String tipoIndice = indice.getTipo().toString();
                addColunaIndice(document, nomeColunaIndice, nomeIndice, tipoIndice, tabela, numeroIndice);
            }
            if (numeroIndice > 0) {
                if (indice.getTipo() == TipoIndice.PRIMARY) {
                    document.println("*/");
                }
            }
        }
    }
    
    private void addColunaIndice(PrintWriter row, String nomeColunaIndice, String nomeIndice,
            String tipo, Tabela tabela, int numeroIndice) {
        
        if (nIndice == 0) {
            row.print("CREATE ");
            if (!"INDEX".equals(tipo)) {
                row.print(tipo);
            }
            row.print(" INDEX index_" + nomeIndice + " ON " + tabela.getNome() + "(");
        }
        if (nIndice > 0) {
            row.print(",");
        }
        nIndice++;
        row.print(nomeColunaIndice);
        if (nIndice == numeroIndice) {
            row.print(");");
        }
    }
}
