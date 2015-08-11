package io;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileSystem {
	public static final String FS_SEPARATOR = System.getProperty("file.separator");
	private final String path;
	private CustomFile target;
	private final Map<String, CustomFile> files = new HashMap<>();
	private final Map<String, FileSystem> directories = new HashMap<>();
	private FileSystem parent;

	public FileSystem(String path) {
		if (path.endsWith(FS_SEPARATOR)) {
			path = path.substring(0, path.lastIndexOf(FS_SEPARATOR));
		}
		this.path = path;
		this.target = new CustomFile(path);
	}

	private FileSystem(String path, FileSystem parent) {
		this.parent = parent;
		this.path = path;
	}

	private String getAbsolutePath() {
		return (parent == null) ? path : parent.getAbsolutePath() + FS_SEPARATOR + path;
	}

	public CustomFile openFile(File file) {
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
			FileSystem subDir = openSubDirectory(path);
			return subDir.openFile(file);
		} else {
			// nope, file
			CustomFile customFile = openSubFile(fPath);
			files.put(customFile.getName(), customFile);
			return customFile;
		}
	}

	private FileSystem openSubDirectory(String path) {
		FileSystem subDir;
		if (directories.containsKey(path)) {
			subDir = directories.get(path);
		} else {
			subDir = new FileSystem(path, this);
			directories.put(path, subDir);
		}
		return subDir;
	}

	private CustomFile openSubFile(String path) {
		CustomFile subFile;
		if (files.containsKey(path)) {
			subFile = files.get(path);
		} else {
			subFile = new CustomFile(getAbsolutePath() + FS_SEPARATOR + path);
			subFile.setDirectory(this);
		}
		return subFile;
	}

	@Override
	public String toString() {
		return (parent == null) ? "[Root]" : FS_SEPARATOR + path;
	}

	// "C:\\users\\xor\\picTest\\bilderse\\pics\\lemminge\\"
	public static void main(String[] args) {
		FileSystem root = new FileSystem("C:\\users\\xor\\picTest\\bilderse\\");
		File file = new File("C:\\users\\xor\\picTest\\bilderse\\pics\\lemminge\\hitler.jpg");
		CustomFile result = root.openFile(file);
		HashRelatedFileStorage storage = new HashRelatedFileStorage();
		System.out.println("done");
	}

	public Map<String, FileSystem> getDirectories() {
		return directories;
	}

	public Map<String, CustomFile> getFiles() {
		return files;
	}

	public String getPath() {
		return path;
	}

	public FileSystem getParent() {
		return parent;
	}

	public File getFile() {
		return new File(getAbsolutePath());
	}

	public void write() {
		Set<String> fileKeySet = new HashSet<>(files.keySet());
		fileKeySet.forEach(hash -> {
			CustomFile file = files.get(hash);
			file.write();
		});
		// files.forEach((hash, file) -> file.write());
		Set<String> dirKeySet = new HashSet<>(directories.keySet());
		dirKeySet.forEach(path -> {
			FileSystem fileSystem = directories.get(path);
			fileSystem.write();
		});
//		directories.forEach((name, fileSytem) -> fileSytem.write());
		checkRemoval();
	}

	public void removeCustomFile(CustomFile customFile) {
		files.remove(customFile.getName());
		// checkRemoval();
	}

	private void checkRemoval() {
		if (files.size() + directories.size() == 0) {
			parent.removeSubFileSystem(this);
		}
	}

	private void removeSubFileSystem(FileSystem fileSystem) {
		// we want root to stay here
		if (parent != null) {
			directories.remove(fileSystem.getPath());
			// checkRemoval();
		}
	}

	public void resetFlags() {
		files.forEach((str, customFile) -> customFile.resetFlags());
		directories.forEach((str, fileSystem) -> fileSystem.resetFlags());
	}
}
