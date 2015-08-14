package data;

import interfaces.IFileRepresentation;
import io.CustomFile;
import io.FSRelated;

public class LeFile extends FSRelated implements IFileRepresentation {
	private CustomFile customFile;
	private Directory directory;


	public LeFile(CustomFile customFile) {
		this.customFile = customFile;
	}

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}

	public String getHash() {
		return customFile.getHash();
	}

	public void resetFlags() {
		highlighted = false;
		hidden = false;
	}

	@Override
	public String toString() {
		return customFile.toString();
	}

	public CustomFile getCustomFile() {
		return customFile;
	}
	
	@Override
	public void hide() {
		super.hide();
		directory.removeLeFile(this);
	}

	public void cleanGraph() {
		if (!customFile.exists() || customFile.isMarkedForDeletion()) {
			hide();
		}
	}

}
