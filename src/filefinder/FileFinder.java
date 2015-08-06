package filefinder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import data.ConcurrentResultMap;
import hash.HashRunnable;
import interfaces.ISearchController;
import main.Settings;

public class FileFinder {

    private final Settings settings;
    private final ISearchController searchController;
    private ExecutorService finderExecutor;
    private ExecutorService hashExecutor;
    private final ConcurrentResultMap resultMap;
    private List<HashRunnable> hashRunnables;


    public FileFinder(Settings settings, ISearchController searchController) {
        this.settings = settings;
        this.searchController = searchController;
        this.resultMap = new ConcurrentResultMap(searchController, 12000);
    }

    private ExecutorService adjustExecutor(ExecutorService executor) {
        if (executor != null && (!executor.isShutdown() || !executor.isTerminated())) {
            executor.shutdownNow();
            executor = null;
        }
        return executor;
    }

    public void start() {
        hashRunnables = new ArrayList<>();
        finderExecutor = adjustExecutor(finderExecutor);
        hashExecutor = adjustExecutor(hashExecutor);

        if (finderExecutor == null) {
            finderExecutor = Executors.newSingleThreadExecutor();
        }
        if (hashExecutor == null) {
            hashExecutor = Executors.newWorkStealingPool();
        }
        FileFinderRunnable fileFinderRunnable = new FileFinderRunnable(this, searchController);
        finderExecutor.submit(fileFinderRunnable);
    }

    public void done() {
        hashRunnables.forEach(r -> hashExecutor.submit(r));
        hashExecutor.shutdown();
        searchController.onInterestingFilesFoundDone();
        System.out.println("done searching for files");
        System.out.println("waiting for hash thing to come to an end...");
        try {
            hashExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("hash things done");
        searchController.onHashingDone(this);
    }

    public Settings getSettings() {
        return settings;
    }

    public ConcurrentResultMap getResultMap() {
        return resultMap;
    }

    public void submit(HashRunnable hashRunnable) {
        hashRunnables.add(hashRunnable);
    }

    public void stop() {
        finderExecutor.shutdownNow();
        hashExecutor.shutdownNow();
        searchController.onSearchAborted();
    }

}
