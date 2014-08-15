package ardis.view.conceitual;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import java.util.HashMap;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Davisson
 */
public class ObjetosPositionHandler {

    private mxGraphComponent graphComponent;
    private static ObjetosPositionHandler instance;

    public ObjetosPositionHandler(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
        contadores = new HashMap<>();
    }
    /* public static ObjetosPositionHandler inicialize(mxGraphComponent graphComponent) {
     instance = new ObjetosPositionHandler(graphComponent);
     return instance;
     }

     public static ObjetosPositionHandler getInstance() {

     return instance;
     }
     * /
     /*
     public void calculatePosition(DependentObjeto objetoDependent, Objeto objetoFather) {

     if (objetoDependent instanceof Atributo) {

     objetoDependent = (Atributo) objetoDependent;
     if (objetoFather instanceof ObjetoWhithAttribute) {
     objetoFather = (ObjetoWhithAttribute) objetoFather;
     calculatePosition(objetoDependent, objetoFather);
     }
     }
     }
     */
    private HashMap<mxCell, Integer> contadores;

    public void calculatePositionAtributo(mxCell attributeCell, mxCell objetoOnCell) {


        if (!contadores.containsKey(objetoOnCell)) {
            contadores.put(objetoOnCell, 0);
        }

        Integer cont = contadores.get(objetoOnCell);
        cont++;
        contadores.put(objetoOnCell, cont);


        double xAtributo = objetoOnCell.getGeometry().getX() + objetoOnCell.getGeometry().getWidth() + 40;
        double yAtributo = objetoOnCell.getGeometry().getCenterY()
                - attributeCell.getGeometry().getHeight() / 2;

        reposicionarAtributo((int) xAtributo, (int) yAtributo, cont % 2 == 0);

        attributeCell.getGeometry().setX(xAtributo);
        attributeCell.getGeometry().setY(yAtributo);


//        mxCellState edgeState = graphComponent.getGraph().getView().getState(edge);
//        List<mxPoint> points = edgeState.getAbsolutePoints();
//            
//        mxPoint sourcePoint = points.get(0);
//
//
//        sourcePoint.setY(yAtributo);
//        edge.getGeometry().setSourcePoint(sourcePoint);
        // edge.getGeometry().setPoints(points);

    }

    public void reposicionarAtributo(int x, int y, boolean descer) {


        mxCell cellAt = (mxCell) graphComponent.getCellAt(x, y);

        if (cellAt != null) {
            int value = descer ? 20 : -20;
            int newY = (int) (y + value);
            reposicionarAtributo(x, newY, descer);

            cellAt.getGeometry().setY(newY);
        }


    }

    public void calculatePositionRelacionamento(mxCell relacionamentoCell, List<mxCell> entidadesCells) {


        double xRelationship = 0;
        double yRelationship = 0;

        boolean sameEntity = entidadesCells.size() == 1;
        //HashMap<Entidade, Cardinalidade[]> entities = relacionamentoCell.getEntidadesCardinalidades();
        for (mxCell entidadeCell : entidadesCells) {

            xRelationship += entidadeCell.getGeometry().getX();
            yRelationship += entidadeCell.getGeometry().getCenterY();

        }
        /*  for (int i = 0; i < entities.size(); i++) {
         Entidade entity = entities.get(i);
         if (sameEntity && !entity.equals(entities.get(Math.abs(i - 1)))) {

         sameEntity = false;
         }
         xRelationship += entity.getCell().getGeometry().getX();
         yRelationship += entity.getCell().getGeometry().getY();
         }
         */
        if (sameEntity) {
            mxGeometry geometry = entidadesCells.get(0).getGeometry();
            xRelationship = geometry.getX() + geometry.getWidth() + 30;
            yRelationship = geometry.getY();
        } else {
            xRelationship /= entidadesCells.size();
            yRelationship /= entidadesCells.size();

            yRelationship -= relacionamentoCell.getGeometry().getHeight() / 2;
        }
        relacionamentoCell.getGeometry().setX(xRelationship);
        relacionamentoCell.getGeometry().setY(yRelationship);
    }

    public void calculatePositionEspecializacao(mxCell especializacaoCell, mxCell entidadePaiCell, List<mxCell> entidadesFilhasCells) {

        mxGeometry especializationGeometry = especializacaoCell.getGeometry();
        double xEspecialization = entidadePaiCell.getGeometry().getCenterX()
                - especializationGeometry.getWidth() / 2;
        double yEspecialization = entidadePaiCell.getGeometry().getY() + 100;

        if (entidadesFilhasCells.size() == 1) {
            mxCell entidadeCell = entidadesFilhasCells.get(0);
            double y = yEspecialization + 100;
            double x = entidadePaiCell.getGeometry().getX();
            entidadeCell.getGeometry().setX(x);
            entidadeCell.getGeometry().setY(y);
        } else {

            //double lastX = xEspecialization - 10;
            for (int i = 0; i < entidadesFilhasCells.size(); i++) {
                mxCell entidadeCell = entidadesFilhasCells.get(i);
                double y = yEspecialization + 100;
                double x;

                if ((i + 1) % 2 == 0) {
                    x = xEspecialization - ((i + 1) * (entidadeCell.getGeometry().getWidth()) + 10)
                            + especializationGeometry.getWidth();
                } else {
                    x = xEspecialization + ((i + 1) * (entidadeCell.getGeometry().getWidth()) + 10);
                }

                entidadeCell.getGeometry().setX(x);
                entidadeCell.getGeometry().setY(y);
            }


        }
        especializationGeometry.setX(xEspecialization);
        especializationGeometry.setY(yEspecialization);
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void setGraphComponent(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }
}
