package filefinder;

import data.ConcurrentResultMap;
import hash.HashRunnable;
import interfaces.ISearchController;
import main.Settings;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FileFinderRunnable implements Runnable {

    private final ISearchController searchListener;
    private final FileFilter interestingFilesFilter;
    private final FileFilter directoryFileFilter;
    private final FileFinder finder;
    private final Settings settings;
    private final ConcurrentResultMap resultMap;
    private List<File> collected;
    private final Map<Long, List<File>> size2files = new HashMap<>();

    public FileFinderRunnable(FileFinder finder, ISearchController searchListener) {
        this.finder = finder;
        this.settings = finder.getSettings();
        this.resultMap = finder.getResultMap();
        this.searchListener = searchListener;
        this.collected = new ArrayList<>(settings.getFileThreadRatio());
        this.interestingFilesFilter = settings.getInterestingFileFilter();
        directoryFileFilter = File::isDirectory;
    }

    @Override
    public void run() {
        File root = new File(settings.getRootPath());
        roamDirectory(root);
        if (!collected.isEmpty()) {
            createHashRunnable(collected);
        }
        finder.done();
    }

    private void createHashRunnable(List<File> files) {
        HashRunnable hashRunnable = new HashRunnable(files, resultMap);
        finder.addRunnable(hashRunnable);
    }

    private void roamDirectory(File directory) {
        // get files
        File[] fileList = directory.listFiles(interestingFilesFilter);
        if (fileList != null) { //might not be accessible
            for (File file : fileList) {
                // add and launch md5 worker if necessary
                if (Thread.currentThread().isInterrupted())
                    return;
                addChecked(file);
            }
        }
        // roam other directories
        File[] dirs = directory.listFiles(directoryFileFilter);
        if (dirs != null) {
            for (File dir : dirs) {
                if (Thread.currentThread().isInterrupted())
                    return;
                roamDirectory(dir);
            }
        }
    }

    private void addChecked(File file) {
        List<File> sameSizeFiles = this.size2files.computeIfAbsent(file.length(), size -> new ArrayList<>());
        sameSizeFiles.add(file);
        if (sameSizeFiles.size() == 1)
            return;
        if (sameSizeFiles.size() == 2) {
            collected.add(sameSizeFiles.get(0));
            searchListener.onInterestingFileFound(sameSizeFiles.get(0));
        }
        collected.add(file);
        searchListener.onInterestingFileFound(file);

        if (collected.size() >= settings.getFileThreadRatio()) {
            createHashRunnable(collected);
            collected = new ArrayList<>(settings.getFileThreadRatio());
        }
    }
}
