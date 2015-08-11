package io;

import java.io.File;
import java.util.Random;

@SuppressWarnings("serial")
public class CustomFile extends File {

	private String filePath;
	private boolean markedForDeletion = false;
	private FileSystem directory;
	private String hash;
	private final long randomId;

	public long getRandomId() {
		return randomId;
	}

	public boolean isMarkedForDeletion() {
		return markedForDeletion;
	}

	public void setMarkedForDeletion(boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}

	public CustomFile(String path) {
		super(path);
		this.filePath = path;
		this.randomId = new Random().nextLong();
	}

	public void setDirectory(FileSystem fileSystem) {
		this.directory = fileSystem;
	}

	public FileSystem getFileSystem() {
		return directory;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getHash() {
		return hash;
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

	public CustomFile returnThis() {
		return this;
	}

	public void write() {
		if (markedForDeletion) {
			this.delete();
			directory.removeCustomFile(this);
		}
	}

	public void resetFlags() {
		markedForDeletion = false;
	}
}
