package interfaces;

import data.Directory;
import data.LeFile;

public interface IGuiEventHandler {
	void onBtnStartClicked();
	void onBtnStopClicked();
	void onBtnRevertClicked();
	void onBtnWriteClicked();
	void onBtnToggleMenuClicked();
	void onFileDeleteFilesEverywhereElse(LeFile leFile);
	void onFileDeleteFile(LeFile leFile);
	void onDirDeleteFilesEverywhereElse(Directory directory);
	void onDirDeleteFiles(Directory directory);
	
}
