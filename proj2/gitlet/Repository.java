package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    public static final File BRANCH_DIR = join(GITLET_DIR, "refs", "heads");
    public static final File ADDITION_FILE = join(STAGINGAREA_DIR, "ADDITION");
    public static final File REMOVAL_FILE = join(STAGINGAREA_DIR, "REMOVAL");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    public static final File MASTER_FILE = join(BRANCH_DIR, "master");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /* TODO: fill in the rest of this class. */

    public static void handleInit() {
        if (GITLET_DIR.exists()) {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            STAGINGAREA_DIR.mkdir();
            BRANCH_DIR.mkdirs();
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
            Addition ad = new Addition();
            ad.saveAddition();
            try {
                MASTER_FILE.createNewFile();
                HEAD_FILE.createNewFile();
//                ADDITION_FILE.createNewFile();
//                REMOVAL_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // initial commit
            String id = makeCommit("initial commit");
            // update branch master
            writeContents(MASTER_FILE, id);
            // update HEAD
            updateHEAD("master");
        }
    }

    public static void handleStatus() {
        validateGitletDirectory();
        // Branches
        String curBranch = getCurrentBranchName();
        System.out.printf("=== Branches ===\n*%s\n", curBranch);
        // TODO: output remain branches
        List<String> branches = getAllBranchName();
        for (String br : branches) {
            if (!br.equals(curBranch)) {
                System.out.println(br);
            }
        }

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

        Commit cm = Commit.fromFile(getBranchFile());

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
        Commit cm = Commit.fromFile(getBranchFile());
        Commit newCm = new Commit(cm, msg);

        for (String key : ad.getKeySet()) {
            // TODO: if has the same
            newCm.addFile(key, ad.get(key));
        }
        ad.clearAndSave();

        newCm.updateIDAndSave();
        // update branch
        writeContents(getBranchFile(), newCm.getID());

    }

    public static void handleBranch(String[] args) {
        validateGitletDirectory();
        String branchName = args[1];
        if (join(BRANCH_DIR, branchName).exists()) {
            exitWithError("A branch with that name already exists.");
        }
        File newBranchFile = join(BRANCH_DIR, branchName);
        try {
            newBranchFile.createNewFile();
            writeContents(newBranchFile, readContentsAsString(getBranchFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleRmBranch(String[] args) {
        validateGitletDirectory();
        String branchName = args[1];
        if (!join(BRANCH_DIR, branchName).exists()) {
            exitWithError("A branch with that name does not exist.");
        }if (getCurrentBranchName().equals(branchName)) {
            exitWithError("Cannot remove the current branch.");
        }

        File branchFile = join(BRANCH_DIR, branchName);
        branchFile.delete();
    }

    public static void handleCheckout(String[] args) {
        validateGitletDirectory();
        switch(args.length) {
            case 3:
                fileCheckout(args[2], readContentsAsString(getBranchFile()));
                break;
            case 4:
                fileCheckout(args[3], args[1]);
                break;
            case 2:
                String branchName = args[1];
                if (!join(BRANCH_DIR, branchName).exists()) {
                    exitWithError("No such branch exists.");
                }
                if (getCurrentBranchName().equals(branchName)) {
                    exitWithError("No need to checkout the current branch.");
                }
                // TODO:  If a working file is untracked in the current branch
                //  and would be overwritten by the checkout,
                if (1!=1) {
                    exitWithError("There is an untracked file in the way; delete it, or add and commit it first.");
                }
                if (readContentsAsString(getBranchFile()).equals(readContentsAsString(getBranchFile(branchName)))) {
                    // update HEAD
                    updateHEAD(branchName);
                } else {
                    Commit curCm = Commit.fromFile(getBranchFile());
                    Commit targetCm = Commit.fromFile(getBranchFile(branchName));
                    for (String fileName : curCm.getFileMap().keySet()) {
                        if (targetCm.getFileBlobID(fileName) == null) {
                            File f = join(CWD, fileName);
                            f.delete();
                        } else {
                            if (!targetCm.getFileBlobID(fileName).equals(curCm.getFileBlobID(fileName))) {
                                fileCheckout(fileName, targetCm.getID());
                            }
                        }
                    }
                    // case: new file -> create
                    for (String fileName : targetCm.getFileMap().keySet()) {
                        if (curCm.getFileBlobID(fileName) == null) {
                            fileCheckout(fileName, targetCm.getID());
                        }
                    }
                    // update HEAD
                    updateHEAD(branchName);
                }

                break;
        }

    }

    public static void fileCheckout(String fileName, String cmID) {
        Commit cm = Commit.fromID(cmID);
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
    public static String makeCommit(String message) {
        Commit cm = new Commit(null, message);
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

        Commit cm = Commit.fromFile(getBranchFile());
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
