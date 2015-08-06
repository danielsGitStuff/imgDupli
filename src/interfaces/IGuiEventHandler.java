package interfaces;

import data.Directory;
import data.LeFile;
import io.CustomFile;
import io.FileSystem;

public interface IGuiEventHandler {
	void onBtnStartClicked();
	void onBtnStopClicked();
	void onBtnRevertClicked();
	void onBtnWriteClicked();
	void onFileDeleteFilesEverywhereElse(LeFile leFile);
	void onFileDeleteFile(LeFile leFile);
	void onDirDeleteFilesEverywhereElse(Directory directory);
	void onDirDeleteFiles(Directory directory);
	
}
