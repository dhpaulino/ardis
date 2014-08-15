/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual.infoTable;

import ardis.model.ObjetoGrafico;
import ardis.view.GraphComponent;
import ardis.view.conceitual.DetalhesPanel;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSplitPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author Davisson
 */
public class ObjetoInfoHandler {

    private List<AbstractInfoTable> tables;
    private GraphComponent graphComponentAtual;
    private JSplitPane rightSplitPanel;
    private DetalhesPanel detalhesPanel;

    public ObjetoInfoHandler(DetalhesPanel detalhesPanel, JSplitPane rightSplitPanel) {

        this.detalhesPanel = detalhesPanel;
        this.rightSplitPanel = rightSplitPanel;
        tables = new ArrayList<>();
    }

    public void addGraphComponent(mxGraphComponent graphComponent) {

        final mxGraph graph = graphComponent.getGraph();

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                cellSelected(graph);

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2 && detalhesPanel.getTable() != null) {


                    detalhesPanel.setVisible(true);
                    detalhesPanel.getTable().setColumnSelectionAllowed(true);
                    detalhesPanel.getTable().setRowSelectionAllowed(true);
                    detalhesPanel.getTable().changeSelection(0, 0, false, false);

                    rightSplitPanel.setDividerLocation(900);
                }
            }
        });

        graph.getModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object o, mxEventObject eo) {
                cellSelected(graph);
            }
        });

    }

    public void cellSelected(mxGraph graph) {
        if (graph.getSelectionCell() != null && ((mxCell) graph.getSelectionCell()).getValue() != null) {
            ObjetoGrafico objetoClicked = (ObjetoGrafico) ((mxCell) graph.getSelectionCell()).getValue();
            setTable(objetoClicked);
        } else {
            //TODO:LIMPAR MODEL
            cleanTable();
        }
    }

    public void cleanTable() {
        detalhesPanel.removeTable();
        detalhesPanel.setVisible(false);
    }

    public void setGraphComponentAtual(GraphComponent graphComponentAtual) {
        this.graphComponentAtual = graphComponentAtual;
    }

    public AbstractInfoTable getInfoTable(ObjetoGrafico objeto) {
        try {
            AbstractInfoTable table = getTableModel(objeto.getClass());

            if (table == null) {
                final AbstractInfoTable finalTable = (AbstractInfoTable) objeto.getModelInfo().newInstance();
                
                finalTable.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("tableCellEditor".equals(evt.getPropertyName())) {
                            if (!finalTable.isEditing()) {
                                graphComponentAtual.newUndoableEdit();
                                graphComponentAtual.getGraph().refresh();
                            }
                        }
                    }
                });
                finalTable.getModel().addTableModelListener(new TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent e) {
                        graphComponentAtual.getGraph().refresh();
                    }
                });
                return finalTable;
            }
            return table;

        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ObjetoInfoHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setTable(ObjetoGrafico objeto) {
        AbstractInfoTable table = getInfoTable(objeto);

        table.getModel().setObjeto(objeto);

        detalhesPanel.setTable(table);

    }

    private AbstractInfoTable getTableModel(Class tableModelClass) {

        for (AbstractInfoTable tableModel : tables) {
            if (tableModelClass.equals(tableModel.getClass())) {

                return tableModel;
            }
        }

        return null;
    }
}
