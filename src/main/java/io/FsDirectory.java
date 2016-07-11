package io;

import interfaces.ICustomFileGlobalStorage;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FsDirectory {
	public static final String FS_SEPARATOR = System.getProperty("file.separator");
	private final String path;
	private final Map<String, FsFile> files = new HashMap<>();
	private final Map<String, FsDirectory> directories = new HashMap<>();
	private FsFile target;
	private FsDirectory parent;

	public FsDirectory(String path) {
		if (path.endsWith(FS_SEPARATOR)) {
			path = path.substring(0, path.lastIndexOf(FS_SEPARATOR));
		}
		this.path = path;
		this.target = new FsFile(path);
	}

	private FsDirectory(String path, FsDirectory parent) {
		this.parent = parent;
		this.path = path;
	}

	private String getAbsolutePath() {
		return (parent == null) ? path : parent.getAbsolutePath() + FS_SEPARATOR + path;
	}

	public FsFile openFile(File file) {
		// File file can be a directory too
		String fPath = file.getAbsolutePath();
		String aPath = getAbsolutePath();
		if (fPath.startsWith(aPath)) {
			fPath = fPath.substring(aPath.length() + FS_SEPARATOR.length(), fPath.length());
		}
		if (fPath.length() == 0) {
			return target;
		}
		// is it a dir?
		if (fPath.contains(FS_SEPARATOR)) {
			String path = fPath.substring(0, fPath.indexOf(FS_SEPARATOR));
			FsDirectory subDir = openSubDirectory(path);
			return subDir.openFile(file);
		} else {
			// nope, file
			FsFile fsFile = openSubFile(fPath);
			files.put(fsFile.getName(), fsFile);
			return fsFile;
		}
	}

	private FsDirectory openSubDirectory(String path) {
		FsDirectory subDir;
		if (directories.containsKey(path)) {
			subDir = directories.get(path);
		} else {
			subDir = new FsDirectory(path, this);
			directories.put(path, subDir);
		}
		return subDir;
	}

	private FsFile openSubFile(String path) {
		FsFile subFile;
		if (files.containsKey(path)) {
			subFile = files.get(path);
		} else {
			subFile = new FsFile(getAbsolutePath() + FS_SEPARATOR + path);
			subFile.setFsDirectory(this);
		}
		return subFile;
	}

	@Override
	public String toString() {
		return (parent == null) ? "[Root]" : FS_SEPARATOR + path;
	}

	public Map<String, FsDirectory> getDirectories() {
		return directories;
	}

	public Map<String, FsFile> getFiles() {
		return files;
	}

	public String getPath() {
		return path;
	}

	public FsDirectory getParent() {
		return parent;
	}

	public File getFile() {
		return new File(getAbsolutePath());
	}

	public void write(ICustomFileGlobalStorage globalStorage) {
		Set<String> fileKeySet = new HashSet<>(files.keySet());
		fileKeySet.forEach(hash -> {
			FsFile file = files.get(hash);
			file.write(globalStorage);
		});
		Set<String> dirKeySet = new HashSet<>(directories.keySet());
		dirKeySet.forEach(path -> {
			FsDirectory fsDirectory = directories.get(path);
			fsDirectory.write(globalStorage);
		});
		checkRemoval();
	}

	public void removeCustomFile(FsFile fsFile) {
		files.remove(fsFile.getName());
		checkRemoval();
	}

	private void checkRemoval() {
		if (files.size() + directories.size() == 0) {
			parent.removeSubFileSystem(this);
		}
	}

	private void removeSubFileSystem(FsDirectory fsDirectory) {
		// we want root to stay here
		if (parent != null) {
			directories.remove(fsDirectory.getPath());
			checkRemoval();
		}
	}
}
