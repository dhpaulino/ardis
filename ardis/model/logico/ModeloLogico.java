package ardis.model.logico;

import ardis.model.Modelo;
import ardis.model.logico.tabela.Tabela;
import java.util.List;

public class ModeloLogico extends Modelo {
    
    public List<Tabela> getTabelas(){
        return super.getObjetosByClass(Tabela.class);
    }
}
