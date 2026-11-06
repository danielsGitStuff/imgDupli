package main;

import java.io.FileFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import tools.Tools;

public class Settings {

	private int fileThreadRatio;
	private String rootPath;
	private Set<String> fileTypes;

	public int getFileThreadRatio() {
		return fileThreadRatio;
	}

	public void setFileThreadRatio(int fileThreadRatio) {
		this.fileThreadRatio = fileThreadRatio;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setFileTypes(Collection<String> fileTypes) {
		this.fileTypes = new HashSet<>(fileTypes);
	}

	private Set<String> getFileTypes() {
		return fileTypes;
	}

	public FileFilter getInterestingFileFilter() {
		return pathname -> {
            String extension = Tools.getFileExtension(pathname);
            return getFileTypes().contains(extension);
        };
	}

}
