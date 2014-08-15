/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.model.logico.tabela.Tabela;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Israel
 */
public class SeparateEdgesLayout extends mxGraphLayout{
    
    private int spacing;
    private mxCell parentCell;
    
    public SeparateEdgesLayout(mxGraph graph) {
        super(graph);
        spacing = 10;
    }

    @Override
    public void execute(Object parent) {
        super.execute(parent);
        if(parent == graph.getDefaultParent()){
            for (Object childCell : graph.getChildCells(parent)) {
                execute(childCell);
            }
        }
        parentCell = (mxCell)parent;
        if(parentCell.getValue() instanceof Tabela){
            spacing = 20;
        }
        changeRepeatedPoints(getPointsMap());
        graph.refresh();
    }
    
    private void changeRepeatedPoints(HashMap<mxPoint,List<mxCell>> pointsMap){
        for(Map.Entry<mxPoint,List<mxCell>> entry : pointsMap.entrySet()){
            if(entry.getValue().size() > 1){
                int edgeCount = 0;
                int right = 0;
                int left = 0;
                List<mxCell> edges = sortEdges(entry.getValue(), !isOnSides(entry.getKey()));
                for(mxCell edge : edges){
                    if(edge != null){
                        List<mxPoint> points = new ArrayList<>();

                        if (entry.getValue().size() % 2 != 0 && edgeCount+1 == (edges.size()+1)/2) {
                            points.add(graph.getView().getNextPoint(graph.getView().getState(edge,true), graph.getView().getState(parent,true), false));
                        } else if (edgeCount+1 <= edges.size()/2) {
                            right++;
                            points.add(getPoint(entry.getKey(), getEndPoint(edge), right, 1));
                        } else {
                            left++;
                            points.add(getPoint(entry.getKey(), getEndPoint(edge), left, -1));
                        }

                        edge.getGeometry().setPoints(points);
                        edgeCount++;
                    }
                }
            }
        }
    }
    
    private boolean isOnSides(mxPoint point){
        long parentSizeY = Math.round(parentCell.getGeometry().getY() + parentCell.getGeometry().getHeight());
        long parentSizeX = Math.round(parentCell.getGeometry().getX() + parentCell.getGeometry().getWidth());
        
        if((point.getX() == parentSizeX || point.getX() == Math.round(parentCell.getGeometry().getX())) 
                && point.getY() != parentSizeY && point.getY() != Math.round(parentCell.getGeometry().getY()))
        {
            return true;
        }else{
            return false;
        }
    }
    
    private mxPoint getEndPoint(mxCell edge){
        if(edge == null){
            Exception ex = new Exception();
            ex.printStackTrace();
        }
        mxPoint endPoint = new mxPoint();
        mxCellState state = graph.getView().getState(edge,true);
        if(state != null){
            int pointsCount = state.getAbsolutePointCount();
            List<mxPoint> absolutePoints = state.getAbsolutePoints();
            endPoint = parent == edge.getSource() ? absolutePoints.get(pointsCount - 1) : absolutePoints.get(0);
        }
        return endPoint;
    }
    
    private mxPoint getPoint(mxPoint commonPoint, mxPoint endPoint, int count, int position) {
        double x = (endPoint.getX() + commonPoint.getX()) / 2;
        double y = (endPoint.getY() + commonPoint.getY()) / 2;
        
        if (isOnSides(commonPoint)) {
            if (position == 1) {
                y = commonPoint.getY() - (spacing * count);
            } else {
                y = commonPoint.getY() + (spacing * count);
            }
            if(isOutsideBounds(endPoint, true, position)){
                x = commonPoint.getX() + (spacing * count);
            }
        } else {
            if (position == 1) {
                x = commonPoint.getX() + (spacing * count);
            } else {
                x = commonPoint.getX() - (spacing * count);
            }
            if(isOutsideBounds(endPoint, false, position)){
                y = commonPoint.getY() + (spacing * count);
            }
        }
        return new mxPoint(x, y);
    }
    
    private boolean isOutsideBounds(mxPoint endPoint, boolean isOnSides, int position){
        if(isOnSides){
            if(position == 1){
                return Math.round(parentCell.getGeometry().getY()) > endPoint.getY();
            }else{
                long parentSizeY = Math.round(parentCell.getGeometry().getY() + parentCell.getGeometry().getHeight());
                return parentSizeY < endPoint.getY();
            }
        }else{
            if(position == 1){
                long parentSizeX = Math.round(parentCell.getGeometry().getX() + parentCell.getGeometry().getWidth());
                return parentSizeX < endPoint.getX();
            }else{
                return Math.round(parentCell.getGeometry().getX()) > endPoint.getX();
            }
        }
    }
    
    private List<mxCell> sortEdges(List<mxCell> edges, boolean sortByX){
        mxCell[] sortedEdges = new mxCell[edges.size()];
        for(int i=0;i<edges.size();i++){
            int position = 0;
            for(mxCell edge : edges){
                if(edge != edges.get(i)){
                    if(compareEdges(edges.get(i), edge, sortByX) == -1){
                        position++;
                    }
                }
            }
            sortedEdges[position] = edges.get(i);
        }
        return Arrays.asList(sortedEdges);
    }
    
    private int compareEdges(mxCell edge1, mxCell edge2, boolean compareX){
        mxPoint point1 = getEndPoint(edge1);
        mxPoint point2 = getEndPoint(edge2);
        if(!compareX){
            if(point1.getY() < point2.getY() || point1.getY() == point2.getY()){
                return 1;
            }else{
                return -1;
            }
        }else{
            if(point1.getX() > point2.getX() || point1.getX() == point2.getX()){
                return 1;
            }else{
                return -1;
            }
        }
    }
    
    private HashMap<mxPoint,List<mxCell>> getPointsMap(){
        HashMap<mxPoint,List<mxCell>> pointsMap = new HashMap<>();
        for(int i=0;i<parentCell.getEdgeCount();i++){
            mxCell edge = (mxCell)parentCell.getEdgeAt(i);
            mxCellState state = graph.getView().getState(edge);
            int pointsCount = state.getAbsolutePointCount();
            List<mxPoint> points  = state.getAbsolutePoints();
            mxPoint point = parentCell == edge.getSource() ? points.get(0) : points.get(pointsCount-1);
            addEdgeOnPointMap(pointsMap, point, edge);
        }
        return pointsMap;
    }
    
    private void addEdgeOnPointMap(HashMap<mxPoint,List<mxCell>> pointsMap, mxPoint edgePoint, mxCell edge){
        boolean pointExists = false;
        for(mxPoint point : pointsMap.keySet()){
            if(point.equals(edgePoint)){
                pointsMap.get(point).add(edge);
                pointExists = true;
                break;
            }
        }
        if(!pointExists){
            List<mxCell> lista = new ArrayList<>();
            lista.add(edge);
            pointsMap.put(edgePoint, lista);
        }
    }
    
}
