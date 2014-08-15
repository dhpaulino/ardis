package ardis.model.logico.tabela;

import ardis.model.ObjetoGraficoImpl;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.Constraint;
import ardis.model.logico.constraint.PK;
import ardis.model.logico.indice.Indice;
import ardis.model.logico.indice.TipoIndice;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tabela extends ObjetoGraficoImpl {

    private List<Coluna> colunas;
    private List<Indice> indices;
    private List<Constraint> constraints;

    public Tabela() {
        nome = "Tabela";
        colunas = new ArrayList<Coluna>();
        constraints = new ArrayList<Constraint>();
        indices = new ArrayList<Indice>();
    }

    public PK getPrimaryKey() {
        for (Constraint constraint : constraints) {
            if (constraint instanceof PK) {
                return (PK) constraint;
            }
        }
        PK primaryKey = new PK();
        constraints.add(primaryKey);
        indices.add(primaryKey.getIndice());
        return primaryKey;
    }

    public List<Coluna> getColunas() {
        return colunas;
    }

    public void setColunas(List<Coluna> colunas) {
        this.colunas = colunas;
    }

    public void addColuna(Coluna coluna) {
        this.colunas.add(coluna);
    }

    public void removeColuna(Coluna coluna) {
        this.colunas.remove(coluna);
        Iterator<Constraint> constraintsIterator = constraints.iterator();
        while (constraintsIterator.hasNext()) {
            Constraint constraint = constraintsIterator.next();
            constraint.getColunas().remove(coluna);
            if (constraint.getColunas().isEmpty() && !(constraint instanceof PK)) {
                constraintsIterator.remove();
            }
        }
        Iterator<Indice> indicesIterator = indices.iterator();
        while (indicesIterator.hasNext()) {
            Indice indice = indicesIterator.next();
            indice.getColunas().remove(coluna);
            if (indice.getColunas().isEmpty() && indice.getTipo() != TipoIndice.PRIMARY) {
                indicesIterator.remove();
            }
        }
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    public List<Indice> getIndices() {
        return indices;
    }

    public void addIndice(Indice indice) {
        this.indices.add(indice);
    }

    public void setIndices(List<Indice> indices) {
        this.indices = indices;
    }

    public <T extends Constraint> T getConstraintByColuna(Coluna coluna, Class<T> constraintClass) {
        for (Constraint constraint : constraints) {
            if (constraint.getColunas().contains(coluna) && constraint.getClass().isAssignableFrom(constraintClass)) {
                return (T) constraint;
            }
        }
        return null;
    }

    public <T extends Constraint> List<T> getConstraintsByClass(Class<T> classe) {
        List<T> constraintsRetorno = new ArrayList<>();
        for (Constraint constraint : constraints) {
            if (constraint.getClass().isAssignableFrom(classe)) {
                constraintsRetorno.add((T) constraint);
            }
        }
        return constraintsRetorno;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {
        return (mxCell) graph.insertVertex(parent, null, this, xPosition, yPosition, 150, 40, "table");
    }

    public boolean hasPrimaryKey() {
        return !getPrimaryKey().getColunas().isEmpty();
    }

    public void addPrefixo() {
        nome = "tb" + nome;
    }
}
