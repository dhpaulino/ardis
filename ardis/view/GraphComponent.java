/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import ardis.model.Modelo;
import ardis.model.Objeto;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxMovePreview;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 *
 * @author Israel
 */
public abstract class GraphComponent extends mxGraphComponent {

    protected Modelo modelo;
    protected boolean salvo;
    protected boolean modified;
    private boolean showLine;
    protected mxRubberband rubberband;
    protected mxKeyboardHandler keyboardHandler;
    protected UndoManager undoManager;
    protected Dimension scaledPreferredSizeForGraph;
    protected mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener() {
        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };
    protected mxEventSource.mxIEventListener changeTracker = new mxEventSource.mxIEventListener() {
        public void invoke(Object source, mxEventObject evt) {
            setModified(true);
        }
    };

    public GraphComponent(mxGraph graph) {
        super(graph);
        initObjects();
        configureGraphComponent();

        ((mxCell) graph.getDefaultParent()).setValue(modelo);
    }

    protected void initObjects() {
        showLine = true;
        rubberband = new mxRubberband(this) {
            @Override
            public void paintRubberband(Graphics g) {
                super.paintRubberband(g);
                if(showLine){
                    Graphics2D g2d = (Graphics2D) g;
                    int gridX = 855;
                    int gridY = 1100;
                    for (int lineBounds = 1; lineBounds < 10; lineBounds++) {
                        Color oldColor = g.getColor();
                        g2d.setColor(Color.lightGray);
                        g2d.draw(new Line2D.Double(gridX, 0, gridX, 9000));
                        g2d.draw(new Line2D.Double(0, gridY, 9000, gridY));
                        gridX = gridX + 855;
                        gridY = gridY + 1100;
                        g2d.setColor(oldColor);
                    }
                }
            }
        };
        keyboardHandler = new mxKeyboardHandler(this) {
            @Override
            protected InputMap getInputMap(int condition) {
                InputMap map = null;

                if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
                    map = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
                } else if (condition == JComponent.WHEN_FOCUSED) {
                    map = new InputMap();
                    map.put(KeyStroke.getKeyStroke("DELETE"), "delete");
                    map.put(KeyStroke.getKeyStroke("UP"), "moveUp");
                    map.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
                    map.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
                    map.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
                    map.put(KeyStroke.getKeyStroke("ENTER"), "expand");
                    map.put(KeyStroke.getKeyStroke("BACK_SPACE"), "collapse");
                    map.put(KeyStroke.getKeyStroke("control A"), "selectAll");
                    map.put(KeyStroke.getKeyStroke("control D"), "selectNone");
                    map.put(KeyStroke.getKeyStroke("control shift E"), "easter");
                    map.put(KeyStroke.getKeyStroke("control shift O"), "organizar");
                    map.put(KeyStroke.getKeyStroke("CUT"), "cut");
                    map.put(KeyStroke.getKeyStroke("control C"), "copy");
                    map.put(KeyStroke.getKeyStroke("COPY"), "copy");
                    map.put(KeyStroke.getKeyStroke("control V"), "paste");
                    map.put(KeyStroke.getKeyStroke("PASTE"), "paste");
                }
                return map;
            }

            @Override
            protected ActionMap createActionMap() {
                ActionMap actionMap = super.createActionMap();
                actionMap.put("moveUp", GraphActions.getMoveUpAction());
                actionMap.put("moveDown", GraphActions.getMoveDownAction());
                actionMap.put("moveLeft", GraphActions.getMoveLeftAction());
                actionMap.put("moveRight", GraphActions.getMoveRightAction());
                actionMap.put("easter", GraphActions.getEasterAction());
                actionMap.put("organizar", GraphActions.getOrganizarAction());
                return actionMap;
            }
        };
    }

    protected void configureGraphComponent() {
        installUndoManager();

        // Sets switches typically used in an editor
        setGridVisible(true);
        //paintGrid(tripleBufferGraphics);
        setToolTips(true);
        getConnectionHandler().setCreateTarget(true);
        getGraphHandler().setRemoveCellsFromParent(false);
        //setPageVisible(true);
        setPageBreaksVisible(true);
        graph.setCellsCloneable(false);
        getGraphHandler().setLivePreview(true);
        graph.setResetEdgesOnMove(true);
        //setPreferPageSize(true);
        setCenterPage(true);
        //getGraphHandler().setLivePreview(true);
        //getGraphHandler().setCenterPreview(true);

        // Sets the background to white
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);

    }

    protected UndoManager createUndoManager() {
        return new UndoManager(this);
    }

    public void installUndoManager() {
        undoManager = createUndoManager();

        graph.getModel().addListener(mxEvent.CHANGE, changeTracker);
        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener() {
            public void invoke(Object source, mxEventObject evt) {
                List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                getGraph().setSelectionCells(getGraph().getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);

        graph.addListener(mxEvent.ADD_CELLS, new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {

                Object[] cells = (Object[]) evt.getProperty("cells");

                for (Object object : cells) {
                    mxCell cell = (mxCell) object;
                    mxRectangle tamGraph = getLayoutAreaSize();
                    if (cell != null) {
                        if (cell.getGeometry().getX() < tamGraph.getX()) {
                            cell.getGeometry().setX((int) tamGraph.getX());
                        } else if (cell.getGeometry().getY() < tamGraph.getY()) {
                            cell.getGeometry().setY((int) tamGraph.getY());
                        } else if (cell.getGeometry().getX() + cell.getGeometry().getWidth() > tamGraph.getWidth()
                                || cell.getGeometry().getY() + cell.getGeometry().getHeight() > tamGraph.getHeight()) {
                            double diferencaX = (cell.getGeometry().getX() + cell.getGeometry().getWidth()) - tamGraph.getWidth();
                            double diferencaY = (cell.getGeometry().getY() + cell.getGeometry().getHeight()) - tamGraph.getHeight();
                            Dimension preferredSize = new Dimension((int) (diferencaX > 0 ? tamGraph.getWidth() + diferencaX : tamGraph.getWidth()),
                                    (int) (diferencaY > 0 ? tamGraph.getHeight() + diferencaY : tamGraph.getHeight()));
                            setScaledPreferredSizeForGraph(preferredSize);
                            getGraphControl().updatePreferredSize();
                            JScrollBar scrollBar;

                            if (diferencaX > 0) {
                                scrollBar = getHorizontalScrollBar();
                                scrollBar.setValue(scrollBar.getMaximum());
                            }
                            if (diferencaY > 0) {
                                scrollBar = getVerticalScrollBar();
                                scrollBar.setValue(scrollBar.getMaximum());
                            }
                        }
                        if (cell.getGeometry().getX() < tamGraph.getX() || cell.getGeometry().getY() < tamGraph.getY()) {
                            double diferencaX = cell.getGeometry().getX() - tamGraph.getX();
                            double diferencaY = cell.getGeometry().getY() - tamGraph.getY();
                            JScrollBar scrollBar;
                            if (diferencaX > 0) {
                                scrollBar = getHorizontalScrollBar();
                                scrollBar.setValue((int) (scrollBar.getValue() - diferencaX));
                            }
                            if (diferencaY > 0) {
                                scrollBar = getVerticalScrollBar();
                                scrollBar.setValue((int) (scrollBar.getValue() + diferencaY));
                            }
                        }
                    }
                    scrollCellToVisible(cell, false);

                }



            }
        });
    }

    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    public void setModified(boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;

        firePropertyChange("modified", oldValue, modified);

        if (oldValue != modified) {
            //updateTitle();
        }
    }

    public abstract Modelo createModelo();

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
        ((mxCell) graph.getDefaultParent()).setValue(modelo);
        updateCellValues(getGraph().getDefaultParent());
        graph.refresh();
    }

    private void updateCellValues(Object parent) {
        for (Object cell : getGraph().getChildCells(parent)) {
            mxCell celula = (mxCell) cell;
            Objeto objeto = encontrarObjetoIgual(((Objeto) celula.getValue()));
            if (objeto != null) {
                celula.setValue(objeto);
            }

            updateCellValues(cell);



        }
    }

    protected Objeto encontrarObjetoIgual(Objeto referencia) {
        for (Objeto objeto : modelo.getObjetos()) {
            if (referencia != null && referencia.equals(objeto)) {
                return objeto;
            }
        }
        return null;
    }

    @Override
    protected mxGraphHandler createGraphHandler() {
        return new mxGraphHandler(this) {
            @Override
            protected mxMovePreview createMovePreview() {
                return new MovePreview(graphComponent);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                mxRectangle tamGraph = getLayoutAreaSize();
                
                if (previewBounds != null) {
                    if(livePreview){
                        previewBounds = ((MovePreview)movePreview).getDirty().getRectangle();
                    }
                    if (previewBounds.getX() < tamGraph.getX()) {
                        previewBounds.x = (int) tamGraph.getX();
                    } else if (previewBounds.getY() < tamGraph.getY()) {
                        previewBounds.y = (int) tamGraph.getY();
                    } else if (previewBounds.getX() + previewBounds.getWidth() > tamGraph.getWidth()
                            || previewBounds.getY() + previewBounds.getHeight() > tamGraph.getHeight()) {
                        double diferencaX = (previewBounds.getX() + previewBounds.getWidth()) - tamGraph.getWidth();
                        double diferencaY = (previewBounds.getY() + previewBounds.getHeight()) - tamGraph.getHeight();
                        Dimension preferredSize = new Dimension((int) (diferencaX > 0 ? tamGraph.getWidth() + diferencaX : tamGraph.getWidth()),
                                (int) (diferencaY > 0 ? tamGraph.getHeight() + diferencaY : tamGraph.getHeight()));
                        setScaledPreferredSizeForGraph(preferredSize);
                        getGraphControl().updatePreferredSize();
                        JScrollBar scrollBar;
                        if (diferencaX > 0) {
                            scrollBar = getHorizontalScrollBar();
                            scrollBar.setValue(scrollBar.getMaximum());
                        }
                        if (diferencaY > 0) {
                            scrollBar = getVerticalScrollBar();
                            scrollBar.setValue(scrollBar.getMaximum());
                        }
                    }
                    if (previewBounds.getX() < tamGraph.getX() || previewBounds.getY() < tamGraph.getY()) {
                        double diferencaX = previewBounds.getX() - tamGraph.getX();
                        double diferencaY = previewBounds.getY() - tamGraph.getY();
                        JScrollBar scrollBar;
                        if (diferencaX > 0) {
                            scrollBar = getHorizontalScrollBar();
                            scrollBar.setValue((int) (scrollBar.getValue() - diferencaX));
                        }
                        if (diferencaY > 0) {
                            scrollBar = getVerticalScrollBar();
                            scrollBar.setValue((int) (scrollBar.getValue() - diferencaY));
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                graph.refresh();
            }
        };
    }

    public void setScaledPreferredSizeForGraph(Dimension scaledPreferredSizeForGraph) {
        this.scaledPreferredSizeForGraph = scaledPreferredSizeForGraph;
    }

    @Override
    protected Dimension getScaledPreferredSizeForGraph() {
        Dimension graphDimension = super.getScaledPreferredSizeForGraph();
        if (this.scaledPreferredSizeForGraph == null) {
            return graphDimension;
        }
        if (graphDimension != null) {
            if (graphDimension.getHeight() > this.scaledPreferredSizeForGraph.getHeight()) {
                scaledPreferredSizeForGraph.setSize(scaledPreferredSizeForGraph.getWidth(), graphDimension.getHeight());
            }
            if (graphDimension.getWidth() > this.scaledPreferredSizeForGraph.getWidth()) {
                scaledPreferredSizeForGraph.setSize(graphDimension.getWidth(), scaledPreferredSizeForGraph.getHeight());
            }
        }
        return this.scaledPreferredSizeForGraph;
    }

    public boolean isSalvo() {
        return salvo;
    }

    public void setSalvo(boolean salvo) {
        this.salvo = salvo;
    }

    public boolean isModified() {
        return this.modified;
    }

    protected int getQtdCopias(String nome, List objetos) {
        int qtd = 0;
        for (Object objeto : objetos) {
            if (((Objeto) objeto).getNome().matches("^" + nome + "_copia_\\d+")) {
                String[] splitString = ((Objeto) objeto).getNome().split("_");
                qtd = Integer.parseInt(splitString[splitString.length - 1]);
            }
        }
        return qtd;
    }

    protected List<Object> getValues(Object[] cells) {
        List<Object> values = new ArrayList<>();
        for (Object cell : cells) {
            values.add(((mxCell) cell).getValue());
        }
        return values;
    }

    @Override
    public Graph getGraph() {
        return (Graph) super.getGraph(); //To change body of generated methods, choose Tools | Templates.
    }

    public void newUndoableEdit() {
        undoManager.undoableEditHappened(new mxUndoableEdit(this) {
            public void dispatch() {
                ((mxGraphModel) graph.getModel()).fireEvent(new mxEventObject(mxEvent.CHANGE, "edit", this, "changes", changes));
            }
        });
    }

    public abstract void afterUndoChange();

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }
    
    
}
