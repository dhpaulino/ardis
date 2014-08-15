/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.logico.indice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Israel
 */
@XmlType(name = "TipoIndice")
@XmlEnum
public enum TipoIndice {
    PRIMARY, INDEX, UNIQUE;
}
