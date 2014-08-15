/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.logico.constraint.FK;
import ardis.model.logico.tabela.Tabela;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davisson
 */
public class LogicoLayout {

    private List<mxCell> ordenedCells;
    private static LogicoLayout instance;

    private LogicoLayout() {
    }

    public void organizar(mxGraph graph, List<mxCell> tableCells) {

        ordenedCells = new ArrayList<>();

        for (mxCell cell : tableCells) {

            insertCellOrdened(cell);
        }

        try {
            graph.getModel().beginUpdate();
            double x = graph.getGraphBounds().getCenterX();
            double y = graph.getGraphBounds().getCenterY();

            int cont = 3;
            for (mxCell cell : ordenedCells) {
                cont++;

                int resto = cont % 4;
                cell.getGeometry().setX(x);
                cell.getGeometry().setY(y);
            }
        } finally {
            graph.getModel().endUpdate();
        }

    }

    private void insertCellOrdened(mxCell cell) {
        Tabela tabela = (Tabela) cell.getValue();

        int qtdRelacionamentos = tabela.getConstraintsByClass(FK.class).size();


        int ordenedIndex = -1;
        for (mxCell ordenedCell : ordenedCells) {
            ordenedIndex++;
            Tabela ordenedTabela = (Tabela) ordenedCell.getValue();

            if (ordenedTabela.getConstraintsByClass(FK.class).size() < qtdRelacionamentos) {
                ordenedCells.add(ordenedIndex, cell);
                return;
            }
        }
        ordenedCells.add(cell);
    }

    public static LogicoLayout getInstance() {
        if (instance == null) {
            instance = new LogicoLayout();
        }

        return instance;
    }
}
