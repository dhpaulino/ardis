/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.control.impressao;

import com.mxgraph.swing.mxGraphComponent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;/**
 *
 * @author Arthur
 */
public class Impressao {

    public void print(mxGraphComponent graphComponent) {
        
        PrinterJob pj = PrinterJob.getPrinterJob();
        
        if (pj.printDialog()) {
            PageFormat pf = graphComponent.getPageFormat();
            Paper paper = new Paper();
            double margin = 36;
            paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());
            pf.setPaper(paper);
            pj.setPrintable(graphComponent, pf);
        }

        try {
            pj.print();
        } catch (PrinterException e2) {
            System.out.println(e2);
        }
    }
}
