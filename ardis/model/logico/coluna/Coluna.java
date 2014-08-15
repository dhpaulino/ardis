package ardis.model.logico.coluna;

import ardis.model.Objeto;
import ardis.model.ObjetoGraficoImpl;
import ardis.model.logico.tabela.Tabela;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.io.Serializable;

public class Coluna extends ObjetoGraficoImpl implements Serializable, Objeto{

    private TipoAtributo tipo;
    private String tamanho;
    private boolean autoIncrement;

    public Coluna() {
        this.nome = "Coluna";
        tipo = TipoAtributo.INT;
        tamanho = 11+"";
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public TipoAtributo getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtributo tipo) {
        this.tipo = tipo;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    @Override
    public String toString() {
        return this.nome + " " + tipo.toString();
    }

    public mxCell render(mxGraph graph, mxCell parent) {
        Tabela tabela = (Tabela) parent.getValue();
        int y =  (tabela.getColunas().indexOf(this)) * 20;
        y += 40;
        return initialRender(graph, parent, 0, y);
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {
        double width = graph.getCellGeometry(parent).getWidth();
        return (mxCell) graph.insertVertex(parent, null, this, xPosition, yPosition, width, 20, "column");
    }

    @Override
    public Coluna clone() throws CloneNotSupportedException {
        return (Coluna) super.clone();
    }

}
