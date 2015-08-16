package interfaces;

import data.DuplicationStructureBuilder;

public interface IDuplicateSearchListener {

    void onAllInterestingFilesFound(int sum);

    void onCompleted(DuplicationStructureBuilder structureBuilder);

    void onSearchAborted();

    void onFileHashed(int count);

    void onInterestingFileFound(int interestingFilesCount);

}
