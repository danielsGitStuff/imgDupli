package data;

import interfaces.ICustomFileGlobalStorage;
import io.CustomFile;
import io.FileSystem;
import io.HashRelatedFileStorage;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DuplicationStructureBuilder implements ICustomFileGlobalStorage {

    private final FileSystem root;
    private HashRelatedFileStorage<LeFile> hashRelatedLeFileStorage;
    private HashRelatedFileStorage<CustomFile> hashRelatedCustomFileStorage;
    private Map<FileSystem, Directory> fileSystemDirectoryMap = new HashMap<>();

    /**
     * @param root
     * @param hashCollisions <String, List<>> ... String is hash value
     */
    public DuplicationStructureBuilder(FileSystem root, Map<String, List<File>> hashCollisions) {
        this.root = root;
        fileSystemDirectoryMap.put(root, new Directory(root));
        build(hashCollisions);
    }

    private Directory traverseToRoot(FileSystem fileSystem) {
        if (fileSystemDirectoryMap.get(fileSystem) == null) {
            Directory directory = new Directory(fileSystem);
            fileSystemDirectoryMap.put(fileSystem, directory);
            if (fileSystem.getParent() == null)
                return getRootDirectory();
            Directory parentDir = traverseToRoot(fileSystem.getParent());
            directory.setParentDirectory(parentDir);
            parentDir.addSubDirectory(directory);
            return directory;
        } else {
            return fileSystemDirectoryMap.get(fileSystem);
        }
    }

    private void traverseToRoot(CustomFile customFile) {
        FileSystem fileSystem = customFile.getFileSystem();
        if (fileSystem != null) {
            traverseToRoot(fileSystem);
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

    private void traverseDownReset(FileSystem fileSystem) {
        Directory directory = traverseToRoot(fileSystem);
        fileSystem.getFiles().forEach((string, customFile) -> {
            customFile.resetFlags();
            LeFile leFile = new LeFile(customFile);
            directory.addLeFile(leFile);
            leFile.setDirectory(directory);
            hashRelatedLeFileStorage.put(leFile.getHash(), leFile);
        });
        fileSystem.getDirectories().forEach((string, fs) -> traverseDownReset(fs));
    }

    private void build(Map<String, List<File>> hashCollisions) {
        fileSystemDirectoryMap = new HashMap<>();
        hashRelatedLeFileStorage = new HashRelatedFileStorage();
        hashRelatedCustomFileStorage = new HashRelatedFileStorage<>();
        hashCollisions.keySet().forEach(hash -> {
            List<File> files = hashCollisions.get(hash);
            files.forEach(file -> {
                CustomFile customFile = root.openFile(file);
                customFile.setHash(hash);
                processCustomFile(customFile);
            });
        });
    }

    private void processCustomFile(CustomFile customFile) {
        if (customFile.isMarkedForDeletion())
            System.out.println("DuplicationStructureBuilder.processCustomFile.del");
        if (customFile.isHidden())
            System.out.println("DuplicationStructureBuilder.processCustomFile.hidden");
        LeFile leFile = new LeFile(customFile);
        FileSystem fileSystem = customFile.getFileSystem();
        Directory directory = traverseToRoot(fileSystem);
        directory.addLeFile(leFile);
        leFile.setDirectory(directory);
        hashRelatedLeFileStorage.put(leFile.getHash(), leFile);
        hashRelatedCustomFileStorage.put(leFile.getHash(), customFile);
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
    public void removeCustomFile(CustomFile customFile) {
        hashRelatedCustomFileStorage.removeFile(customFile);
    }


}
