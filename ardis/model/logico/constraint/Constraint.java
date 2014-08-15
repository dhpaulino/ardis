package ardis.model.logico.constraint;

import ardis.model.ObjetoImpl;
import ardis.model.logico.coluna.Coluna;
import java.util.List;

public abstract class Constraint extends ObjetoImpl {
        
    public abstract List<Coluna> getColunas();

}
