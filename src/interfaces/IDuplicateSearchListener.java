package interfaces;

import java.io.File;

import data.ConcurrentResultMap;
import data.DuplicationStructureBuilder;
import filefinder.FileFinder;

public interface IDuplicateSearchListener {

    void onAllInterestingFilesFound(int sum);

    void onCompleted(DuplicationStructureBuilder structureBuilder);

    void onSearchAborted();

    void onFileHashed(int count);

    void onInterestingFileFound(int interestingFilesCount);

//	void onFileFound(File file);
//
//	void onFileSearchDone();
//
//	void onFoundDuplicate(String hash, File file);
//
//	void onFindingDuplicatesDone(FileFinder fileFinder);
//
//	void onSearchDone(DuplicationStructureBuilder structureBuilder);
//
//	void onSearchAborted();
//
//	void onFileHashed();
}
