package data;

import interfaces.IFileRepresentation;
import io.FsFile;
import io.FsRelated;

public class LeFile extends FsRelated implements IFileRepresentation {
	private FsFile fsFile;
	private Directory directory;


	public LeFile(FsFile fsFile) {
		this.fsFile = fsFile;
	}

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}

	public String getHash() {
		return fsFile.getHash();
	}

	public void resetFlags() {
		highlighted = false;
		hidden = false;
	}

	@Override
	public String toString() {
		return fsFile.toString();
	}

	public FsFile getFsFile() {
		return fsFile;
	}
	
	@Override
	public void hide() {
		super.hide();
		directory.removeLeFile(this);
	}

	public void cleanGraph() {
		if (!fsFile.exists() || fsFile.isMarkedForDeletion()) {
			hide();
		}
	}

}
