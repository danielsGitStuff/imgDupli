package data;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.CustomFile;
import io.FileSystem;
import io.HashRelatedFileStorage;

public class DuplicationStructureBuilder {

    private HashRelatedFileStorage hashRelatedFileStorage = new HashRelatedFileStorage();
    private final FileSystem root;
    private Map<FileSystem, Directory> fileSystemDirectoryMap = new HashMap<>();
    private Map<FileSystem, Directory> fileSystemDirectoryMapBackup = new HashMap<>();

    /**
     * @param root
     * @param hashCollisions <String, List<>> ... String is hash value
     */
    public DuplicationStructureBuilder(FileSystem root, Map<String, List<File>> hashCollisions) {
        this.root = root;
        fileSystemDirectoryMap.put(root, new Directory(root));
        build(hashCollisions);
    }

    public void backup() {
        //TODO implement this
        hashRelatedFileStorage.backup();
        fileSystemDirectoryMapBackup = new HashMap<>(fileSystemDirectoryMap);
    }

    public void restore() {
        //TODO implement this
        hashRelatedFileStorage.restore();
        fileSystemDirectoryMap = fileSystemDirectoryMapBackup;
        fileSystemDirectoryMapBackup = null;
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

    public void rebuild() {
        fileSystemDirectoryMap = new HashMap<>();
        hashRelatedFileStorage = new HashRelatedFileStorage();
        fileSystemDirectoryMap.put(root, new Directory(root));
        traverseDown(root);
        // it is rebuilt but there might be single files in it
        hashRelatedFileStorage.getSingles().forEach(f->f.hide());
    }

    private void traverseDown(FileSystem fileSystem) {
        Directory directory = traverseToRoot(fileSystem);
        fileSystemDirectoryMap.put(fileSystem, directory);
        fileSystem.getFiles().forEach((string, customFile) -> {
            LeFile leFile = new LeFile(customFile);
            directory.addLeFile(leFile);
            leFile.setDirectory(directory);
            hashRelatedFileStorage.put(leFile.getHash(), leFile);
        });
        fileSystem.getDirectories().forEach((string, fs) -> traverseDown(fs));
    }

    private void build(Map<String, List<File>> hashCollisions) {
        fileSystemDirectoryMap = new HashMap<>();
        hashRelatedFileStorage = new HashRelatedFileStorage();
        hashCollisions.keySet().forEach(hash -> {
            List<File> files = hashCollisions.get(hash);
            files.forEach(file -> {
                CustomFile customFile = root.openFile(file);
                customFile.setHash(hash);
                LeFile leFile = new LeFile(customFile);
                FileSystem fileSystem = customFile.getFileSystem();
                Directory directory = traverseToRoot(fileSystem);
                directory.addLeFile(leFile);
                leFile.setDirectory(directory);
                hashRelatedFileStorage.put(hash, leFile);
            });
        });
    }


    public HashRelatedFileStorage getHashRelatedFileStorage() {
        return hashRelatedFileStorage;
    }

    public FileSystem getRoot() {
        return root;
    }

    public void resetHighlighted() {
        fileSystemDirectoryMap.get(root).resetFlags();
    }

    public Directory getRootDirectory() {
        return fileSystemDirectoryMap.get(root);
    }
}
