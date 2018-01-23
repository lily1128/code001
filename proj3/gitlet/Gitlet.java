package gitlet;

/**
 * Created by lily1128 on 17/12/5.
 */

import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import java.io.File;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Gitlet class.
 *
 * @author Xinyun(Lily) Zhang
 */
public class Gitlet {
    /**
     * Map from branchName to its commit ID.
     */
    private HashMap<String, String> branches;
    /**
     * Current branch.
     */
    private String head;
    /**
     * Map from fileName to commit ID
     * Represents files tracked by current commit.
     */
    private HashMap<String, String> tracked;
    /**
     * Map from fileName to commit ID
     * Represents files in staging area.
     */
    private HashMap<String, String> stagingArea;
    /**
     * List of files to be untracked next commit.
     */
    private ArrayList<String> removed;

    /**
     * Constructor.
     */
    public Gitlet() {
        if (new File(".gitlet").isDirectory()) {
            branches = deserialization(".gitlet/branches");
            head = deserialization(".gitlet/head");
            tracked = deserialization(".gitlet/status/tracked");
            stagingArea = deserialization(".gitlet/status/stagingArea");
            removed = deserialization(".gitlet/status/removed");
        } else {
            branches = new HashMap<>();
            head = "";
            tracked = new HashMap<>();
            stagingArea = new HashMap<>();
            removed = new ArrayList<>();
        }
    }

