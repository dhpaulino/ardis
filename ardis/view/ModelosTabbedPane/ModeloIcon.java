/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.ModelosTabbedPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 *
 * @author Davisson
 */
public class ModeloIcon implements Icon {

    private String simbolo;
    private int x_pos;
    private int y_pos;
    private int width;
    private int height;

    public ModeloIcon(String simbolo) {
        this.simbolo = simbolo;
        width = 16;
        height = 16;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        this.x_pos = x;
        this.y_pos = y;
        g.setColor(Color.black);

        int y_p = y + 2;
        g.drawLine(x + 1, y_p, x + 12, y_p);
        g.drawLine(x + 1, y_p + 13, x + 12, y_p + 13);
        g.drawLine(x, y_p + 1, x, y_p + 12);
        g.drawLine(x + 13, y_p + 1, x + 13, y_p + 12);


        g.setFont(new Font("default", Font.BOLD, g.getFont().getSize()));
        g.drawString(simbolo, x + 4, y_p + 11);
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public int getIconWidth() {
        return width;
    }
}
