package ardis.model;

import java.io.Serializable;

public abstract class ObjetoImpl implements Serializable, Objeto {

    protected String nome;
    protected String descricao;
    protected int originalHash;

    public ObjetoImpl() {
        originalHash = hashCode();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getOriginalHash() {
        return originalHash;
    }

    public void setOriginalHash(int originalHash) {
        this.originalHash = originalHash;
    }

    @Override
    public String toString() {
        return this.nome;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass() ? ((Objeto) obj).getOriginalHash() == this.originalHash : false;
    }

    @Override
    public Objeto clone() throws CloneNotSupportedException {
        Objeto clone = (Objeto) super.clone();
        clone.setOriginalHash(this.originalHash);
        return clone;
    }
}
