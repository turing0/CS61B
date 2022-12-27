package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Turing
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String id;
    private final String message;
    private final String timestamp;
    private final String parent;
    private Map<String, String> fileMap;

    public Commit(Commit cm, String msg) {
        message = msg;
        if (cm == null) {
            timestamp = getDate(0);
            parent = null;
//            fileMap = null;
            fileMap = new HashMap<>();
            List<String> ls = new ArrayList<>(fileMap.values());
            ls.add(message);
            ls.add(timestamp);
            id = sha1(ls);
        } else {
            timestamp = getDate();
            parent = cm.getID();
            fileMap = new HashMap<>(cm.getFileMap());
//            if (cm.getFileMap() == null) {
//                fileMap = new HashMap<>();
//            } else {
//                fileMap = new HashMap<>(cm.getFileMap());
//            }
            id = cm.getID();
        }
    }

    public void addFile(String fileName, String id) {
        fileMap.put(fileName, id);
    }

    public void removeFile(String fileName) {
        fileMap.remove(fileName);
    }

    public static Commit fromID(String id) {
        if (id == null) {
            return null;
        }
        File objectFile = getObjectFile(id);
        if (objectFile == null) {
            return null;
        }
        return readObject(objectFile, Commit.class);
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
        createObjectFile(id, this);
    }

    @Override
    public String toString() {
        return String.format(
                "===\ncommit %s\nDate: %s\n%s\n",
                id, timestamp, message);
    }

}
