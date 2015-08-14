package io;

import interfaces.ICustomFileGlobalStorage;
import interfaces.IFileRepresentation;

import java.io.File;
import java.util.Random;

@SuppressWarnings("serial")
public class CustomFile extends File implements IFileRepresentation {

    private final long randomId;
    private String filePath;
    private boolean markedForDeletion = false;
    private FileSystem fileSystem;
    private String hash;

    public CustomFile(String path) {
        super(path);
        this.filePath = path;
        this.randomId = new Random().nextLong();
    }

    public long getRandomId() {
        return randomId;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String toString() {
        String path = filePath;
        String fileName = path;
        if (path.contains(FileSystem.FS_SEPARATOR)) {
            fileName = path.substring(path.lastIndexOf(FileSystem.FS_SEPARATOR) + 1);
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
        System.out.println("CustomFile.equals()");
        if (obj instanceof CustomFile) {
            CustomFile f = (CustomFile) obj;
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
        fileSystem.removeCustomFile(this);
        globalStorage.removeCustomFile(this);
    }

    public void resetFlags() {
        markedForDeletion = false;
    }
}
