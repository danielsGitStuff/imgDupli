package data;

import java.util.ArrayList;
import java.util.HashSet;

import io.CustomFile;
import io.FileSystem;
import io.FSRelated;

public class Directory extends FSRelated {
    private Directory parentDirectory;
    private HashSet<Directory> subDirectories = new HashSet<>();
    private HashSet<LeFile> leFiles = new HashSet<>();
    private FileSystem fileSystem;

    public Directory(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void addLeFile(LeFile leFile) {
        leFiles.add(leFile);
    }

    public void addSubDirectory(Directory subDir) {
        subDirectories.add(subDir);
    }

    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    @Override
    public String toString() {
        return "[" + fileSystem.getPath() + "]";
    }

    @Override
    public void resetFlags() {
        highlighted = false;
        subDirectories.forEach(dir -> dir.resetFlags());
        leFiles.forEach(f -> f.resetFlags());
    }

    public HashSet<Directory> getSubDirectories() {
        return subDirectories;
    }

    public HashSet<LeFile> getLeFiles() {
        return leFiles;
    }

    public HashSet<String> getHashes() {
        HashSet<String> result = new HashSet<>();
        leFiles.stream().filter(f -> !f.isHidden()).forEach(leFile -> result.add(leFile.getHash()));
        return result;
    }

    public void removeLeFile(LeFile leFile) {
        leFiles.remove(leFile);
        checkRemoval();
    }

    private void checkRemoval() {
        if (subDirectories.size() == 0 && leFiles.size() == 0 && parentDirectory != null) {
            parentDirectory.removeSubDirectory(this);
            hide();
        }
    }

    private void removeSubDirectory(Directory directory) {
        subDirectories.remove(directory);
        checkRemoval();
    }

}
