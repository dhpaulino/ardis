/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.model.Modelo;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.tabela.Tabela;
import ardis.ultil.CloneHandler;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davisson
 */
public class UndoManager extends mxUndoManager {

    private GraphComponent graphComponent;
    private List<Modelo> modelos;

    public UndoManager(GraphComponent graphComponent) {
        this.graphComponent = graphComponent;
        this.modelos = new ArrayList<>();
    }

    @Override
    public void undoableEditHappened(mxUndoableEdit undoableEdit) {
        super.undoableEditHappened(undoableEdit);
      
        if (size > 0 && size == modelos.size()) {
            modelos.remove(0);
        }

        try {
            modelos.add(CloneHandler.clone(graphComponent.getModelo()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void trim() {
        while (history.size() > indexOfNextAdd) {
            mxUndoableEdit edit = history.remove(indexOfNextAdd);
            edit.die();
            modelos.remove(indexOfNextAdd);
        }
    }

    @Override
    public void undo() {
        while (indexOfNextAdd > 0) {
            mxUndoableEdit edit = history.get(--indexOfNextAdd);

            for (mxUndoableEdit.mxUndoableChange change : edit.getChanges()) {
                if (change instanceof mxGraphModel.mxChildChange) {
                    mxGraphModel.mxChildChange childChange = (mxGraphModel.mxChildChange) change;
                    mxCell parent = (mxCell) childChange.getParent();
                    mxCell previous = (mxCell) childChange.getPrevious();
                    mxCell child = (mxCell) childChange.getChild();
                    if (parent != null && parent.getValue() != null && parent.getValue() instanceof Tabela) {
                        Coluna coluna = (Coluna) child.getValue();
                        if (((Tabela) parent.getValue()).getConstraintByColuna(coluna, FK.class) != null) {
                            if (indexOfNextAdd - 1 > 0) {
                                edit.undo();
                                edit = history.get(--indexOfNextAdd);
                            }
                        }
                    }
                    if(previous != null && previous.getValue() != null && previous.getValue() instanceof Tabela){
                        for(Object child1 : graphComponent.getGraph().getChildCells(previous)){
                            mxCell filha = (mxCell)child1;
                            if(filha.getGeometry().getY() >= child.getGeometry().getY()){
                                filha.getGeometry().setY(filha.getGeometry().getY() + 20);
                            }
                        }
                        previous.getGeometry().setHeight(previous.getGeometry().getHeight() + 20);
                        graphComponent.getGraph().refresh();
                    }
                }
            }
            edit.undo();

                if (history.indexOf(edit) - 1 < 0) {
                graphComponent.setModelo(graphComponent.createModelo());
            } else {
                graphComponent.setModelo(modelos.get(history.indexOf(edit) - 1));
            }
            graphComponent.afterUndoChange();
            if (edit.isSignificant()) {
                fireEvent(new mxEventObject(mxEvent.UNDO, "edit", edit));
                break;
            }
        }
    }

    @Override
    public void redo() {
        int n = history.size();

        while (indexOfNextAdd < n) {
            mxUndoableEdit edit = history.get(indexOfNextAdd++);
            for (mxUndoableEdit.mxUndoableChange change : edit.getChanges()) {
                if (change instanceof mxGraphModel.mxChildChange) {
                    mxGraphModel.mxChildChange childChange = (mxGraphModel.mxChildChange) change;
                    mxCell child = (mxCell) childChange.getChild();
                    if (child != null && child.getValue() != null && child.getValue() instanceof FK) {
                            edit.redo();
                            edit = history.get(indexOfNextAdd++);
                    }
                }
            }
            edit.redo();
            graphComponent.setModelo(modelos.get(history.indexOf(edit)));
            graphComponent.afterUndoChange();
            if (edit.isSignificant()) {
                fireEvent(new mxEventObject(mxEvent.REDO, "edit", edit));
                break;
            }
        }
    }

    public Modelo getLastModelo() {
        return modelos.get(history.size() - 1);
    }
}
