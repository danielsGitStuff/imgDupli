package data;

import io.FsDirectory;
import io.FsRelated;

import java.util.HashSet;

public class Directory extends FsRelated {
    private Directory parentDirectory;
    private HashSet<Directory> subDirectories = new HashSet<>();
    private HashSet<LeFile> leFiles = new HashSet<>();
    private FsDirectory fsDirectory;

    public Directory(FsDirectory fsDirectory) {
        this.fsDirectory = fsDirectory;
    }

    public FsDirectory getFsDirectory() {
        return fsDirectory;
    }

    public void addLeFile(LeFile leFile) {
        leFiles.add(leFile);
    }

    public void addSubDirectory(Directory subDir) {
        subDirectories.add(subDir);
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    @Override
    public String toString() {
        return "[" + fsDirectory.getPath() + "]";
    }

    @Override
    public void resetFlags() {
        highlighted = false;
        subDirectories.forEach(Directory::resetFlags);
        leFiles.forEach(LeFile::resetFlags);
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

    public void cleanGraph() {
        HashSet<LeFile> files = (HashSet<LeFile>) this.leFiles.clone();
        files.forEach(LeFile::cleanGraph);
        HashSet<Directory> directories = (HashSet<Directory>) this.subDirectories.clone();
        directories.forEach(Directory::cleanGraph);
        checkRemoval();
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
