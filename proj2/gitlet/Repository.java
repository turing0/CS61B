package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Turing
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BRANCH_DIR = join(GITLET_DIR, "refs", "heads");
    public static final File STAGE_FILE = join(GITLET_DIR, "INDEX");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commits");
    public static final File MASTER_FILE = join(BRANCH_DIR, "master");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    public static void handleInit() {
        if (GITLET_DIR.exists()) {
            exitWithSuccess(
                    "A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            BRANCH_DIR.mkdirs();
            OBJECT_DIR.mkdir();
            COMMIT_DIR.mkdir();
            Stage stage = new Stage();
            stage.saveStage();
            try {
                MASTER_FILE.createNewFile();
                HEAD_FILE.createNewFile();
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
        System.out.printf("=== Branches ===\n");
        List<String> branches = getAllBranchName();
        for (String br : branches) {
            if (br.equals(curBranch)) {
                System.out.print("*");
            }
            System.out.println(br);
        }
        System.out.println();
        // Staged Files
        System.out.printf("=== Staged Files ===\n");
        Stage stage = Stage.fromFile(STAGE_FILE);
        for (String filaName : stage.getAdditionKeySet()) {
            System.out.printf("%s\n", filaName);
        }
        System.out.println();
        // Removed Files
        System.out.printf("=== Removed Files ===\n");
        for (String filaName : stage.getRemovalList()) {
            System.out.printf("%s\n", filaName);
        }
        System.out.println();

//        File[] fileList = join(CWD).listFiles(File::isFile);
        List<String> fileNames = plainFilenamesIn(CWD);
        Commit cm = Commit.fromFile(getBranchFile());
        // Modifications Not Staged For Commit
        System.out.printf("=== Modifications Not Staged For Commit ===\n");
        for (String fileName : stage.getAdditionKeySet()) {
            if (!join(fileName).exists()) {
                System.out.printf("%s (deleted)\n", fileName);
            }
        }
        for (String fileName : cm.getFileMap().keySet()) {
            if (!join(fileName).exists() && !stage.getRemovalList().contains(fileName)) {
                System.out.printf("%s (deleted)\n", fileName);
            }
        }

        if (fileNames != null) {
            for (String f : fileNames) {
                if (stage.get(f) != null) {
                    if (!getFileSha1(f).equals(stage.get(f))) {
                        System.out.printf("%s (modified)\n", f);
                    }
                } else {
                    if (cm.getFileMap().get(f) != null
                            && !getFileSha1(f).equals(cm.getFileMap().get(f))) {
                        System.out.printf("%s (modified)\n", f);
                    }
                }
            }

        }
        System.out.println();

        // Untracked Files
        System.out.printf("=== Untracked Files ===\n");
        if (fileNames != null) {
            for (String f : fileNames) {
                if (stage.get(f) == null && cm.getFileMap().get(f) == null) {
                    System.out.println(f);
                }
            }
        }
        System.out.println();

    }
    public static void handleAdd(String fileName) {
        validateGitletDirectory();
        if (!join(CWD, fileName).exists()) {
            exitWithSuccess("File does not exist.");
        }
        Commit cm = Commit.fromFile(getBranchFile());

        if (cm.getFileMap() != null && cm.getFileMap().get(fileName) != null) {
            Stage stage = Stage.fromFile(STAGE_FILE);
            if (stage.getRemovalList().contains(fileName)) {
                stage.clearAndSave();
                return;
            }
            String targetBlobID = cm.getFileMap().get(fileName);
            if (getFileSha1(fileName).equals(targetBlobID)) {
                exitWithSuccess("");
            } else {
                String blid = makeBlob(fileName);
                stage.stageForAddition(fileName, blid);

            }
        } else {
            // make blob
            String blid = makeBlob(fileName);
            // update staging area addition
            Stage stage = Stage.fromFile(STAGE_FILE);
            stage.stageForAddition(fileName, blid);
        }

    }

    public static void handleCommit(String[] args) {
        validateGitletDirectory();
        validateCmMessage(args);

        String msg = args[1];
        Stage stage = Stage.fromFile(Repository.STAGE_FILE);
        if (stage.additionSize() == 0 && stage.removalSize() == 0) {
            exitWithSuccess("No changes added to the commit.");
        }
        Commit cm = Commit.fromFile(getBranchFile());
        Commit newCm = new Commit(cm, msg);
        // addition
        if (stage.additionSize() != 0) {
            for (String key : stage.getAdditionKeySet()) {
                // TODO: if has the same
                newCm.addFile(key, stage.get(key));
            }
        }
        // removal
        if (stage.removalSize() != 0) {
            for (String fileName : stage.getRemovalList()) {
                newCm.removeFile(fileName);
            }
        }

        stage.clearAndSave();
        newCm.updateIDAndSave();
        // update branch
        writeContents(getBranchFile(), newCm.getID());

    }

    public static void handleRm(String[] args) {
        validateGitletDirectory();
        String fileName = args[1];
        Stage stage = Stage.fromFile(Repository.STAGE_FILE);
        if (stage.get(fileName) != null) {
            stage.remove(fileName);
            return;
        }
        Commit cm = Commit.fromFile(getBranchFile());
        if (cm.getFileMap().containsKey(fileName)) {
            stage.stageForRemoval(fileName);
            File file = join(CWD, fileName);
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        exitWithSuccess("No reason to remove the file.");
    }

    public static void handleBranch(String[] args) {
        validateGitletDirectory();
        String branchName = args[1];
        if (join(BRANCH_DIR, branchName).exists()) {
            exitWithSuccess("A branch with that name already exists.");
        }
        File newBranchFile = join(BRANCH_DIR, branchName);
        try {
            newBranchFile.createNewFile();
            writeContents(newBranchFile, readContentsAsString(getBranchFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkBranchExist(String branchName) {
        if (!join(BRANCH_DIR, branchName).exists()) {
            exitWithSuccess("A branch with that name does not exist.");
        }
    }

    public static void handleRmBranch(String[] args) {
        validateGitletDirectory();
        String branchName = args[1];
        checkBranchExist(branchName);
        if (getCurrentBranchName().equals(branchName)) {
            exitWithSuccess("Cannot remove the current branch.");
        }

        File branchFile = join(BRANCH_DIR, branchName);
        branchFile.delete();
    }

    public static void handleCheckout(String[] args) {
        validateGitletDirectory();
        if (args.length > 2 && !args[args.length - 2].equals("--")) {
            exitWithSuccess("Incorrect operands.");
        }
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
                    exitWithSuccess("No such branch exists.");
                }
                if (getCurrentBranchName().equals(branchName)) {
                    exitWithSuccess("No need to checkout the current branch.");
                }
                if (!readContentsAsString(getBranchFile()).equals(readContentsAsString(getBranchFile(branchName)))) {
                    Commit curCm = Commit.fromFile(getBranchFile());
                    Commit targetCm = Commit.fromFile(getBranchFile(branchName));
                    commitCheckout(curCm, targetCm);
                }
                // update HEAD
                updateHEAD(branchName);
                break;
            default:
                break;
        }
    }

    public static void checkStageClean() {
        Stage stage = Stage.fromFile(STAGE_FILE);
        if (stage.additionSize() != 0 || stage.removalSize() != 0) {
            exitWithSuccess("You have uncommitted changes.");
        }
    }

    public static void handleMerge(String[] args) {
        validateGitletDirectory();
        String branchName = args[1];
        checkStageClean();
        checkBranchExist(branchName);
        if (getCurrentBranchName().equals(branchName)) {
            exitWithSuccess("Cannot merge a branch with itself.");
        }

        Commit cm = Commit.fromFile(getBranchFile());
        Commit targetCm = Commit.fromFile(getBranchFile(branchName));
        checkUntrackedOverwritten(cm, targetCm);

        Set<String> s = new HashSet<>();
        while (cm != null) {
            s.add(cm.getID());
            cm = Commit.fromID(cm.getParentID());
        }
        if (s.contains(targetCm.getID())) {
            exitWithSuccess("Given branch is an ancestor of the current branch.");
        }
        Commit curCm = Commit.fromFile(getBranchFile());
        // find split point
        Commit splitCm = null;
        while (targetCm != null) {
            if (s.contains(targetCm.getID())) {
                splitCm = Commit.fromID(targetCm.getID());
                break;
            }
            targetCm = Commit.fromID(targetCm.getParentID());
        }
        targetCm = Commit.fromFile(getBranchFile(branchName));
        if (splitCm.getID().equals(curCm.getID())) {
            commitCheckout(curCm, targetCm);
//            updateHEAD(branchName);
            exitWithSuccess("Current branch fast-forwarded.");
        }

        Map<String, String> curFileMap = curCm.getFileMap();
        Map<String, String> splitFileMap = splitCm.getFileMap();
        Map<String, String> targetFileMap = targetCm.getFileMap();
        Set<String> allFiles = new HashSet<>();
        Stage stage = Stage.fromFile(STAGE_FILE);
        boolean conflictExist = false;
        for (String file : curFileMap.keySet()) {
            allFiles.add(file);
        }
        for (String file : targetFileMap.keySet()) {
            allFiles.add(file);
        }
        // 7 rules
        for (String fileName : allFiles) {
            if (splitFileMap.containsKey(fileName)) {

                if (curFileMap.containsKey(fileName) && targetFileMap.containsKey(fileName)) {
                    if (!splitFileMap.get(fileName).equals(targetFileMap.get(fileName)) && splitFileMap.get(fileName).equals(curFileMap.get(fileName))) {
                        stage.stageForAddition(fileName, targetFileMap.get(fileName)); // rule 1
                        fileCheckout(fileName, targetCm.getID());
                        continue;
                    }
                    if (!splitFileMap.get(fileName).equals(curFileMap.get(fileName)) && splitFileMap.get(fileName).equals(targetFileMap.get(fileName))) {
                        continue; // rule 2
                    }
                }
                if (curFileMap.containsKey(fileName)) { // this file in head and split, not in target
                    if (splitFileMap.get(fileName).equals(curFileMap.get(fileName)) && !targetFileMap.containsKey(fileName)) {
                        stage.stageForRemoval(fileName); // rule 6
                        join(CWD, fileName).delete();
                        continue;
                    }
                } else { // this file in head and target, not in split
                    if (splitFileMap.get(fileName).equals(targetFileMap.get(fileName)) && !curFileMap.containsKey(fileName)) {
                        continue; // rule 7
                    }
                }

                conflictExist = dealConflict(splitCm, curCm, targetCm, fileName); // check rule 3
            } else {
                if (!splitFileMap.containsKey(fileName) && !targetFileMap.containsKey(fileName) && curFileMap.containsKey(fileName)) {
                    continue; // rule 4
                }
                if (!splitFileMap.containsKey(fileName) && !curFileMap.containsKey(fileName) && targetFileMap.containsKey(fileName)) {
                    stage.stageForAddition(fileName, targetFileMap.get(fileName)); // rule 5
                    fileCheckout(fileName, targetCm.getID());
                    continue;
                }
            }
        }
        if (conflictExist) {
            System.out.println("Encountered a merge conflict.");
        }
        // automatically commit
        mergeCommit(branchName);
    }

    public static boolean dealConflict(Commit splitCm, Commit curCm, Commit targetCm, String fileName) {
        boolean conflictExist = false;
        if (!targetCm.getFileMap().containsKey(fileName)) { // modified in head, deleted in target
            conflictExist = true;
        } else if (!curCm.getFileMap().containsKey(fileName)) { // modified in target, deleted in head
            conflictExist = true;
        } else if (!curCm.getFileMap().get(fileName).equals(targetCm.getFileMap().get(fileName))) { // both modified
            conflictExist = true;
        }
        if (conflictExist) {
            Stage stage = Stage.fromFile(STAGE_FILE);
            String contents1 = "";
            String contents2 = "";
            if (curCm.getFileMap().containsKey(fileName)) {
                contents1 = readContentsAsString(join(CWD, fileName));
            }
            if (targetCm.getFileMap().containsKey(fileName)) {
                Blob bl = Blob.fromID(targetCm.getFileBlobID(fileName));
                contents2 = bl.getContents();
            }
            String contents = String.format("<<<<<<< HEAD\n%s=======\n%s>>>>>>>", contents1, contents2);
            writeContents(join(CWD, fileName), contents);
            String blid = makeBlob(fileName);
            stage.stageForAddition(fileName, blid);
        }
        return conflictExist;
    }

    public static void mergeCommit(String targetBranch) {
        String msg = String.format("Merged %s into %s.", targetBranch, getCurrentBranchName());
        Stage stage = Stage.fromFile(Repository.STAGE_FILE);
        if (stage.additionSize() == 0 && stage.removalSize() == 0) {
            exitWithSuccess("No changes added to the commit.");
        }
        Commit cm = Commit.fromFile(getBranchFile());
        Commit cm2 = Commit.fromFile(getBranchFile(targetBranch));
        Commit newMgCm = new Commit(cm, cm2, msg);
        // addition
        if (stage.additionSize() != 0) {
            for (String key : stage.getAdditionKeySet()) {
                newMgCm.addFile(key, stage.get(key));
            }
        }
        // removal
        if (stage.removalSize() != 0) {
            for (String fileName : stage.getRemovalList()) {
                newMgCm.removeFile(fileName);
            }
        }

        stage.clearAndSave();
        newMgCm.updateIDAndSave();
        // update branch
        writeContents(getBranchFile(), newMgCm.getID());
    }

    public static void handleReset(String[] args) {
        validateGitletDirectory();
        String cmID = getFullID(args[1]);
        checkCommitID(cmID);
        Commit curCm = Commit.fromFile(getBranchFile());
        Commit targetCm = Commit.fromID(cmID);
        commitCheckout(curCm, targetCm);
        // update branch's head
        writeContents(getBranchFile(), cmID);
        Stage st = Stage.fromFile(STAGE_FILE);
        st.clearAndSave();
    }

    public static void checkUntrackedOverwritten(Commit curCm, Commit targetCm) {
        for (String fileName : targetCm.getFileMap().keySet()) {
            if (join(CWD, fileName).exists() && !curCm.getFileMap().containsKey(fileName)) {
                exitWithSuccess(
                        "There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
    }

    public static void commitCheckout(Commit curCm, Commit targetCm) {
        // check  if a working file is untracked in the current branch
        // and would be overwritten by the checkout
        checkUntrackedOverwritten(curCm, targetCm);
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
        if (targetCm.getFileMap() != null) {
            for (String fileName : targetCm.getFileMap().keySet()) {
                if (curCm.getFileBlobID(fileName) == null) {
                    fileCheckout(fileName, targetCm.getID());
                }
            }
        }
    }
    public static void fileCheckout(String fileName, String cmID) {
        cmID = getFullID(cmID);
        Commit cm = Commit.fromID(cmID);
        if (cm == null) {
            exitWithSuccess("No commit with that id exists.");
        }
        String blid = cm.getFileBlobID(fileName);
        if (blid == null) {
            exitWithSuccess("File does not exist in that commit.");
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

    public static void handleFind(String[] args) {
        validateGitletDirectory();
        String msg = args[1];

        boolean findFlag = false;
        List<String> fileNames = plainFilenamesIn(COMMIT_DIR);
        for (String fileName : fileNames) {
            Commit cm = Commit.fromID(fileName);
            if (cm.getMessage().equals(msg)) {
                findFlag = true;
                System.out.println(cm.getID());
            }
        }

        if (!findFlag) {
            exitWithSuccess("Found no commit with that message.");
        }
    }
    public static void handleLog() {
        validateGitletDirectory();

        Commit cm = Commit.fromFile(getBranchFile());
        while (cm != null) {
            System.out.println(cm);
            cm = Commit.fromID(cm.getParentID());
        }
    }

    public static void handleGlobalLog() {
        validateGitletDirectory();

        List<String> fileNames = plainFilenamesIn(COMMIT_DIR);

        for (String fileName : fileNames) {
            Commit cm = Commit.fromID(fileName);
            System.out.println(cm);
        }
    }

    public static void validateGitletDirectory() {
        if (!GITLET_DIR.exists()) {
            exitWithSuccess("Not in an initialized Gitlet directory.");
        }
    }

}
