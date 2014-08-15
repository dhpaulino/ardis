/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.logico;

import ardis.model.Modelo;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.constraint.PK;
import ardis.model.logico.tabela.Tabela;
import ardis.view.Graph;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraphView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.Arrays;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 *
 * @author Israel
 */
public class LogicoGraph extends Graph{    
    
    private boolean showLine;
    private boolean showCardinalidade;
    private boolean mudaY;

    public LogicoGraph() {
        showLine = true;
        mudaY = false;
        showCardinalidade = true;
    }

    public boolean isShowCardinalidade() {
        return showCardinalidade;
    }

    public void setShowCardinalidade(boolean showCardinalidade) {
        this.showCardinalidade = showCardinalidade;
    }
    
    public boolean isMudaY() {
        return mudaY;
    }

    public void setMudaY(boolean mudaY) {
        this.mudaY = mudaY;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }
 
    
    @Override
    public boolean isCellMovable(Object cell) {
        return super.isCellMovable(cell) && !getModel().isVertex(getModel().getParent(cell)) && !getModel().isEdge(cell);
    }

    @Override
    public boolean isCellResizable(Object cell) {
        return super.isCellResizable(cell) && !getModel().isVertex(getModel().getParent(cell)); 
    }
    
    @Override
    public String getLabel(Object cell) {
        mxCell celula = (mxCell)cell;
        if(celula.getValue() instanceof Coluna && celula.getParent() != null){
            Coluna coluna = (Coluna)celula.getValue();
            String label = coluna.toString();
            Tabela tabela = (Tabela)celula.getParent().getValue();
            if(tabela.getConstraintByColuna(coluna, FK.class) != null){
                label = "FK "+label;
            }
            if(tabela.getConstraintByColuna(coluna, PK.class) != null){
                label =  "PK "+label;
            }
            return label;
        }
        return super.getLabel(cell); 
    }
    
