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
    private final List<String> parents;
    private Map<String, String> fileMap;
    private static final long serialVersionUID = 6529689098266657690L;

    public Commit(Commit cm, String msg) {
        message = msg;
        if (cm == null) {
            timestamp = getDate(0);
            parents = new ArrayList<>();
            fileMap = new HashMap<>();
            List<String> ls = new ArrayList<>(fileMap.values());
            ls.add(message);
            ls.add(timestamp);
            id = sha1(ls);
        } else {
            timestamp = getDate();
            parents = List.of(cm.getID());
//            parent = cm.getID();
            fileMap = new HashMap<>(cm.getFileMap());
            id = cm.getID();
        }
    }

    public Commit(Commit cm, Commit cm2, String msg) {
        message = msg;
        parents = List.of(cm.getID(), cm2.getID());
        fileMap = new HashMap<>();
        timestamp = getDate();
        fileMap = new HashMap<>(cm.getFileMap());
        id = cm.getID();
    }

    public void addFile(String fileName, String blobid) {
        fileMap.put(fileName, blobid);
    }

    public void removeFile(String fileName) {
        fileMap.remove(fileName);
    }

    public static Commit fromID(String id) {
        if (id == null) {
            return null;
        }
        File objectFile = getCmObjectFile(id);
        if (objectFile == null) {
            return null;
        }
        return readObject(objectFile, Commit.class);
    }
    public static Commit fromFile(File f) {
        String id = readContentsAsString(f);
        return fromID(id);
    }
    public static Commit fromOtherFile(String remotePath, File f) {
//        System.out.println(readContentsAsString(f));
        File objectFile = join(remotePath, ".gitlet/objects/commits", readContentsAsString(f));
//        System.out.println(objectFile);
        return readObject(objectFile, Commit.class);
    }

    public void updateIDAndSave() {
        List<String> ls = new ArrayList<>(fileMap.values());
        ls.addAll(parents);
        ls.add(message);
        ls.add(timestamp);
        id = sha1(ls);
        saveCommit();
    }

    public String getID() {
        return id;
    }
    public String getParentID() {
        if (parents.size() == 0) {
            return null;
        }
        return parents.get(0);
    }
    public String getSecondParentID() {
        if (parents.size() < 2) {
            return null;
        }
        return parents.get(1);
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
        createCmObjectFile(id, this);
    }

    @Override
    public String toString() {
        if (parents.size() == 2) {
            return String.format(
                    "===\ncommit %s\nMerge: %s %s\nDate: %s\n%s\n",
                    id, getParentID().substring(0, 7), getSecondParentID().substring(0, 7), timestamp, message);
        }
        return String.format(
                "===\ncommit %s\nDate: %s\n%s\n",
                id, timestamp, message);
    }

}
