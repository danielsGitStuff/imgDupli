package data;

import interfaces.ICustomFileGlobalStorage;
import io.FsDirectory;
import io.FsFile;
import io.HashRelatedFileStorage;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DuplicationStructureBuilder implements ICustomFileGlobalStorage {

    private final FsDirectory root;
    private HashRelatedFileStorage<LeFile> hashRelatedLeFileStorage;
    private HashRelatedFileStorage<FsFile> hashRelatedCustomFileStorage;
    private Map<FsDirectory, Directory> fileSystemDirectoryMap = new HashMap<>();

    /**
     * @param root
     * @param hashCollisions <String, List<>> ... String is hash value
     */
    public DuplicationStructureBuilder(FsDirectory root, Map<String, List<File>> hashCollisions) {
        this.root = root;
        fileSystemDirectoryMap.put(root, new Directory(root));
        build(hashCollisions);
    }

    private Directory traverseToRoot(FsDirectory fsDirectory) {
        if (fileSystemDirectoryMap.get(fsDirectory) == null) {
            Directory directory = new Directory(fsDirectory);
            fileSystemDirectoryMap.put(fsDirectory, directory);
            if (fsDirectory.getParent() == null)
                return getRootDirectory();
            Directory parentDir = traverseToRoot(fsDirectory.getParent());
            directory.setParentDirectory(parentDir);
            parentDir.addSubDirectory(directory);
            return directory;
        } else {
            return fileSystemDirectoryMap.get(fsDirectory);
        }
    }

    private void traverseToRoot(FsFile fsFile) {
        FsDirectory fsDirectory = fsFile.getFsDirectory();
        if (fsDirectory != null) {
            traverseToRoot(fsDirectory);
        }
    }

    public void rebuildDirectoryGraph() {
        forget();
        traverseDownReset(root);
        // it is rebuilt but there might be single files in it
        hashRelatedLeFileStorage.getSingles().forEach(f -> f.hide());
    }

    public void forget() {
        fileSystemDirectoryMap = new HashMap<>();
        hashRelatedLeFileStorage = new HashRelatedFileStorage();
        fileSystemDirectoryMap.put(root, new Directory(root));
    }

    private void traverseDownReset(FsDirectory fsDirectory) {
        Directory directory = traverseToRoot(fsDirectory);
        fsDirectory.getFiles().forEach((string, customFile) -> {
            customFile.resetFlags();
            LeFile leFile = new LeFile(customFile);
            directory.addLeFile(leFile);
            leFile.setDirectory(directory);
            hashRelatedLeFileStorage.put(leFile.getHash(), leFile);
        });
        fsDirectory.getDirectories().forEach((string, fs) -> traverseDownReset(fs));
    }

    private void build(Map<String, List<File>> hashCollisions) {
        fileSystemDirectoryMap = new HashMap<>();
        hashRelatedLeFileStorage = new HashRelatedFileStorage();
        hashRelatedCustomFileStorage = new HashRelatedFileStorage<>();
        hashCollisions.keySet().forEach(hash -> {
            List<File> files = hashCollisions.get(hash);
            files.forEach(file -> {
                FsFile fsFile = root.openFile(file);
                fsFile.setHash(hash);
                processCustomFile(fsFile);
            });
        });
    }

    private void processCustomFile(FsFile fsFile) {
        if (fsFile.isMarkedForDeletion())
            System.out.println("DuplicationStructureBuilder.processCustomFile.del");
        if (fsFile.isHidden())
            System.out.println("DuplicationStructureBuilder.processCustomFile.hidden");
        LeFile leFile = new LeFile(fsFile);
        FsDirectory fsDirectory = fsFile.getFsDirectory();
        Directory directory = traverseToRoot(fsDirectory);
        directory.addLeFile(leFile);
        leFile.setDirectory(directory);
        hashRelatedLeFileStorage.put(leFile.getHash(), leFile);
        hashRelatedCustomFileStorage.put(leFile.getHash(), fsFile);
    }

    public void resetHighlighted() {
        fileSystemDirectoryMap.get(root).resetFlags();
    }

    public Directory getRootDirectory() {
        return fileSystemDirectoryMap.get(root);
    }

    public void write() {
        root.write(this);
    }

    public void removeLeFile(LeFile f) {
        hashRelatedLeFileStorage.removeFile(f);
    }

    public HashSet<LeFile> getHashRelatedLeFiles(String hash) {
        return hashRelatedLeFileStorage.get(hash);
    }

    public boolean atLeastTwoLeFiles(String hash) {
        return hashRelatedLeFileStorage.atLeastTwoCopies(hash);
    }

    @Override
    public void removeCustomFile(FsFile fsFile) {
        hashRelatedCustomFileStorage.removeFile(fsFile);
    }


}
