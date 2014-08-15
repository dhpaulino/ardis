package ardis.model.conceitual.atributo.composto;

import ardis.model.Objeto;
import ardis.model.conceitual.ObjetoWithAttribute;
import ardis.model.conceitual.atributo.Atributo;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class AtributoCompostoImpl extends Atributo implements AtributoComposto {

    @XmlElementWrapper
    protected List<Atributo> attributes;

    public AtributoCompostoImpl() {
        super("Atributo Composto");
        this.attributes = new ArrayList<>();
    }

    @Override
    public void addAttribute(Atributo attribute) {
        //attribute.initialRender(graph, 0, 0);
        attributes.add(attribute);
    }

    @Override
    public void removeAttibute(Atributo attribute) {
        attributes.remove(attribute);
    }

    public List<Atributo> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Atributo> attributes) {
        this.attributes = attributes;
    }
}
