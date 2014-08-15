/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conceitual;

import ardis.model.ObjetoGrafico;
import ardis.model.conceitual.atributo.Atributo;
import java.util.List;

/**
 *
 * @author Davisson
 */
public interface ObjetoWithAttribute extends ObjetoGrafico {

    public List<Atributo> getAttributes();

    public void addAttribute(Atributo attribute);

    public void removeAttibute(Atributo attribute);
}
