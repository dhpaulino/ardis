/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.conceitual;

import ardis.model.Objeto;
import ardis.model.ObjetoGrafico;
import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.ObjetoWithAttribute;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.especializacao.Especializacao;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.view.Graph;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraphView;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.SwingUtilities;

/**
 *
 * @author Davisson
 */
public class ConceitualGraph extends Graph {

    private boolean showLine = true;
    private boolean mudaY = false;
    private boolean showCardinalidade = true;

    @Override
    public boolean isResetEdgesOnMove() {
        return true;
    }

    

    public boolean isShowCardinalidade() {
        return showCardinalidade;
    }

    public void setShowCardinalidade(boolean showCardinalidade) {
        this.showCardinalidade = showCardinalidade;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }

    public boolean isMudaY() {
        return mudaY;
    }

    public void setMudaY(boolean mudaY) {
        this.mudaY = mudaY;
    }

    public ConceitualGraph() {

        setVertexLabelsMovable(false);
        setCellsCloneable(false);
        setConnectableEdges(false);


        showLine = true;
        // setCellsResizable(false);
        /**
         * Não deixa desconectar as edges
         */
        setCellsDisconnectable(false);
        setCellsEditable(false);

        setLabelsClipped(false);



    }

    @Override
    public void cellLabelChanged(Object o, Object o1, boolean bln) {

        Objeto objeto = ((Objeto) ((mxCell) o).getValue());
        objeto.setNome(o1.toString());
        super.cellLabelChanged(o, objeto, bln);
    }

