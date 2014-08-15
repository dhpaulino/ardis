package ardis.model.conceitual.atributo;

import ardis.model.ObjetoGraficoImpl;
import ardis.model.conceitual.atributo.infoTable.AtributoInfoTable;
import ardis.model.logico.coluna.TipoAtributo;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class Atributo extends ObjetoGraficoImpl {

    private TipoAtributo tipo;
    protected boolean primaryKey = false;
    private String tamanho;

    public Atributo() {
        this.nome = "Atributo";
        tipo = TipoAtributo.INT;
        tamanho = "11";
    }

    public Atributo(boolean primaryKey) {
        this();
        this.primaryKey = primaryKey;
    }

    public Atributo(String nome) {
        this();
        this.nome = nome;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    
    public TipoAtributo getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtributo tipo) {
        this.tipo = tipo;
    }

    @Override
    public Class getModelInfo() {
        return AtributoInfoTable.class;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {

        return (mxCell) graph.insertVertex(parent, null, this, xPosition, yPosition, 12, 12, getStyle());
    }

    private String getStyle() {
        return primaryKey ? "primaryKey" : "attribute";
    }

    @Override
    public Atributo clone() throws CloneNotSupportedException {
        return (Atributo) super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLimitLabelSize() {
        return false;
    }
}
