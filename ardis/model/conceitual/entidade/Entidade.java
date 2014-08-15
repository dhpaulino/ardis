package ardis.model.conceitual.entidade;

import ardis.model.ObjetoGrafico;
import ardis.model.conceitual.ObjetoWithAttributeImpl;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Entidade")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(namespace = "ARDIS/Modelo/Entidade")
public class Entidade extends ObjetoWithAttributeImpl implements ObjetoGrafico {

    @XmlAttribute
    protected boolean fraca;

    public Entidade() {
        this.nome = "Entidade";
    }

    public boolean isFraca() {
        return fraca;
    }

    public void setFraca(boolean fraca) {
        this.fraca = fraca;
    }

    @Override
    public mxCell initialRender(mxGraph graph, mxCell parent, double xPosition, double yPosition) {
        return (mxCell) graph.insertVertex(parent, null, this, xPosition, yPosition, 100, 50, "entity");
    }
}
