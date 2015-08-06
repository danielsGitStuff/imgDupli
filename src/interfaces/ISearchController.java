package interfaces;

import filefinder.FileFinder;

import java.io.File;

/**
 * Created by xor on 8/4/15.
 */
public interface ISearchController {
    void onFileHashed();

    void onHashingDone(FileFinder fileFinder);

    void onSearchAborted();

    void onInterestingFileFound(File file);

    void onInterestingFilesFoundDone();
}
