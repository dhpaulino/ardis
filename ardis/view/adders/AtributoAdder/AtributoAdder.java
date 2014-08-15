/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.adders.AtributoAdder;

import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.ObjetoWithAttribute;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.atributo.composto.AtributoComposto;
import ardis.model.conceitual.atributo.composto.AtributoCompostoImpl;
import ardis.view.adders.Adder;
import ardis.view.conceitual.ObjetosPositionHandler;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author Davisson
 */
public class AtributoAdder implements Adder {

    private Class<Atributo> classe;
    private static AtributoAdder instance;
    private Atributo atributo;
    private ObjetosPositionHandler positionHandler;

    public AtributoAdder(ObjetosPositionHandler positionHandler) {
        this.positionHandler = positionHandler;
    }

    public void prepare(Atributo atributo) {

        this.atributo = atributo;
    }

    @Override
    public boolean add(mxGraph graph, ModeloConceitual modelo, double x, double y) throws Exception {

        mxCell cellClicked = (mxCell) graph.getSelectionCell();

        if (cellClicked != null) {

            if (cellClicked.getValue() instanceof ObjetoWithAttribute) {
                ObjetoWithAttribute objetoClicked = (ObjetoWithAttribute) cellClicked.getValue();

                graph.getModel().beginUpdate();
                mxCell atributoCell = null;
                try {
                    atributoCell = atributo.initialRender(graph, (mxCell) graph.getDefaultParent(), 0, 0);


                    objetoClicked.addAttribute(atributo);

                    positionHandler.calculatePositionAtributo(atributoCell, cellClicked);


                    graph.insertEdge(cellClicked, null, null, cellClicked, atributoCell);


                } finally {

                    graph.getModel().endUpdate();
                }

                if (atributo instanceof AtributoCompostoImpl) {

                    for (int i = 0; i < 2; i++) {

                        graph.getModel().beginUpdate();
                        try {

                            Atributo atributoOnComposto = new Atributo();

                            AtributoComposto atributoComposto = ((AtributoComposto) atributo);
                            atributoComposto.addAttribute(atributoOnComposto);


                            mxCell atributoOnCompostoCell = atributoOnComposto.initialRender(graph, (mxCell) graph.getDefaultParent(), 0, 0);

                            positionHandler.calculatePositionAtributo(atributoOnCompostoCell, atributoCell);

                            graph.insertEdge(atributoCell, null, null, atributoCell, atributoOnCompostoCell);
                        } finally {
                            graph.getModel().endUpdate();
                        }
                    }
                }

 
                return true;

            }
        }

        throw new Exception("Seleção inválida");
    }

    public static AtributoAdder getInstance(ObjetosPositionHandler positionHandler) {
        if (instance == null) {

            instance = new AtributoAdder(positionHandler);
        }
        return instance;

    }
}
