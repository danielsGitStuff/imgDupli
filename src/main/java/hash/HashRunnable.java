package hash;

import java.io.File;
import java.util.List;

import data.ConcurrentResultMap;
import tools.Hash;

public class HashRunnable implements Runnable {
    private final ConcurrentResultMap resultMap;
    private final List<File> files;

    public HashRunnable(List<File> files, ConcurrentResultMap resultMap) {
        this.files = files;
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        for (File file : files) {
            try {
                String hash = Hash.md5(file);
                resultMap.put(hash, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("hashrunnable.finish :) " + files.size());
    }

}
