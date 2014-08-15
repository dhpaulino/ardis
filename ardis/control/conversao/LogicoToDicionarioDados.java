package ardis.control.conversao;

import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.Check;
import ardis.model.logico.constraint.Default;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.constraint.NotNull;
import ardis.model.logico.constraint.PK;
import ardis.model.logico.constraint.Unique;
import ardis.model.logico.tabela.Tabela;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class LogicoToDicionarioDados {

    private List<Tabela> tabelas;

    public List<Tabela> getTabelas() {
        return tabelas;
    }

    public void setTabelas(List<Tabela> tabelas) {
        this.tabelas = tabelas;
    }

    public void createDoc(String path) {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph paragraphOne = document.createParagraph();
        XWPFRun titulo = paragraphOne.createRun();
        titulo.setBold(true);
        titulo.setFontSize(20);
        titulo.setText("Dicionário de Dados");
        document.createParagraph().createRun().addBreak();

        for (Tabela tabela : tabelas) {
            addTabela(document, tabela);
            document.createParagraph().createRun().addBreak();
        }
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(path + ".doc");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            document.write(outStream);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTabela(XWPFDocument document, Tabela tabela) {
        int nRows = tabela.getColunas().size() + 1;

        XWPFTable table = document.createTable(nRows, 9);
        
        XWPFTableRow rowOne = table.getRow(0);
        rowOne.setHeight(1);
        rowOne.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(0).setText(" Nome ");
        rowOne.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(1).setText(" Descrição ");
        rowOne.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(2).setText(" Tipo ");
        rowOne.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(3).setText(" Tamanho ");
        rowOne.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(4).setText(" Nulo ");
        rowOne.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(5).setText(" Regra(Check) ");
        rowOne.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(6).setText(" Chave ");
        rowOne.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(7).setText(" Default ");
        rowOne.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        rowOne.getCell(8).setText(" Unique ");

        for (int i = 1; i < nRows; i++) {
            Coluna coluna = tabela.getColunas().get(i - 1);
            addColuna(table.getRow(i), coluna, tabela);
        }
        XWPFParagraph paragraphOne = document.createParagraph();
        XWPFRun titulo = paragraphOne.createRun();
        titulo.setText(tabela.getNome());
    }

    private void addColuna(XWPFTableRow row, Coluna coluna, Tabela tabela) {
        Default constraintDefault = tabela.getConstraintByColuna(coluna, Default.class);
        Check check = tabela.getConstraintByColuna(coluna, Check.class);
        FK foreignKey = tabela.getConstraintByColuna(coluna, FK.class);
        String autoIncrement = coluna.isAutoIncrement() ? "Sim" : "Não";
        String notNull = tabela.getConstraintByColuna(coluna, NotNull.class) != null ? "Sim" : "Não";
        String regra = check != null ? check.getRegra() : "";
        String pk = tabela.getConstraintByColuna(coluna, PK.class) != null ? "PK" : "";
        String fk = foreignKey != null ? "FK referencia " + foreignKey.getTabelaReferenciada()
                + " (" + foreignKey.getColunasComReferencias().get(coluna).getNome() + ")" : "";
        String valor = coluna.getTamanho();
        String unique = tabela.getConstraintByColuna(coluna, Unique.class) != null ? "Sim" : "Não";
        String descricao = coluna.getDescricao() != null ? coluna.getDescricao() : "";

        row.setHeight(1);
        if (coluna.getNome().length() >= 10) {
            for (int i = 1; i <= coluna.getNome().length(); i++) {
                row.getCell(0).setText(coluna.getNome().substring(i - 1, i));
                if (i == 10 || i == 20 || i == 30 || i == 40 || i == 50 || i == 60) {
                    row.getCell(0).setText(" ");
                }
            }
        } else {
            row.getCell(0).setText(" " + coluna.getNome() + " ");
        }
        row.getCell(0).setText(" ");
        row.getCell(1).setText(" " + descricao + " ");
        row.getCell(2).setText(" " + coluna.getTipo().toString() + " ");
        row.getCell(3).setText(" " + valor + " ");
        row.getCell(4).setText(" " + notNull + " ");
        row.getCell(5).setText(" " + regra + " ");
        row.getCell(6).setText(" " + pk + " " + fk + " ");
        row.getCell(7).setText(" " + autoIncrement + " ");
        row.getCell(8).setText(" " + unique + " ");
    }
}
