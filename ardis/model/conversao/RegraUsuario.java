/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model.conversao;

import ardis.model.Objeto;
import java.util.List;

/**
 *
 * @author Davisson
 */
//TODO: MUDAR ARRAYS PARA LISTA
public interface RegraUsuario<E extends Object, T extends Objeto> {

    public String getDescricao();

    public String getPegunta();

    public E[] getOpcoes();

    public void setOpcoesSelecionadas(List<E> opcoes);

    public E[] getOpcoesSelecionadas();

    public T getObjeto();

    public void setObjeto(T objeto);

    public boolean isMultiSelection();
}
