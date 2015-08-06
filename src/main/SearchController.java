package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.DuplicationStructureBuilder;
import filefinder.FileFinder;
import interfaces.IDuplicateSearchListener;
import interfaces.ISearchController;
import io.FileSystem;

public class SearchController implements ISearchController {
    private Settings settings;
    private final List<IDuplicateSearchListener> searchListeners = new ArrayList<>();
    private FileFinder fileFinder;

    public SearchController() {
    }

    public void search(Settings settings) {
        this.settings = settings;
        interestingFilesCount = 0;
        hashedFilesCount = 0;
        fileFinder = new FileFinder(settings, this);
        fileFinder.start();
    }

    public void addSearchListener(IDuplicateSearchListener listener) {
        searchListeners.add(listener);
    }

    private int interestingFilesCount = 0;
    private int hashedFilesCount = 0;

    @Override
    public void onHashingDone(FileFinder fileFinder) {
        FileSystem root = new FileSystem(settings.getRootPath());
        DuplicationStructureBuilder structureBuilder = new DuplicationStructureBuilder(root,
                fileFinder.getResultMap().getHashCollisions());
        searchListeners.forEach(l -> l.onCompleted(structureBuilder));
    }

    @Override
    public void onInterestingFileFound(File file) {
        this.interestingFilesCount++;
        searchListeners.forEach(l -> l.onInterestingFileFound(interestingFilesCount));
    }

    @Override
    public void onInterestingFilesFoundDone() {
        searchListeners.forEach(l -> l.onAllInterestingFilesFound(interestingFilesCount));
    }

    public Settings getSettings() {
        return settings;
    }

    public void stop() {
        fileFinder.stop();
    }

    @Override
    public void onSearchAborted() {
        searchListeners.forEach(l -> l.onSearchAborted());
    }

    @Override
    public void onFileHashed() {
        this.hashedFilesCount++;
        searchListeners.forEach(l -> l.onFileHashed(hashedFilesCount));
    }

}
