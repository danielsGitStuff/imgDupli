package data;

import io.CustomFile;
import io.FSRelated;

public class LeFile extends FSRelated {
	private CustomFile customFile;
	private Directory directory;


	public LeFile(CustomFile customFile) {
		this.customFile = customFile;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}

	public Directory getDirectory() {
		return directory;
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

}
