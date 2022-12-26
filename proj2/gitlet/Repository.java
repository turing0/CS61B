package gitlet;

import java.io.File;
import java.io.IOException;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGINGAREA_DIR = join(GITLET_DIR, "staging");
    public static final File ADDITION_FILE = join(STAGINGAREA_DIR, "ADDITION");
    public static final File REMOVAL_FILE = join(STAGINGAREA_DIR, "REMOVAL");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /* TODO: fill in the rest of this class. */

    public static void handleInit() {
        if (GITLET_DIR.exists()) {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            STAGINGAREA_DIR.mkdir();
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
            Addition ad = new Addition();
            ad.saveAddition();
            try {
                HEAD_FILE.createNewFile();
//                ADDITION_FILE.createNewFile();
//                REMOVAL_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // initial commit
            String id = makeCommit("initial commit");
            // update HEAD
            writeContents(HEAD_FILE, id);

        }
    }

    public static void handleStatus() {
        validateGitletDirectory();
        // Branches
        System.out.printf("=== Branches ===\n*master\n");
        // TODO
        System.out.println();
        // Staged Files
        System.out.printf("=== Staged Files ===\n");
        Addition ad = Addition.fromFile(ADDITION_FILE);
        for (String filaName : ad.getKeySet()) {
            System.out.printf("%s\n", filaName);
        }
        System.out.println();
        // Removed Files
        System.out.printf("=== Removed Files ===\n");
        // TODO
        System.out.println();


    }
    public static void handleAdd(String fileName) {
        validateGitletDirectory();
        if (!join(CWD, fileName).exists()) {
            exitWithError("File does not exist.");
        }

        Commit cm = Commit.fromFile(Repository.HEAD_FILE);

        if (cm.getFileMap() != null && cm.getFileMap().get(fileName) != null) {
            String targetBlobID = cm.getFileMap().get(fileName);
            if (getFileSha1(fileName).equals(targetBlobID)) {
                exitWithSuccess("");
            } else {
                String blid = makeBlob(fileName);
                Addition ad = Addition.fromFile(ADDITION_FILE);
                ad.addAndSave(fileName, blid);
                // TODO: remove it from the staging area if it is already there
                //  (as can happen when a file is changed, added, and then changed back to it’s original version).
                //  The file will no longer be staged for removal (see gitlet rm),
                //  if it was at the time of the command.
            }
        } else {
            // make blob
            String blid = makeBlob(fileName);
            // update staging area addition
            Addition ad = Addition.fromFile(ADDITION_FILE);
            ad.addAndSave(fileName, blid);
        }

    }

    public static void handleCommit(String msg) {
        validateGitletDirectory();
        Addition ad = Addition.fromFile(Repository.ADDITION_FILE);
        if (ad.size() == 0) {
            exitWithSuccess("No changes added to the commit.");
        }
        Commit cm = Commit.fromFile(Repository.HEAD_FILE);
        Commit newCm = new Commit(cm, msg);

        for (String key : ad.getKeySet()) {
            // TODO: if has the same
//            newCm.addfileId(ad.get(key));
            newCm.addFile(key, ad.get(key));
        }
        ad.clearAndSave();

        newCm.updateIDAndSave();
        // update HEAD
        writeContents(HEAD_FILE, newCm.getID());

    }

    public static void handleCheckout(String[] args) {
        validateGitletDirectory();
        switch(args.length) {
            case 3:
                fileCheckout(args[2], readContentsAsString(HEAD_FILE));
                break;
            case 4:
                String targetID = args[1];
                String fileName = args[3];
                fileCheckout(fileName, targetID);
                break;
            case 2:
                // TODO

                break;
        }

    }

    public static void fileCheckout(String fileName, String id) {
        Commit cm = Commit.fromID(id);
        if (cm == null) {
            exitWithError("No commit with that id exists.");
        }
        String blid = cm.getFileBlobID(fileName);
        if (blid == null) {
            exitWithError("File does not exist in that commit.");
        }
        Blob bl = Blob.fromID(blid);
        File targetFile = join(CWD, fileName);
        writeContents(targetFile, bl.getContents());
    }
    public static String makeCommit(String msg) {
        Commit cm = new Commit(msg);
        cm.saveCommit();
        return cm.getID();
    }

    public static String makeBlob(String fileName) {
        Blob bl = new Blob(fileName);
        bl.saveBlob();
        return bl.getID();
    }

    public static void handleLog() {
        validateGitletDirectory();

        Commit cm = Commit.fromFile(HEAD_FILE);
        while (cm != null) {
            System.out.println(cm);
            cm = Commit.fromID(cm.getParentID());
        }

    }

    public static void validateGitletDirectory() {
        if (!GITLET_DIR.exists()) {
            exitWithError("Not in an initialized Gitlet directory.");
        }
    }

}