    /**
     * Deserialization.
     * @param fileName is the value.
     * @param <T> type.
     * @return deserialized object.
     */
    static <T extends Serializable> T deserialization(String fileName) {
        try {
            ObjectInputStream out
                    = new ObjectInputStream
                    (new FileInputStream(new File(fileName)));
            @SuppressWarnings("unchecked")
            T rtn = (T) out.readObject();
            out.close();
            return rtn;
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Serialization.
     * @param fileName is the value.
     * @param c is another value.
     * @param <T> type.
     */
    static <T extends Serializable> void serialization(String fileName, T c) {
        try {
            ObjectOutputStream inp
                    = new ObjectOutputStream(new FileOutputStream(fileName));
            inp.writeObject(c);
            inp.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Find the split point.
     * @param a is the first c.
     * @param b is the second c.
     * @return the found point.
     */
    public static Commit findSplit(Commit a, Commit b) {
        HashSet<String> seen = new HashSet<>();
        Commit x = a;
        Commit y = b;
        Commit cur = a;
        boolean isA = true;
        while (!seen.contains(cur.getID())) {
            seen.add(cur.getID());

            if (isA) {
                if (x.getParentID() != null) {
                    x = x.getParent();
                    isA = false;
                }
                cur = y;

            } else {
                if (y.getParentID() != null) {
                    y = y.getParent();
                    isA = true;
                }
                cur = x;

            }
        }
        return cur;
    }

    /**
     * Check whether is file is modified from the splitpoint.
     * @param fileName is the file name.
     * @param c is the first point.
     * @param p is the second point.
     * @return the file is modified.
     */
    public static boolean isModified(String fileName,
                                     HashMap<String, String> c,
                                     HashMap<String, String> p) {
        if (c.containsKey(fileName) && p.containsKey(fileName)) {
            if (!c.get(fileName).equals(p.get(fileName))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether is file is modified from the splitpoint.
     * @param fileName is the file name.
     * @param c is the first point.
     * @param p is the second point.
     * @return the file is not modified.
     */
    public static boolean isNotModified(String fileName,
                                        HashMap<String, String> c,
                                        HashMap<String, String> p) {
        if (c.containsKey(fileName) && p.containsKey(fileName)) {
            if (c.get(fileName).equals(p.get(fileName))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the short ID.
     * @param id is the id.
     * @param list is the list.
     * @return shortID.
     */
    static String shortId(String id, String[] list) {
        int low = 0;
        int high = list.length - 1;
        int mid = (high + low) / 2;
        while (low <= high) {
            String sub = list[mid].substring(0, id.length());
            int compare = id.compareTo(sub);
            if (compare == 0) {
                return list[mid];
            } else if (compare < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
            mid = (low + high) / 2;
        }
        return null;
    }
    /**
     * @Return tracked.
     */
    public HashMap<String, String> getTracked() {
        return tracked;
    }

    /**
     * @Return stagingArea.
     */
    public HashMap<String, String> getStagingArea() {
        return stagingArea;
    }

    /**
     * Set stagingArea.
     * @param x is the value.
     */
    public void setStagingArea(HashMap<String, String> x) {
        stagingArea = x;
    }

    /**
     * @Return removed.
     */
    public ArrayList<String> getRemoved() {
        return removed;
    }

    /**
     * Set removed.
     * @param x is the value.
     */
    public void setRemoved(ArrayList<String> x) {
        removed = x;
    }

    /**
     * @Return headID.
     */
    public String getHeadId() {
        return branches.get(head);
    }

    /**
     * @Return head.
     */
    public Commit getHead() {
        return deserialization(".gitlet/commit/" + getHeadId());
    }

    /**
     * Save all things.
     */
    public void save() {
        serialization(".gitlet/branches", branches);
        serialization(".gitlet/head", head);
        serialization(".gitlet/status/tracked", tracked);
        serialization(".gitlet/status/stagingArea", stagingArea);
        serialization(".gitlet/status/removed", removed);
    }

    /**
     * Init.
     */
    public void init() {
        if (new File(".gitlet").isDirectory()) {
            System.out.println(
                    "A gitlet version-control system already "
                            + "exists in the current directory.");
            return;
        }
        new File(".gitlet").mkdir();
        new File(".gitlet/status").mkdir();
        new File(".gitlet/commit").mkdir();
        new File(".gitlet/blob").mkdir();

        Commit initial = new Commit();
        initial.save();
        branches.put("master", initial.getID());

        head = "master";

        save();

    }

    /**
     * Add the file.
     * @param fileName is the value.
     */
    public void add(String fileName) {
        File f = new File(fileName);

        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        removed.remove(fileName);
        Blob b = new Blob(fileName);
        Commit c = deserialization(".gitlet/commit/" + getHeadId());

        if (b.getID().equals(c.getFiles().get(fileName))) {
            return;
        }

        if (stagingArea.containsKey(fileName)) {
            new File(".gitlet/blob/" + stagingArea.get(fileName)).delete();
        }

        b.save();
        stagingArea.put(fileName, b.getID());
    }

    /**
     * Commit the file.
     * @param message is the msg.
     */
    public void commit(String message) {
        if (stagingArea.isEmpty() && removed.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }

        Commit c = new Commit(message, this);
        c.save();
        branches.put(head, c.getID());
    }

    /**
     * Remove the file.
     * @param fileName is the value.
     */
    public void rm(String fileName) {
        if (!stagingArea.containsKey(fileName)
                && !tracked.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (stagingArea.containsKey(fileName)) {
            File f = new File(".gitlet/blob/" + stagingArea.get(fileName));
            f.delete();
            stagingArea.remove(fileName);
        }

        if (tracked.containsKey(fileName)) {
            tracked.remove(fileName);
            Utils.restrictedDelete(fileName);
            removed.add(fileName);
        }
    }

    /**
     * Print.
     */
    public void log() {
        Commit cur = deserialization(".gitlet/commit/" + getHeadId());
        System.out.println("===");
        System.out.println("commit " + cur.getID());
        System.out.println("Date: " + cur.getTime());
        System.out.println(cur.getMessage());
        System.out.println();

        while (cur.getParentID() != null) {
            cur = deserialization(".gitlet/commit/" + cur.getParentID());
            System.out.println("===");
            System.out.println("commit " + cur.getID());
            System.out.println("Date: " + cur.getTime());
            System.out.println(cur.getMessage());
            System.out.println();
        }
    }

    /**
     * Print all.
     */
    public void globalLog() {
        File[] commits = new File(".gitlet/commit/").listFiles();
        for (File f : commits) {
            Commit cur = deserialization(".gitlet/commit/" + f.getName());
            System.out.println("===");
            System.out.println("commit " + cur.getID());
            System.out.println("Date: " + cur.getTime());
            System.out.println(cur.getMessage());
            System.out.println();
        }
    }

    /**
     * Find.
     * @param message is the value.
     */
    public void find(String message) {
        List<String> commits = Utils.plainFilenamesIn(".gitlet/commit");

        int count = 0;

        for (String s : commits) {
            Commit p = deserialization(".gitlet/commit/" + s);
            if (p.getMessage().equals(message)) {
                System.out.println(p.getID());
                count++;
            }
        }

        if (count == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Status.
     */
    public void status() {
        System.out.println("=== Branches ===");
        System.out.println("*" + head);

        TreeSet<String> b = new TreeSet<>();
        for (String f : branches.keySet()) {
            b.add(f);
        }

        for (String branch : b) {
            if (!branch.equals(head)) {
                System.out.println(branch);
            }
        }

        System.out.println();

        System.out.println("=== Staged Files ===");
        TreeSet<String> s = new TreeSet<>();
        for (String f : stagingArea.keySet()) {
            s.add(f);
        }

        for (String file : s) {
            System.out.println(file);
        }

        System.out.println();

        System.out.println("=== Removed Files ===");

        Collections.sort(removed);

        for (String file : removed) {
            System.out.println(file);
        }

        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();

    }

    /**
     * Checkout the file.
     * @param fileName is the value.
     */
    public void checkoutF(String fileName) {
        checkoutCF(branches.get(head), fileName);
    }

    /**
     * Checkout the file with given id.
     * @param id is the id.
     * @param fileName is the value.
     */
    public void checkoutCF(String id, String fileName) {
        String trueID;
        if (id.length() >= Utils.UID_LENGTH) {
            trueID = id;
            File f = new File(".gitlet/commit/" + trueID);
            if (!f.exists()) {
                System.out.println("No commit with that id exists.");
                return;
            }
        } else {
            String[] files = new File(".gitlet/commit").list();
            Arrays.sort(files);
            trueID = shortId(id, files);
            if (trueID == null) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        Commit c = deserialization(".gitlet/commit/" + trueID);

        if (!c.getFiles().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        File oldF = new File(".gitlet/blob/"
                + c.getFiles().get(fileName));
        File newF = new File(fileName);
        Utils.writeContents(newF,
                Utils.readContents(oldF));

    }

    /**
     * Checkout the branch.
     * @param branch is the value.
     */
    public void checkoutB(String branch) {
        if (!branches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            return;
        }

        if (branch.equals(head)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        Commit c = deserialization(".gitlet/commit/" + branches.get(branch));
        Set<String> working = new HashSet<>(Utils.plainFilenamesIn("."));

        working.removeAll(tracked.keySet());

        working.retainAll(c.getFiles().keySet());

        if (!working.isEmpty()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it or add it first.");
            return;
        }

        Set<String> keys = c.getFiles().keySet();
        Set<String> s = tracked.keySet();

        s.removeAll(keys);

        for (String name : s) {
            Utils.restrictedDelete(name);
        }

        for (String fileName : keys) {
            File oldF = new File(".gitlet/blob/" + c.getFiles().get(fileName));
            File newF = new File(fileName);
            Utils.writeContents(newF,
                    Utils.readContents(oldF));
        }

        head = branch;
        tracked = c.getFiles();
        stagingArea.clear();

    }

    /**
     * Add the branch.
     * @param branch is the value.
     */
    public void branch(String branch) {
        if (branches.containsKey(branch)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        branches.put(branch, getHeadId());
    }

    /**
     * Rm the branch.
     * @param branch is the value.
     */
    public void rmBranch(String branch) {
        if (!branches.containsKey(branch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (branch.equals(head)) {
            System.out.println("Cannot remove the current branch.");
            return;
        } else {
            branches.remove(branch);
        }
    }

    /**
     * Reset.
     * @param id is the value.
     */
    public void reset(String id) {
        String trueID;
        if (id.length() >= Utils.UID_LENGTH) {
            trueID = id;
            if (!new File(".gitlet/commit/" + trueID).exists()) {
                System.out.println("No commit with that id exists.");
                return;
            }
        } else {
            String[] files = new File(".gitlet/commit").list();
            Arrays.sort(files);
            trueID = shortId(id, files);
            if (trueID == null) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        Commit c = deserialization(".gitlet/commit/" + trueID);
        Set<String> working = new HashSet<>(Utils.plainFilenamesIn("."));

        for (String f : tracked.keySet()) {
            working.remove(f);
        }

        working.retainAll(c.getFiles().keySet());

        if (!working.isEmpty()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it or add it first.");
            return;
        }

        Set<String> keys = c.getFiles().keySet();
        Set<String> s = tracked.keySet();
        s.removeAll(keys);

        for (String name : s) {
            Utils.restrictedDelete(name);
        }
        for (String fileName : keys) {
            File oldF = new File(".gitlet/blob/"
                    + c.getFiles().get(fileName));
            File newF = new File(fileName);
            Utils.writeContents(newF,
                    Utils.readContents(oldF));
        }

        branches.put(head, c.getID());
        tracked = c.getFiles();
        stagingArea.clear();
    }

    /**
     * Merge.
     * @param branch is the value.
     */
    public void merge(String branch) {
        if (!stagingArea.isEmpty() || !removed.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        } else if (!branches.containsKey(branch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (head.equals(branch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Commit cur = deserialization(".gitlet/commit/" + getHeadId());
        Commit given = deserialization(".gitlet/commit/"
                + branches.get(branch));
        Commit splitPoint = findSplit(cur, given);

        HashSet<String> working = new HashSet<>(Utils.plainFilenamesIn("."));

        for (String f : tracked.keySet()) {
            working.remove(f);
        }

        for (String f : working) {
            if (!given.getFiles().containsKey(f)) {
                working.remove(f);
            }
        }

        if (!working.isEmpty()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it or add it first.");
            return;
        }

        if (branches.get(branch).equals(splitPoint.getID())) {
            System.out.println("Given branch is an"
                    + " ancestor of the current branch.");
            return;
        }

        if (getHeadId().equals(splitPoint.getID())) {
            head = branch;
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        mergeHelper(cur, given, splitPoint, branch);
    }

    /**
     * Merge helper.
     * @param x is the value1.
     * @param y is the value2.
     * @param z is the value3.
     * @param b is the value4.
     */
    public void mergeHelper(Commit x, Commit y, Commit z, String b) {
        HashMap<String, String> c = x.getFiles();
        HashMap<String, String> g = y.getFiles();
        HashMap<String, String> s = z.getFiles();
        HashSet<String> possible = new HashSet<>();
        possible.addAll(s.keySet());
        possible.addAll(g.keySet());
        possible.addAll(c.keySet());
        boolean conflict = false;
        for (String f : possible) {
            if (isModified(f, g, s)
                    && isNotModified(f, c, s)) {
                checkoutCF(y.getID(), f);
                add(f);
            } else if (!s.containsKey(f)
                    && !c.containsKey(f)) {
                checkoutCF(y.getID(), f);
                add(f);
            } else if (s.containsKey(f)
                    && !g.containsKey(f)
                    && isNotModified(f, c, s)) {
                rm(f);
            } else if ((isModified(f, c, s) && isModified(f, g, s)
                    && !c.get(f).equals(g.get(f)))
                    || (isModified(f, g, s)
                    && !c.containsKey(f))
                    || (isModified(f, c, s)
                    && !g.containsKey(f))) {
                writecontent(x.getFiles().get(f),
                        y.getFiles().get(f), f);
                conflict = true;
                add(f);
            }
        }

        commit("Merged " + b + " into " + head + ".");

        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Write the content.
     * @param a is the value1.
     * @param b is the value2.
     * @param c is the value3.
     */
    public static void writecontent(String a, String b, String c) {
        try {
            PrintWriter writer = new PrintWriter(c, "UTF-8");
            writer.println("<<<<<<< HEAD");
            if (a != null) {
                Scanner input1 = new Scanner(new File(".gitlet/blob/" + a));
                while (input1.hasNextLine()) {
                    writer.println(input1.nextLine());
                }
            }
            writer.println("=======");
            if (b != null) {
                Scanner input2 = new Scanner(new File(".gitlet/blob/" + b));
                while (input2.hasNextLine()) {
                    writer.println(input2.nextLine());
                }
            }
            writer.println(">>>>>>>");
            writer.close();
        } catch (IOException e) {
            System.out.println("Print contents wrong!");
        }
    }
}
