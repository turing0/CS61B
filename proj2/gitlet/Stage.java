package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    private Map<String, String> additionMap;
    //    private Map<String, String> removalMap;
    private List<String> removalList;

    public Stage() {
        additionMap = new HashMap<>();
        removalList = new ArrayList<>();
    }

    public Set<String> getAdditionKeySet() {
        return additionMap.keySet();
    }

    public List<String> getRemovalList() {
        return removalList;
    }

    public void stageToRemoval(String fileName) {
        removalList.add(fileName);
        saveStage();
    }

    public void stageToAddition(String fileName, String blobid) {
        additionMap.put(fileName, blobid);
        saveStage();
    }

    public String get(String key) {
        return additionMap.get(key);
    }

    public void remove(String key) {
        additionMap.remove(key);
        saveStage();
    }
    public int additionSize() {
        return additionMap.size();
    }

    public int removalSize() {
        return removalList.size();
    }

    public void saveStage() {
        File stageFile = Repository.STAGE_FILE;
        try {
            stageFile.createNewFile();
            writeObject(stageFile, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAndSave() {
        additionMap = new HashMap<>();
        removalList = new ArrayList<>();
        saveStage();
    }

    public static Stage fromFile(File f) {
        return readObject(f, Stage.class);
    }
}
