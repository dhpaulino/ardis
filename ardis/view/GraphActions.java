/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.model.conceitual.ObjetoWithAttribute;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.atributo.composto.AtributoComposto;
import ardis.model.logico.coluna.Coluna;
import ardis.view.logico.LogicoGraph;
import ardis.view.logico.LogicoGraphComponent;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import static com.mxgraph.swing.util.mxGraphActions.getGraph;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 *
 * @author Israel
 */
public class GraphActions extends mxGraphActions {

    static final Action moveUp = new MoveAction("up");
    static final Action moveDown = new MoveAction("down");
    static final Action moveLeft = new MoveAction("left");
    static final Action moveRight = new MoveAction("right");
    static final Action easterAction = new EasterEggAction();
    static final Action organizarAction = new OrganizarAction();

    public static Action getMoveUpAction() {
        return moveUp;
    }

    public static Action getMoveDownAction() {
        return moveDown;
    }

    public static Action getMoveLeftAction() {
        return moveLeft;
    }

    public static Action getMoveRightAction() {
        return moveRight;
    }

    public static Action getEasterAction() {
        return easterAction;
    }

    public static Action getOrganizarAction() {
        return organizarAction;
    }

    public static class MoveAction extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -8212339796803275529L;

        /**
         *
         * @param name
         */
        public MoveAction(String name) {
            super(name);
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            mxGraph graph = getGraph(e);

            if (graph != null) {
                String moveTo = getValue(Action.NAME).toString();
                double dx = 0;
                double dy = 0;
                if (moveTo.equalsIgnoreCase("up")) {
                    dy = -1;
                } else if (moveTo.equalsIgnoreCase("down")) {
                    dy = 1;
                } else if (moveTo.equalsIgnoreCase("left")) {
                    dx = -1;
                } else if (moveTo.equalsIgnoreCase("right")) {
                    dx = 1;
                }

                if (graph instanceof LogicoGraph) {
                    Object[] cells = graph.getSelectionCells();
                    boolean move = true;
                    for (Object cell : cells) {
                        if (((mxCell) cell).getValue() != null && ((mxCell) cell).getValue() instanceof Coluna) {
                            move = false;
                        }
                    }
                    if (move) {
                        graph.moveCells(cells, dx, dy);
                    }
                } else {
                    graph.moveCells(graph.getSelectionCells(), dx, dy);
                }
            }
        }
    }

    public static class EasterEggAction extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -8212339796803275529L;

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source instanceof mxGraphComponent) {
                final mxGraphComponent graphComponent = (mxGraphComponent) source;
                BufferedImage elaini = mxUtils.loadImage("/ardis/resources/elaini.jpg");
                mxRectangle area = graphComponent.getLayoutAreaSize();
                ImageIcon image = new ImageIcon(resizeImage(elaini, (int) area.getWidth(), (int) area.getHeight()));
                graphComponent.setBackgroundImage(image);
                graphComponent.setGridVisible(false);
                graphComponent.refresh();

                ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

                exec.schedule(new Runnable() {
                    public void run() {
                        clear(graphComponent);
                    }
                }, 250, TimeUnit.MILLISECONDS);
            }
        }

        private void clear(mxGraphComponent graphComponent) {
            graphComponent.setBackgroundImage(null);
            graphComponent.setBackground(Color.WHITE);
            graphComponent.setGridVisible(true);
            graphComponent.refresh();
        }

        private BufferedImage resizeImage(final Image image, int width, int height) {
            final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.drawImage(image, 0, 0, width, height, null);
            graphics2D.dispose();

            return bufferedImage;
        }
    }

    public static class OrganizarAction extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = -8212339796803275529L;
        private int contDireita;
        private int contEsquerda;
        private int contCima;
        private int contBaixo;
        private mxGraphComponent graphComponent;
        private int distanciaObjetoAtributo = 30;
        private int distanciaAtributos = 15;

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {

            contDireita = 0;
            contEsquerda = 0;
            contCima = 0;
            contBaixo = 0;

            Object source = e.getSource();


            if (source instanceof mxGraphComponent) {


                graphComponent = (mxGraphComponent) source;
                mxGraph graph = graphComponent.getGraph();
                Object[] cells = graph.getSelectionCells();

                if (graphComponent instanceof LogicoGraphComponent) {
                    SeparateEdgesLayout edgeLayout = new SeparateEdgesLayout(graph);
                    if(cells.length == 0){
                        edgeLayout.execute(graph.getDefaultParent());
                    }else{
                        for (Object object : cells) {
                            edgeLayout.execute(object);
                        }
                    }
                }else{
                for (Object object : cells) {

                    mxCell cell = (mxCell) object;
//                            edgeLayout.execute(object);

                    if (cell.getValue() instanceof ObjetoWithAttribute
                            && !(cell.getValue() instanceof AtributoComposto)) {
                        ObjetoWithAttribute value = (ObjetoWithAttribute) cell.getValue();

                        Object[] edges = graph.getEdges(cell);

                        for (Object objectEdge : edges) {

                            mxCell edge = (mxCell) objectEdge;

                            mxICell target = edge.getTarget();
                            if (target.getValue() instanceof Atributo
                                    && !(target.getValue() instanceof AtributoComposto)) {
                                mxGeometry cellGeometry = cell.getGeometry();
                                mxGeometry targetGeometry = target.getGeometry();

                                if (cellGeometry.getY() > targetGeometry.getY()) {
                                    double x = cellGeometry.getX() + cellGeometry.getWidth() - 5;
                                    double y = cellGeometry.getCenterY() - cellGeometry.getHeight() / 2 - distanciaObjetoAtributo;

                                    int multiplaier = getMultiplier(contCima);
                                    x -= contCima * distanciaAtributos;
                                    y -= contCima * distanciaAtributos;


                                    target.getGeometry().setY(y);
                                    target.getGeometry().setX(x);
                                    contCima++;

                                } else if (cellGeometry.getY() + cellGeometry.getHeight() < targetGeometry.getY()) {
                                    double x = cellGeometry.getX() + cellGeometry.getWidth() - 5;
                                    double y = cellGeometry.getCenterY() + cellGeometry.getHeight() / 2 + distanciaObjetoAtributo;

                                    int multiplaier = getMultiplier(contBaixo);
                                    x -= contBaixo * distanciaAtributos;
                                    y += contBaixo * distanciaAtributos;
                                    target.getGeometry().setY(y);
                                    target.getGeometry().setX(x);
                                    contBaixo++;
                                } else if (cellGeometry.getX() > targetGeometry.getX()) {
                                    double x = cellGeometry.getCenterX() - cellGeometry.getWidth() / 2 - distanciaObjetoAtributo;
                                    double y = cellGeometry.getCenterY();

                                    y += getMultiplier(contEsquerda) * distanciaObjetoAtributo;

                                    target.getGeometry().setY(y);
                                    target.getGeometry().setX(x);


                                    contEsquerda++;
                                } else {
                                    double x = cellGeometry.getCenterX() + cellGeometry.getWidth() / 2 + distanciaObjetoAtributo;
                                    double y = cellGeometry.getCenterY();

                                    y += getMultiplier(contDireita) * distanciaAtributos;

                                    target.getGeometry().setY(y);
                                    target.getGeometry().setX(x);

                                    contDireita++;
                                }
                            }
                        }

                        graphComponent.refresh();

                        List<Object> edgesReset = new ArrayList<>();
                        for (Object objectEdge : edges) {

                            mxCell edge = (mxCell) objectEdge;

                            mxICell target = edge.getTarget();
                            if (target.getValue() instanceof Atributo) {

                                edgesReset.add(edge);

                                mxCellState edgeState = graphComponent.getGraph().getView().getState(edge);

                                List<mxPoint> points = edgeState.getAbsolutePoints();

                                mxPoint sourcePoint = points.get(0);

                                ///mxPoint middlePoint = points.get(1);

                                mxPoint targetPoint = points.get(points.size() - 1);

                                mxGeometry cellGeometry = cell.getGeometry();
                                mxGeometry targetGeometry = target.getGeometry();

                                if (cellGeometry.getY() > targetGeometry.getY()
                                        || cellGeometry.getY() + cellGeometry.getHeight() < targetGeometry.getY()) {


                                    sourcePoint.setX(targetPoint.getX());
                                    // middlePoint.setY((sourcePoint.getY() + targetPoint.getY()) / 2);

                                } else {
                                    sourcePoint.setY(targetPoint.getY());
                                    //middlePoint.setX((sourcePoint.getX() + targetPoint.getX()) / 2);
                                }


                                edge.getGeometry().setPoints(points);

                                //calculatePositionAtributo((mxCell) target, cell);

                                if (cellGeometry.getY() > targetGeometry.getY()
                                        || cellGeometry.getY() + cellGeometry.getHeight() < targetGeometry.getY()) {
                                    //graphComponent.getGraph().resetEdge(edge);
                                }

                            }

                        }
                        //graphComponent.getGraph().resetEdges(edgesReset.toArray());
                    }


                    //graphComponent.getGraph().setResetEdgesOnMove(true);

                }
                }
//                    }
//                }else{
//                    OrganizarAtributosLayout layout = new OrganizarAtributosLayout(graph);
//                    if(cells.length == 0){
//                        layout.execute(graph.getDefaultParent());
//                    }else{
//                        for (Object object : cells) {
//                            layout.execute(object);
//                        }
//                    }
//                }
            }

            graphComponent.refresh();
        }

        private int getMultiplier(int cont) {

            int multiplier = 0;
            if (cont % 2 == 0) {
                multiplier = cont / 2;
            } else {
                multiplier = -1 * (cont + 1) / 2;
            }
            return multiplier;
        }
    }
}
