/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.model;

/**
 *
 * @author Davisson
 */
public interface Objeto extends Cloneable{

    public String getNome();

    public String getDescricao();

    public void setNome(String Nome);

    public void setDescricao(String descricao);
    
    public int getOriginalHash();

    public void setOriginalHash(int originalHash);
    
    public Objeto clone() throws CloneNotSupportedException;

}