    @Override
    public boolean isCellMovable(Object cell) {
        if (((mxCell) cell).getParent() != this.getDefaultParent()) {
            return false;
        }

        return super.isCellMovable(cell); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCellResizable(Object cell) {
        return super.isCellResizable(cell) && !getModel().isVertex(getModel().getParent(cell));
    }

    @Override
    protected mxGraphView createGraphView() {
        return new mxGraphView(this) {
            @Override
            public void updateLabel(mxCellState state) {
                super.updateLabel(state);
                ObjetoGrafico value = (ObjetoGrafico) ((mxCell) state.getCell()).getValue();
                if (value != null && value.isLimitLabelSize()) {
                    String label = " " + state.getLabel();
                    FontMetrics metrics = mxUtils.getFontMetrics(mxUtils.getFont(state.getStyle()));
                    int maxWidth = (int) getWordWrapWidth(state);

                    if (label != null && metrics.stringWidth(label) > maxWidth) {
                        maxWidth = maxWidth - metrics.stringWidth("...");
                        for (int i = 0; i < label.length(); i++) {
                            if (metrics.stringWidth(label.substring(0, i + 1)) > maxWidth) {
                                label = label.substring(0, i + 1) + "...";
                                state.setLabel(label);
                                break;
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
        if (state != null) {
            mxCell cell = (mxCell) state.getCell();
            Object value = cell.getValue();


            if (canvas instanceof mxGraphics2DCanvas) {
                super.drawState(canvas, state, false);
                Graphics2D g = ((mxGraphics2DCanvas) canvas).getGraphics();
                int gridX = 855;
                int gridY = 1100;

                if (showLine) {
                    for (int bounds = 1; bounds < 10; bounds++) {
                        Color oldColor = g.getColor();
                        g.setColor(Color.lightGray);
                        g.draw(new Line2D.Double(gridX, 0, gridX, 9000));
                        g.draw(new Line2D.Double(0, gridY, 9000, gridY));
                        gridX = gridX + 855;
                        gridY = gridY + 1100;
                        g.setColor(oldColor);
                    }
                }

                if (value != null && value instanceof MembroRelacionamento) {
                    MembroRelacionamento membroRelacionamento = (MembroRelacionamento) value;
                    drawCardinality("(" + value.toString() + ")", g, state, canvas);

                    if (membroRelacionamento.isFraco()) {

                        cell.setStyle("entidadeFraca");
                    } else {
                        cell.setStyle("cardinalidade");
                    }
                } else {
                    super.drawState(canvas, state, drawLabel);
                }

            }
        }
    }

    @Override
    public Object[] moveCells(Object[] cells, double dx, double dy, boolean clone, Object target, Point location) {


        Object[] result = super.moveCells(cells, dx, dy, clone, target, location); //To change body of generated methods, choose Tools | Templates.

        getModel().beginUpdate();
        try {

            mxIGraphLayout edgeLayout = new mxParallelEdgeLayout(this);
            edgeLayout.execute(getDefaultParent());

            for (Object object : result) {
                mxCell cell = (mxCell) object;
                Object value = cell.getValue();
                if (value instanceof Atributo) {

                    Object[] edges = getEdges(cell);
                    if (edges.length > 0) {
                        mxCell ownerAtributo = (mxCell) ((mxCell) edges[0]).getSource();

                        Map<String, Object> style = view.getState(cell).getStyle();

                        if (model.getGeometry(ownerAtributo).getX() < model.getGeometry(cell).getX()) {

                            style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_RIGHT);
                            style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
                        } else {
                            style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT);
                            style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT);
                        }
                        StringBuilder sb = new StringBuilder();
                        Iterator<Entry<String, Object>> iter = style.entrySet().iterator();
                        while (iter.hasNext()) {
                            Entry<String, Object> entry = iter.next();
                            sb.append(entry.getKey());
                            sb.append('=');
                            sb.append(entry.getValue());
                            if (iter.hasNext()) {
                                sb.append(';');
                            }

                            cell.setStyle(sb.toString());
                        }
                    }
                }
            }
        } finally {
            getModel().endUpdate();
        }
        refresh();
        return result;
    }

    private void drawCardinality(String text, Graphics2D g, mxCellState state, mxICanvas canvas) {
        mudaY = true;
        Font scaledFont = mxUtils.getFont(state.getStyle(), canvas.getScale());
        g.setFont(scaledFont);
        FontMetrics fm = g.getFontMetrics();
        Color fontColor = mxUtils.getColor(state.getStyle(), mxConstants.STYLE_FONTCOLOR, Color.black);
        g.setColor(fontColor);

        int w2 = SwingUtilities.computeStringWidth(fm, text);

        int h = fm.getAscent();

        int points = state.getAbsolutePointCount();

        mxGeometry source = ((mxCell) state.getCell()).getSource().getGeometry();

        mxGeometry target = ((mxCell) state.getCell()).getTarget().getGeometry();

        int point1X = (int) state.getAbsolutePoint(0).getX();
        int point1Y = (int) state.getAbsolutePoint(0).getY();

        int point2X = (int) state.getAbsolutePoint(points - 1).getX();
        int point2Y = (int) state.getAbsolutePoint(points - 1).getY();

        int y1 = point1Y == source.getY() + source.getHeight() ? point1Y + h : point1Y - 5;
        int y2 = point2Y == target.getY() + target.getHeight() ? point2Y + h : point2Y - 5;

        int x2 = target.contains(point2X, y2) && target.getCenterX() > point2X ? (point2X - w2) - 10 : point2X + 10;

        if (source.contains(point1X, y1) && source.getCenterX() < point1X || source.contains(point1X, y1) && source.getCenterX() > point1X
                || target.contains(point2X, y2) && target.getCenterX() < point2X || target.contains(point2X, y2) && target.getCenterX() > point2X) {
            mudaY = false;
        }

        if (showCardinalidade == false) {
            if ((point1Y >= source.getY() + source.getHeight()
                    || point2Y >= target.getY() + target.getHeight()) || mudaY) {
                if (point2Y > point1Y) {
                    y1 = point1Y + 10;
                } else {
                    y1 = point1Y - 10;
                }
                if (point1Y > point2Y) {
                    y2 = point2Y + 10;
                } else {
                    y2 = point2Y - 10;
                }
                mudaY = true;
            }
            if ((point1X != source.getX() + source.getHeight()
                    || point2X != target.getX() + target.getHeight())) {
                if (point1X > point2X) {
                    x2 = point2X + 10;
                } else {
                    x2 = point2X - 20;
                }
            }
        }
        g.drawString(text, x2, y2);
    }

    @Override
    public void cellsRemoved(Object[] cells) {

        ModeloConceitual modelo = (ModeloConceitual) ((mxCell) getDefaultParent()).getValue();

        for (Object object : cells) {

            mxCell cell = (mxCell) object;

            ObjetoGrafico value = (ObjetoGrafico) cell.getValue();


            if (cell.isEdge()) {

                if (value instanceof MembroRelacionamento) {
                    Object[] edges = getEdges(cell);


                    Relacionamento relacionamento =
                            (Relacionamento) cell.getSource().getValue();

                    relacionamento.removeMembro((MembroRelacionamento) value);

                }
                if (cell.getTarget() != null && cell.getTarget().getValue() instanceof Atributo) {
                    cellsRemoved(new Object[]{cell.getTarget()});

                }
            } else if (value instanceof Entidade
                    || value instanceof Relacionamento
                    || value instanceof Especializacao) {

                modelo.getObjetos().remove(value);
            } else if (value instanceof Atributo) {

                Object[] edges = getEdges(cell);

                for (Object edgeObject : edges) {
                    mxICell cellOWA =
                            ((mxCell) edgeObject).getSource();

                    if (cellOWA != null) {
                        ObjetoWithAttribute objetoWhithAttribute = (ObjetoWithAttribute) cellOWA.getValue();

                        objetoWhithAttribute.removeAttibute((Atributo) value);
                    }
                }
            }



        }
        super.cellsRemoved(cells);
    }

    @Override
    public void drawCell(mxICanvas canvas, Object cell) {
        super.drawCell(canvas, cell); //To change body of generated methods, choose Tools | Templates.
    }
}
