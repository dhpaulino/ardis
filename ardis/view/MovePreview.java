/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view;

import com.mxgraph.swing.handler.mxMovePreview;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import java.awt.event.MouseEvent;

/**
 *
 * @author Israel
 */
public class MovePreview extends mxMovePreview{
    
    private mxRectangle dirty;
    
    public MovePreview(mxGraphComponent graphComponent) {
        super(graphComponent);
        
    }

    @Override
    public void update(MouseEvent e, double dx, double dy, boolean clone) {
        super.update(e, dx, dy, clone);
        
        dirty = lastDirty;
        lastDirty = preview.show();

        if (dirty != null) {
            dirty.add(lastDirty);
        } else {
            dirty = lastDirty;
        }
    }

    public mxRectangle getDirty() {
        return dirty;
    }

    public void setDirty(mxRectangle dirty) {
        this.dirty = dirty;
    }
}
