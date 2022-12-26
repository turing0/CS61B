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
    private String id;
    private String message;
    private String timestamp;
    private String parent;
    private Map<String, String> fileMap;

    public Commit(Commit cm, String msg) {
        message = msg;
        if (cm == null) {
            timestamp = getDate(0);
            parent = null;
            fileMap = null;
            List<String> ls = new ArrayList<>();
            ls.add(message);
            ls.add(timestamp);
            id = sha1(ls);
        } else {
            timestamp = getDate();
            parent = cm.getID();
            if (cm.getFileMap() == null) {
                fileMap = new HashMap<>();
            } else {
                fileMap = new HashMap<>(cm.getFileMap());
            }
            id = cm.getID();
        }

    }


    public void addFile(String fileName, String id) {
        fileMap.put(fileName, id);
    }

    public static Commit fromID(String id) {
        if (id == null) {
            return null;
        }
        File cmFile = join(Repository.COMMIT_DIR, id);
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
        File cmFile = new File(Repository.COMMIT_DIR, id);
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
