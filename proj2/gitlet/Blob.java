package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static gitlet.Utils.*;
public class Blob implements Serializable{
    private final String id;
    private final String contents;

    public Blob(String fileName) {
        File file = new File(fileName);
        contents = readContentsAsString(file);
        id = getFileSha1(fileName);
    }

    public String getID() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public static Blob fromID(String id) {
        if (id == null) {
            return null;
        }
        File blFile = join(Repository.BLOB_DIR, id);
        if (blFile.exists()) {
            return readObject(blFile, Blob.class);
        }
        return null;
    }

    public void saveBlob() {
        File blFile = new File(Repository.BLOB_DIR, id);
        try {
            blFile.createNewFile();
            writeObject(blFile, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
