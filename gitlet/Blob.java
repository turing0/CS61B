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

//    public static Blob fromID(String id) {
//        if (id == null) {
//            return null;
//        }
//        File blFile = join(Repository.BLOB_DIR, id);
//        if (blFile.exists()) {
//            return readObject(blFile, Blob.class);
//        }
//        return null;
//    }
    public static Blob fromID(String id) {
        if (id == null) {
            return null;
        }
        return readObject(getObjectFile(id), Blob.class);
//        File blFilePath = join(Repository.BLOB_DIR, id.substring(0, 2));
//        if (blFilePath.exists()) {
//            File blFile = join(blFilePath, id.substring(2));
//            if (blFile.exists()) {
//                return readObject(blFile, Blob.class);
//            }
//        }
    }

//    public void saveBlob() {
//        File blFile = new File(Repository.BLOB_DIR, id);
//        try {
//            blFile.createNewFile();
//            writeObject(blFile, this);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public void saveBlob() {
//        File blFilePath = join(Repository.BLOB_DIR, id.substring(0, 2));
//        if (!blFilePath.exists()) {
//            blFilePath.mkdir();
//        }
//        File blFile = join(blFilePath, id.substring(2));
//        try {
//            blFile.createNewFile();
//            writeObject(blFile, this);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public void saveBlob() {
        createObjectFile(id, this);
    }


}
