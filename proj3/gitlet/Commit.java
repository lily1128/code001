package gitlet;

/**
 * Created by lily1128 on 17/12/5.
 */

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Commit class.
 *
 * @author Xinyun(Lily) Zhang
 */
public class Commit implements Serializable {
    /**
     * id.
     */
    private String id;
    /**
     * files.
     */
    private HashMap<String, String> files;
    /**
     * time.
     */
    private String time;
    /**
     * message.
     */
    private String message;
    /**
     * parentid.
     */
    private String parentID;

    /**
     * Constructor.
     * @param m value1.
     * @param g value2.
     */
    public Commit(String m, Gitlet g) {
        files = new HashMap<>(g.getHead().files);

        files.keySet().removeAll(g.getRemoved());

        files.putAll(g.getStagingArea());

        g.getTracked().putAll(g.getStagingArea());

        g.setStagingArea(new HashMap<>());

        g.setRemoved(new ArrayList<>());

        parentID = g.getHeadId();

        DateFormat dateFormatInit
                = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        Calendar calenit = Calendar.getInstance();
        time = dateFormatInit.format(calenit.getTime());

        message = m;

        id = Utils.sha1(Utils.serialize(this));
    }

    /**
     * Constructor of initial commit.
     */
    public Commit() {
        files = new HashMap<>();

        message = "initial commit";

        DateFormat dateFormatInit
                = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        Date d = new Date(0);
        time = dateFormatInit.format(d.getTime());

        parentID = null;

        id = Utils.sha1(Utils.serialize(this));
    }

    /**
     * parent.
     * @return parent
     */
    public Commit getParent() {
        return Gitlet.deserialization(".gitlet/commit/" + parentID);
    }

    /**
     * id.
     * @return id
     */
    public String getID() {
        return id;
    }

    /**
     * files.
     * @return files
     */
    public HashMap<String, String> getFiles() {
        return files;
    }

    /**
     * time.
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     * message.
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * parentid.
     * @return parentid
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * save.
     */
    public void save() {
        Gitlet.serialization(".gitlet/commit/" + id, this);
    }
}
