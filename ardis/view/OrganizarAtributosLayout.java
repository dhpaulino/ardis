/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.model.conceitual.ObjetoWithAttribute;
import ardis.model.conceitual.atributo.Atributo;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Israel
 */
public class OrganizarAtributosLayout extends mxGraphLayout{
    private mxCell parentCell;
    private ObjetoWithAttribute parentObjeto;
    
    public OrganizarAtributosLayout(mxGraph graph) {
        super(graph);
    }

    @Override
    public void execute(Object parent) {
        super.execute(parent);
        parentCell = (mxCell)parent;
        if(parentCell.getValue() instanceof ObjetoWithAttribute){
            parentObjeto = (ObjetoWithAttribute)parentCell.getValue();
            organizeAttributes();
        }
        graph.refresh();
    }
    
    private void organizeAttributes(){
        int count = 0;
        for(Atributo atributo : parentObjeto.getAttributes()){
            mxCell cell = ((Graph)graph).getCellByValue(atributo);
            mxGeometry geo = (mxGeometry)cell.getGeometry().clone();
            double spacing = parentCell.getGeometry().getHeight()/parentObjeto.getAttributes().size();
            geo.setY(parentCell.getGeometry().getY() + (spacing*count));
            geo.setX(parentCell.getGeometry().getX() + parentCell.getGeometry().getWidth() + 40);
            cell.setGeometry(geo);
            Object[] edges = graph.getEdgesBetween(parent, cell);
            List<mxPoint> points = new ArrayList<>();
            points.add(graph.getView().getNextPoint(graph.getView().getState(edges[0]), graph.getView().getState(parent), false));
            points.add(new mxPoint(geo.getX(),geo.getCenterY()));
            ((mxCell)edges[0]).getGeometry().setPoints(points);
            count++;
        }
    }
}
