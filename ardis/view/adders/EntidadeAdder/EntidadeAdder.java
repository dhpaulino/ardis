/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.view.adders.EntidadeAdder;

import ardis.model.conceitual.ModeloConceitual;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.logico.ModeloLogico;
import ardis.model.logico.tabela.Tabela;
import ardis.view.adders.Adder;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Davisson
 */
public class EntidadeAdder implements Adder {

    private Class<Entidade> classe;
    private static EntidadeAdder instance;

    public void prepare(Class classe) {
        this.classe = classe;
    }

    @Override
    public boolean add(mxGraph graph, ModeloConceitual modelo, double x, double y) {
        try {

            graph.getModel().beginUpdate();
            Entidade entidade = classe.newInstance();

            entidade.initialRender(graph, (mxCell) graph.getDefaultParent(), x, y);

         

            modelo.addObjeto(entidade);

        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(EntidadeAdder.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            graph.getModel().endUpdate();
        }
        return true;
    }

    public static EntidadeAdder getInstance() {

        if (instance == null) {
            instance = new EntidadeAdder();
        }
        return instance;
    }
}
