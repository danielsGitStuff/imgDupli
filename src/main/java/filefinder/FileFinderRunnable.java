package filefinder;

import data.ConcurrentResultMap;
import hash.HashRunnable;
import interfaces.ISearchController;
import main.Settings;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

class FileFinderRunnable implements Runnable {

    private final ISearchController searchListener;
    private final FileFilter interestingFilesFilter;
    private final FileFilter directoryFileFilter;
    private final FileFinder finder;
    private final Settings settings;
    private final ConcurrentResultMap resultMap;
    private List<File> collected;

    public FileFinderRunnable(FileFinder finder, ISearchController searchListener) {
        this.finder = finder;
        this.settings = finder.getSettings();
        this.resultMap = finder.getResultMap();
        this.searchListener = searchListener;
        this.collected = new ArrayList<>(settings.getFileThreadRatio());
        this.interestingFilesFilter = settings.getInterestingFileFilter();
        directoryFileFilter = pathname -> pathname.isDirectory();
    }

    @Override
    public void run() {
        File root = new File(settings.getRootPath());
        roamDirectory(root);
        if (collected.size() > 0) {
            createHashRunnable(collected);
        }
        finder.done();
    }

    private void createHashRunnable(List<File> files) {
        HashRunnable hashRunnable = new HashRunnable(files, resultMap);
        finder.submit(hashRunnable);
    }

    private void roamDirectory(File directory) {
        // get files
        File[] fileList = directory.listFiles(interestingFilesFilter);
        if (fileList != null) { //might not be accessible
            for (File file : fileList) {
                searchListener.onInterestingFileFound(file);
                // add and launch md5 worker if necessary
                addChecked(file);
            }
        }
        // roam other directories
        File[] dirs = directory.listFiles(directoryFileFilter);
        if (dirs != null) {
            for (File dir : dirs) {
                roamDirectory(dir);
            }
        }
    }

    private void addChecked(File file) {
        collected.add(file);
        if (collected.size() == settings.getFileThreadRatio()) {
            createHashRunnable(collected);
            collected = new ArrayList<>(settings.getFileThreadRatio());
        }
    }
}
