/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual;

import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.entidade.Entidade;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Israel
 */
public class ConceitualLayout extends mxGraphLayout {

    private ObjetosPositionHandler positionHandler;
    private ConceitualGraphComponent graphComponent;

    public ConceitualLayout(mxGraph graph, ConceitualGraphComponent graphComponent) {
        super(graph);
        this.graphComponent = graphComponent;
        this.positionHandler = new ObjetosPositionHandler(graphComponent);
    }

    @Override
    public void execute(Object parent) {
        super.execute(parent);
        for (Object vertice : graph.getChildVertices(parent)) {
            mxCell cell = (mxCell) vertice;
            if (cell.getValue() instanceof Entidade) {
                organizarEntidade(cell, getEmptyPoint(graphComponent.getVisibleRect()));
            }
        }
        graph.refresh();
    }

    private Point getEmptyPoint(Rectangle bounds) {
        Point point = bounds.getLocation();
        String lookOn = "right";
        while (!isPointEmpty(point)) {
            mxCell cell = (mxCell) graphComponent.getCellAt((int) point.getX(), (int) point.getY());
            if (lookOn.equals("right")) {
                point.x += 2 * (cell.getGeometry().getWidth());
                if (!bounds.contains(point.getX(), point.getY())) {
                    point.x -= 2 * (cell.getGeometry().getWidth());
                    point.y += 2 * (cell.getGeometry().getHeight());
                    lookOn = "left";
                }
            } else if (lookOn.equals("left")) {
                point.x -= 2 * (cell.getGeometry().getWidth());
                if (!bounds.contains(point.getX(), point.getY())) {
                    point.x += 2 * (cell.getGeometry().getWidth());
                    point.y += 2 * (cell.getGeometry().getHeight());
                    lookOn = "right";
                }
            }
        }
        return point;
    }

    private boolean isPointEmpty(Point point) {
        return (mxCell) graphComponent.getCellAt((int) point.getX(), (int) point.getY()) == null;
    }

    private void organizarEntidade(mxCell entidade, Point posicao) {
        List<mxCell> atributos = new ArrayList<>();
        entidade.getGeometry().setX(posicao.getX());
        entidade.getGeometry().setY(posicao.getY());
        //List<mxCell> relacionamentos = new ArrayList<>();
        //List<mxCell> especializacoes = new ArrayList<>();
        for (Object edge : graph.getEdges(entidade)) {
            if (((mxCell) edge).getTarget().getValue() instanceof Atributo) {
                atributos.add((mxCell) edge);
            }/*else if(((mxCell)edge).getSource().getValue() instanceof RelacionamentoImpl){
             relacionamentos.add((mxCell)edge);
             }else{
             especializacoes.add((mxCell)edge);
             }*/
        }
        organizarAtributos(atributos, entidade);
    }

    private void organizarAtributos(List<mxCell> atributos, mxCell objetoWithAttribute) {
        for (mxCell atributo : atributos) {
            positionHandler.calculatePositionAtributo(atributo, objetoWithAttribute);
        }
    }
}
