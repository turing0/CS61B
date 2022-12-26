package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
public class Blob implements Serializable {
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
        return readObject(getObjectFile(id), Blob.class);
    }

    public void saveBlob() {
        createObjectFile(id, this);
    }


}
