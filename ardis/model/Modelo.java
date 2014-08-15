package ardis.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.formula.functions.T;

public abstract class Modelo implements Cloneable, Serializable {

    protected String nome;
    protected List<Objeto> objetos;
    protected String path;

    public Modelo() {

        this.objetos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Objeto> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<Objeto> objetos) {
        this.objetos = objetos;
    }

    public void addObjeto(Objeto objeto) {
        this.objetos.add(objeto);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public <T extends Objeto> List<T> getObjetosByClass(Class<T> objetoClass) {
        List<T> objetosRetorno = new ArrayList<>();
        for (Objeto objeto : objetos) {
            if (objetoClass.isAssignableFrom(objeto.getClass())) {
                objetosRetorno.add((T) objeto);
            }
        }
        return objetosRetorno;
    }

    @Override
    public Modelo clone() throws CloneNotSupportedException {
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(this);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            Modelo clonedObject = (Modelo) in.readObject();

            return clonedObject;

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
}
