package io;

import interfaces.ICustomFileGlobalStorage;
import interfaces.IFileRepresentation;

import java.io.File;

@SuppressWarnings("serial")
public class FsFile extends File implements IFileRepresentation {

    private final String filePath;
    private boolean markedForDeletion = false;
    private FsDirectory fsDirectory;
    private String hash;

    public FsFile(String path) {
        super(path);
        this.filePath = path;
    }


    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    public FsDirectory getFsDirectory() {
        return fsDirectory;
    }

    public void setFsDirectory(FsDirectory fsDirectory) {
        this.fsDirectory = fsDirectory;
    }

    @Override
    public String toString() {
        String path = filePath;
        String fileName = path;
        if (path.contains(FsDirectory.FS_SEPARATOR)) {
            fileName = path.substring(path.lastIndexOf(FsDirectory.FS_SEPARATOR) + 1);
        }
        return fileName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public void hide() {

    }

    /**
     * Identity is the path, never the content hash: two copies of the same image
     * share a hash but are different files. The mutable fields (hash, markedForDeletion,
     * fsDirectory) are deliberately left out so the hashCode never changes while the
     * object sits in a HashSet/HashMap.
     * Delegates to File so path comparison follows the platform rules
     * (case-insensitive on Windows) and stays symmetric with plain File objects.
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void write(ICustomFileGlobalStorage globalStorage) {
        if (markedForDeletion) {
            this.delete();
            this.removeFromGraph(globalStorage);
        }
    }

    public void removeFromGraph(ICustomFileGlobalStorage globalStorage) {
        fsDirectory.removeCustomFile(this);
        globalStorage.removeCustomFile(this);
    }

    public void resetFlags() {
        markedForDeletion = false;
    }
}
