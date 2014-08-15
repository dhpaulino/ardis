/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ardis.ultil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Davisson
 */
public class CloneHandler {

    public static <T extends Object> T clone(T object) {
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(object);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            T clonedObject = (T) in.readObject();

            return clonedObject;

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CloneHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(CloneHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
}
