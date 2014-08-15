package ardis.model.conceitual;

import ardis.model.Modelo;
import ardis.model.Objeto;
import ardis.model.conceitual.atributo.Atributo;
import ardis.model.conceitual.entidade.Entidade;
import ardis.model.conceitual.relacionamento.Relacionamento;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ModeloConceitual extends Modelo implements Cloneable {

    public List<Relacionamento> getRelacionamentos(Entidade entidade) {

        List<Relacionamento> relacionamentos = new ArrayList<>();
        for (Objeto objeto : objetos) {

            if (objeto instanceof Relacionamento) {

                Relacionamento relacionamento = (Relacionamento) objeto;

                for (MembroRelacionamento membroRelacionamento : relacionamento.getMembros()) {

                    if (membroRelacionamento.getEntidade() == entidade) {

                        relacionamentos.add(relacionamento);
                    }
                }
            }
        }
        return relacionamentos;
    }

    public void removeRelacionamentos(Entidade entidade) {

        for (Iterator<Objeto> it = objetos.iterator(); it.hasNext();) {
            Objeto objeto = it.next();

            if (objeto instanceof Relacionamento) {

                Relacionamento relacionamento = (Relacionamento) objeto;

                for (MembroRelacionamento membroRelacionamento : relacionamento.getMembros()) {

                    if (membroRelacionamento.getEntidade() == entidade) {

                        it.remove();
                    }
                }
            }
        }

    }


    @Override
    public ModeloConceitual clone() throws CloneNotSupportedException {
        ModeloConceitual modelo = (ModeloConceitual) super.clone();

        List<Objeto> objetos = new ArrayList<>(this.objetos.size());

        for (Objeto objeto : this.objetos) {
            objetos.add(objeto.clone());
        }

        modelo.objetos = objetos;
        return modelo;



    }
}
