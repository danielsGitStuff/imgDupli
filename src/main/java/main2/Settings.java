package main;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import tools.Tools;

public class Settings {

	private int fileThreadRatio;
	private String rootPath;
	private List<String> fileTypes;

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

	public void setFileTypes(List<String> fileTypes) {
		this.fileTypes = fileTypes;
	}

	private List<String> getFileTypes() {
		return fileTypes;
	}

	public FileFilter getInterestingFileFilter() {
		return pathname -> {
            String extension = Tools.getFileExtension(pathname);
            return getFileTypes().contains(extension);
        };
	}

}
