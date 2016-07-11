package io;

import interfaces.ICustomFileGlobalStorage;
import interfaces.IFileRepresentation;

import java.io.File;

@SuppressWarnings("serial")
public class FsFile extends File implements IFileRepresentation {

    private String filePath;
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

    @Override
    public boolean equals(Object obj) {
        System.out.println("FsFile.equals()");
        if (obj instanceof FsFile) {
            FsFile f = (FsFile) obj;
            if (hash != null && f.hash != null) {
                return f.hash.equals(hash);
            }
        }
        return false;
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
