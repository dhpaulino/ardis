/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual;

import ardis.model.ObjetoGraficoImpl;
import ardis.model.conceitual.atributo.Atributo;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Davisson
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public abstract class ObjetoWithAttributeImpl extends ObjetoGraficoImpl implements ObjetoWithAttribute {

    @XmlElementWrapper
    protected List<Atributo> attributes;

    public List<Atributo> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Atributo> attributes) {
        this.attributes = attributes;
    }

    public ObjetoWithAttributeImpl() {
        this.attributes = new ArrayList<>();
    }

    public boolean hasPrimaryKey() {
        for (Atributo atributo : attributes) {
            if (atributo.isPrimaryKey()) {
                return true;
            }
        }
        return false;
    }

    public List<Atributo> getPrimaryKey() {
        List<Atributo> pk = new ArrayList<>();
        for (Atributo atributo : attributes) {

            if (atributo.isPrimaryKey()) {
                pk.add(atributo);
            }
        }

        return pk;
    }

    @Override
    public void addAttribute(Atributo attribute) {
        attributes.add(attribute);
    }

    @Override
    public void removeAttibute(Atributo attribute) {
        attributes.remove(attribute);
    }

    @Override
    public ObjetoWithAttributeImpl clone() throws CloneNotSupportedException {
        ObjetoWithAttributeImpl clone = (ObjetoWithAttributeImpl) super.clone();

        List<Atributo> atributos = new ArrayList<>();

        for (Atributo atributo : clone.getAttributes()) {

            atributos.add(atributo.clone());
        }
        return clone;
    }
}
