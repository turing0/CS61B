package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;

public class Addition implements Serializable {
    private Map<String, String> mp;

    public Addition() {
        mp = new HashMap<>();
    }

    public Set<String> getKeySet () {
        return mp.keySet();
    }

    public void addAndSave(String fileName, String blobid) {
        mp.put(fileName, blobid);
        saveAddition();
    }

    public String get(String key) {
        return mp.get(key);
    }
    public int size() {
        return mp.size();
    }

    public void saveAddition() {
        File adFile = Repository.ADDITION_FILE;
        try {
            adFile.createNewFile();
            writeObject(adFile, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAndSave() {
        mp = new HashMap<>();
        saveAddition();
    }

    public static Addition fromFile(File f) {
        return readObject(f, Addition.class);
    }

}

