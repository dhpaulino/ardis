/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import javax.swing.JSplitPane;

/**
 *
 * @author Davisson
 */
public interface ModeloPanel {

    public GraphComponent getGraphComponent();

    public void setGraphComponent(GraphComponent graphComponent);

    public JSplitPane getSplitPane();

    public void focusGained();
}
