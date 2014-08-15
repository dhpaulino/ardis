package ardis.control.modelo;

import ardis.control.conversao.ConceitualToLogico;
import ardis.control.conversao.LogicoToConceitual;
import ardis.control.xml.Xml;
import ardis.model.conceitual.ModeloConceitual;
import ardis.model.logico.ModeloLogico;
import java.util.List;
import ardis.model.Modelo;
import ardis.model.conversao.RegraUsuario;
import ardis.view.conceitual.ConceitualGraph;
import com.mxgraph.view.mxGraph;
import java.io.File;


public class ModeloFacade {

    private Xml xml;
    private LogicoToConceitual logicoToConceitual;
    private ConceitualToLogico conceitualToLogico;

    public ModeloFacade() {
        conceitualToLogico = new ConceitualToLogico();
        logicoToConceitual = new LogicoToConceitual();
        xml = new Xml();
    }

    public ModeloConceitual gerarConceitual(ModeloLogico modeloLogico) {
        return logicoToConceitual.converter(modeloLogico);
    }

    public List<RegraUsuario> analisarConceitual(ConceitualGraph conceitualGraph) {

        List<RegraUsuario> regrasParaDefinir = conceitualToLogico.analisar(conceitualGraph);
        return regrasParaDefinir;
    }

    public ConceitualToLogico getConceitualToLogico() {
        return conceitualToLogico;
    }

    public ModeloLogico gerarLogico(ModeloConceitual modeloConceitual, List<RegraUsuario> preferenciasConversao) {
        return conceitualToLogico.converter(preferenciasConversao);

    }

    public void salvar(mxGraph graph, Modelo modelo, File file) {
        xml.objectToXml(graph, file.getPath());
    }

    public void salvar(Modelo modelo, mxGraph graph) {
        xml.objectToXml(graph, modelo.getPath());
    }
}
