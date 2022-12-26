package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.text.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public static final File COMMIT_DIR = join(".gitlet", "commit");
    private String id;
    private String message;
    private String timestamp;
    private String parent;
//    private List<String> fileIdList;
    private Map<String, String> fileMap;

    public Commit(String msg) {
//        id = sha1();
        message = msg;
        SimpleDateFormat ft = new SimpleDateFormat ("E MMM dd hh:mm:ss yyyy Z");
        if (msg.equals("initial commit")) {
            Date dstart = new Date(0);
//            System.out.println(ft.format(dstart));
//            timestamp = "Thu 1970.01.01 at 00:00:00 AM UTC";
            timestamp = ft.format(dstart);
        } else {
            Date dNow = new Date();
            timestamp = ft.format(dNow);
        }
        parent = null;
        fileMap = null;

        List<String> ls = new ArrayList<>();
        ls.add(message);
        ls.add(timestamp);
//        System.out.println("ls: " + ls);
        id = sha1(ls);
    }

    public Commit(Commit cm, String msg) {
        id = cm.getID();
        message = msg;
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("E MMM dd hh:mm:ss yyyy Z");
        timestamp = ft.format(dNow);
        parent = cm.getID();
        if (cm.getFileMap() == null) {
            fileMap = new HashMap<>();
        } else {
            fileMap = new HashMap<>(cm.getFileMap());
        }
    }

    public void addFile(String fileName, String id) {
        fileMap.put(fileName, id);
    }

    public static Commit fromID(String id) {
        if (id == null) {
            return null;
        }
        File cmFile = join(COMMIT_DIR, id);
        if (cmFile.exists()) {
            return readObject(cmFile, Commit.class);
        }
        return null;
    }
    public static Commit fromFile(File f) {
        String id = readContentsAsString(f);
        return fromID(id);
    }

    public void updateIDAndSave() {
        List<String> ls = new ArrayList<>(fileMap.values());
        ls.add(parent);
        ls.add(message);
        ls.add(timestamp);
        id = sha1(ls);
        saveCommit();
    }


    public String getID() {
        return id;
    }

    public String getParentID() {
        return parent;
    }
    public String getMessage() {
        return message;
    }

//    public List<String> getfileIdList() {
//        return fileIdList;
//    }
    public Map<String, String> getFileMap() {
        return fileMap;
    }

    public String getFileBlobID(String fileName) {
        if (fileMap == null) {
            return null;
        }
        return fileMap.get(fileName);
    }

    public void saveCommit() {
        File cmFile = new File(COMMIT_DIR, id);
        try {
            cmFile.createNewFile();
            writeObject(cmFile, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "===\ncommit %s\nDate: %s\n%s\n",
                id, timestamp, message);
    }

}
