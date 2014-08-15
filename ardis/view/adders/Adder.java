/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.adders;

import ardis.model.conceitual.ModeloConceitual;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author Davisson
 */
public interface Adder {

    public boolean add(mxGraph graph, ModeloConceitual modelo, double x, double y) throws Exception;
}
