/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.model.Objeto;
import ardis.model.conceitual.atributo.Atributo;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.JScrollBar;

/**
 *
 * @author Davisson
 */
public class Graph extends mxGraph {

    private boolean generateCellName = true;
    private GraphComponent graphComponent;
    
    public Graph() {
        super();
      
    }

    @Override
    public Object[] resizeCells(Object[] cells, mxRectangle[] bounds) {

        int cont = 0;

        List<Object> cellsList = Arrays.asList(cells);
        for (Iterator<Object> it = cellsList.iterator(); it.hasNext();) {
            mxCell cell = (mxCell) it.next();


            if (cell.getValue() instanceof Atributo) {
                it.remove();
    }
            cont++;
        }

        return super.resizeCells(cellsList.toArray(), bounds); //To change 
    }

    @Override
    public boolean isValidDropTarget(Object cell, Object[] cells) {
        return false;
    }

    @Override
    public boolean isCellConnectable(Object cell) {
        return false;
    }

    @Override
    public boolean isCellDisconnectable(Object cell, Object terminal, boolean source) {
        return false;
    }

    public boolean isGenerateCellName() {
        return generateCellName;
    }

    public void setGenerateCellName(boolean generateCellName) {
        this.generateCellName = generateCellName;

    }

    @Override
    public Object insertVertex(Object parent, String id, Object value, double x, double y, double width, double height, String style, boolean relative) {
        generateNameCell(value, parent);
        return super.insertVertex(parent, id, value, x, y, width, height, style, relative); //To change body of generated methods, choose Tools | Templates.
    }

    public void generateNameCell(Object value, Object parent) {

        if (generateCellName) {
            Objeto objeto = (Objeto) value;

            if (objeto != null) {
                int count = 0;
                for (Object cellModelo : getChildCells(parent)) {

                    Objeto cellModeloValue = (Objeto) ((mxCell) cellModelo).getValue();
                    if (cellModeloValue != null && cellModeloValue.getClass().equals(objeto.getClass())
                            && cellModeloValue.getNome()
                            .matches("^" + objeto.getNome() + "_\\d+")) {
                        String[] splitString = cellModeloValue.getNome().split("_");
                        count = Integer.parseInt(splitString[splitString.length - 1]);
                    }
                }
                objeto.setNome(objeto.getNome() + "_" + (count + 1));
            }
        }
    }

    @Override
    public Object[] moveCells(Object[] cells, double dx, double dy, boolean clone, Object target, Point location) {
        double diferencaX = 0;
        double diferencaY = 0;
        double maxX = 0;
        double maxY = 0;
        for (Object cell : cells) {
            mxGeometry geo = (mxGeometry) ((mxCell) cell).getGeometry().clone();
            geo.translate(dx, dy);
            if(geo.getX() + geo.getWidth() > maxX){
                maxX = geo.getX() + geo.getWidth();
            }
            if(geo.getY() + geo.getHeight() > maxY){
                maxY = geo.getY() + geo.getHeight();
            }
            if (geo.getX() < 0) {
                diferencaX = Math.max(diferencaX, -geo.getX());
            }
            if (geo.getY() < 0) {
                diferencaY = Math.max(diferencaY, -geo.getY());
            }
        }
        dx += diferencaX;
        dy += diferencaY;
        
        mxRectangle tamGraph = graphComponent.getLayoutAreaSize();
        if (maxX > tamGraph.getWidth() || maxY > tamGraph.getHeight()) {
            double difX = maxX - tamGraph.getWidth();
            double difY = maxY - tamGraph.getHeight();
            Dimension preferredSize = new Dimension((int) (difX > 0 ? tamGraph.getWidth() + difX : tamGraph.getWidth()),
                    (int) (difY > 0 ? tamGraph.getHeight() + difY : tamGraph.getHeight()));
            graphComponent.setScaledPreferredSizeForGraph(preferredSize);
            graphComponent.getGraphControl().updatePreferredSize();
            JScrollBar scrollBar;
            if (difX > 0) {
                scrollBar = graphComponent.getHorizontalScrollBar();
                scrollBar.setValue(scrollBar.getMaximum());
            }
            if (difY > 0) {
                scrollBar = graphComponent.getVerticalScrollBar();
                scrollBar.setValue(scrollBar.getMaximum());
            }
        }
        Object[] cellsMoved = super.moveCells(cells, dx, dy, clone, target, location);

//        SeparateEdgesLayout layout = new SeparateEdgesLayout(this);
//        for (Object object : cellsMoved) {
//            layout.execute(object);
//        }
//        refresh();
        return cellsMoved;
    }

    public mxCell getCellByValue(Object value) {
        return getCellByValue(value, getDefaultParent());
    }

    public mxCell getCellByValue(Object value, Object parent) {
        return getCellByValue(value, getChildCells(parent));
    }

    public mxCell getCellByValue(Object value, Object[] cells) {
        for (Object childCell : cells) {
            mxCell cell = (mxCell) childCell;

            if (cell.getValue() != null && cell.getValue().equals(value)) {
                return cell;
            }
            mxCell findCell = getCellByValue(value, cell);
            if (findCell != null) {
                return findCell;
            }
        }
        return null;
    }

    public GraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void setGraphComponent(GraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }
}
