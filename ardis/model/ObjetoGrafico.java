/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model;

import ardis.view.conceitual.infoTable.AbstractInfoTable;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author Israel
 */
public interface ObjetoGrafico extends Objeto {

    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition);
    
    //TODO:ARRUMAR POG ABAIXO
    public boolean isLimitLabelSize();

    public Class getModelInfo();
}
