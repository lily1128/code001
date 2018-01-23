package gitlet;

import java.io.Serializable;
import java.io.File;

/**
 * Created by lily1128 on 17/12/5.
 */
/**
 * Blob class.
 *
 * @author Xinyun(Lily) Zhang
 */
public class Blob implements Serializable {
    /**
     * file.
     */
    private byte[] file;
    /**
     * id.
     */
    private final String id;

    /**
     * Constructor.
     * @param fileName is value
     */
    public Blob(String fileName) {
        File f = new File(fileName);
        file = Utils.readContents(f);
        id = Utils.sha1(file);
    }

    /**
     * save.
     */
    public void save() {
        File f = new File(".gitlet/blob/" + id);
        Utils.writeContents(f, file);
    }

    /**
     * getID.
     * @return id
     */
    public String getID() {
        return id;
    }
}