    @Override
    protected mxGraphView createGraphView() {
        return new mxGraphView(this) {
            @Override
            public void updateLabel(mxCellState state) {
                super.updateLabel(state);
                String label = " " + state.getLabel();
                FontMetrics metrics = mxUtils.getFontMetrics(mxUtils.getFont(state.getStyle()));
                int maxWidth = (int) getWordWrapWidth(state);
                
                if(label != null && metrics.stringWidth(label) > maxWidth){
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
        };
    }
    
    @Override
    public Object resizeCell(Object cell, mxRectangle bounds) {
        mxCell celula = (mxCell)cell;
        boolean resize = true;
        
        if(celula.getValue() instanceof Tabela){
            if(celula.getChildCount() > 0){
                resize = bounds.getHeight() > (celula.getChildCount()*20)+30;
                for(Object child : this.getChildCells(celula)){
                    mxCell filha = (mxCell) child;
                    filha.getGeometry().setWidth(bounds.getWidth());
                }
            }else{
                resize = false;
            }
        }
        
        return resize ? super.resizeCell(cell, bounds) : cell;
    }

    @Override
    public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
        
        if (canvas instanceof mxGraphics2DCanvas) {
            super.drawState(canvas, state, false);
            Graphics2D g = ((mxGraphics2DCanvas) canvas).getGraphics();
            int gridX = 855;
            int gridY = 1100;
            
            if(showLine){
                for(int bounds = 1; bounds < 10; bounds++)
                {
                    
                    Color oldColor = g.getColor();
                    g.setColor(Color.lightGray);
                    g.draw(new Line2D.Double(gridX, 0, gridX, 9000));
                    g.draw(new Line2D.Double(0, gridY, 9000, gridY));
                    gridX = gridX + 855;
                    gridY = gridY + 1100;
                    g.setColor(oldColor);
                }
            }
            
            if(model.isEdge(state.getCell())) 
            {
                FK fk = (FK) model.getValue(state.getCell());
                drawCardinalities("(" + fk.getCardinalidadeTabelaReferenciada().toString() + ")",
                        "(" + fk.getCardinalidadeTabelaFK().toString() + ")", g, state);
            }
            else {
                super.drawState(canvas, state, drawLabel);
            }
            getModel().beginUpdate();
            try {

                mxIGraphLayout edgeLayout = new mxParallelEdgeLayout(this);
                edgeLayout.execute(getDefaultParent());
            } finally {
                getModel().endUpdate();
            }
        }
    }
    
    private void drawCardinalities(String text1, String text2, Graphics2D g, mxCellState state) {
      
        mudaY = true;
        Font scaledFont = mxUtils.getFont(state.getStyle(), getView().getScale());
        g.setFont(scaledFont);
        FontMetrics fm = g.getFontMetrics();
        Color fontColor = mxUtils.getColor(state.getStyle(), mxConstants.STYLE_FONTCOLOR, Color.black);
        g.setColor(fontColor);

        int w1 = SwingUtilities.computeStringWidth(fm, text1);
        int w2 = SwingUtilities.computeStringWidth(fm, text2);

        int h = fm.getAscent();

        int points = state.getAbsolutePointCount();
        
        mxGeometry source = ((mxCell)state.getCell()).getSource().getGeometry();
        mxGeometry target = ((mxCell)state.getCell()).getTarget().getGeometry();
        
        int point1X = (int) state.getAbsolutePoint(0).getX();
        int point1Y = (int) state.getAbsolutePoint(0).getY();   

        int point2X = (int) state.getAbsolutePoint(points - 1).getX();
        int point2Y = (int) state.getAbsolutePoint(points - 1).getY();
        
        int y1, y2;
        int x2, x1;
        
        long sourceSizeY = Math.round(source.getY() + source.getHeight());
        long targetSizeY = Math.round(target.getY() + target.getHeight());
        
        y1 = point1Y == sourceSizeY ? point1Y + h : point1Y - 5;
        y2 = point2Y == targetSizeY ? point2Y + h : point2Y - 5;
        x1 = point1X == Math.round(source.getX()) && source.getCenterX() > point1X ? (point1X - w1) - 10 : point1X + 10;
        x2 = point2X == Math.round(target.getX()) && target.getCenterX() > point2X ? (point2X - w2) - 10 : point2X + 10;
        
        if(source.contains(point1X, y1) && source.getCenterX() < point1X || source.contains(point1X, y1) && source.getCenterX() > point1X || 
           target.contains(point2X, y2) && target.getCenterX() < point2X || target.contains(point2X, y2) && target.getCenterX() > point2X)
        {
            mudaY = false;
        }
        
        if(showCardinalidade == false)
        {
                if(point2Y > point1Y)
                {
                    y1 = point1Y + 10;
                }
                else
                {
                    y1 = point1Y - 10;
                }
                if(point1Y > point2Y)
                {
                    y2 = point2Y + 10;
                }
                else
                {
                    y2 = point2Y - 10;
                }
                if(point2X > point1X )
                {
                    x1 = point1X + 10;
                }
                else
                {
                    x1 = point1X - 20;
                }
                if(point1X > point2X)
                {
                    x2 = point2X +10;
                }
                else
                {
                    x2 = point2X -20;
                }
        }
        g.drawString(text1, x1, y1);
        g.drawString(text2, x2, y2);
    }
    
    private void removePKReferences(Coluna colunaPK, Tabela tabela){
        Modelo modelo = (Modelo)((mxCell)getDefaultParent()).getValue();
        for(FK fk : modelo.getObjetosByClass(FK.class)){
            if(fk.getTabelaReferenciada().equals(tabela)){
                for(Map.Entry<Coluna,Coluna> entry : fk.getColunasComReferencias().entrySet()){
                    if(entry.getValue().equals(colunaPK)){
                        mxCell colunaFk = getCellByValue(entry.getKey());
                        removeCells(new Object[]{colunaFk});
                    }
                }
            }
        }
    }
    
    @Override
    public void cellsRemoved(Object[] cells) {
        List<Object> checked = new ArrayList<>();
        for (Object cell : cells) {
            if(!checked.contains(cell)){
                checked.add(cell);
                mxCell celula = (mxCell) cell;
                if (celula.getValue() instanceof Coluna) {
                    mxCell pai = (mxCell)celula.getParent();
                    for(Object child : getChildCells(pai)){
                        mxCell filha = (mxCell)child;
                        if(filha.getGeometry().getY() > celula.getGeometry().getY()){
                            filha.getGeometry().setY(filha.getGeometry().getY() - 20);
                        }
                    }
                    pai.getGeometry().setHeight(pai.getGeometry().getHeight() - 20);
                    this.refresh();
                    Coluna coluna =  (Coluna)celula.getValue();

                    FK fk = ((Tabela)pai.getValue()).getConstraintByColuna(coluna, FK.class);
                    if(fk != null){
                        Object edge = getCellByValue(fk);
                        fk.getColunasComReferencias().remove(coluna);
                        if (!Arrays.asList(cells).contains(edge)
                                && (fk.getColunasComReferencias().size() != fk.getTabelaReferenciada().getPrimaryKey().getColunas().size()
                                || fk.getTabelaReferenciada().getPrimaryKey().getColunas().isEmpty())) {
                            removeCells(new Object[]{edge});
                        }
                    }

                    PK pk = ((Tabela)pai.getValue()).getConstraintByColuna(coluna, PK.class);

                    ((Tabela) pai.getValue()).removeColuna(coluna);

                    if(pk != null){
                        removePKReferences(coluna, ((Tabela)pai.getValue()));
                    }
                } else if (celula.getValue() instanceof Tabela) {
                    for (Object edge : getEdges(celula)) {
                        if(!Arrays.asList(cells).contains(edge)){
                            removeCells(new Object[]{edge});
                        }
                    }
                    Modelo modelo = (Modelo) ((mxCell) getDefaultParent()).getValue();
                    modelo.getObjetos().remove((Tabela) celula.getValue());
                } else if (celula.getValue() instanceof FK) {
                    Tabela tabelaFK = (Tabela) celula.getTarget().getValue();
                    tabelaFK.getConstraints().remove((FK) celula.getValue());
                    Modelo modelo = (Modelo)((mxCell)getDefaultParent()).getValue();
                    modelo.getObjetos().remove(celula.getValue());
                    for (Coluna coluna : ((FK) celula.getValue()).getColunas()) {
                        mxCell colunaCell = getCellByValue(coluna);
                        if(!Arrays.asList(cells).contains(colunaCell)){
                            removeCells(new Object[]{colunaCell});
                        }
                        tabelaFK.removeColuna(coluna);
                    }
                }
            }
        }
        super.cellsRemoved(cells);
    }

    @Override
    public Object[] foldCells(boolean collapse) {
        Object[] cells = super.foldCells(collapse);
        if(!collapse){
            for(Object cell : cells){
                resizeCell(cell, ((mxCell)cell).getGeometry());
            }
        }
        return cells;
    }
}
