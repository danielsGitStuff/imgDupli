package io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import data.LeFile;

public class HashRelatedFileStorage {
    private Map<String, HashSet<LeFile>> hashFileMap = new HashMap<>();
    private Map<String, HashSet<LeFile>> backup = new HashMap<>();

    public void put(String hash, LeFile file) {
        if (hashFileMap.containsKey(hash)) {
            hashFileMap.get(hash).add(file);
        } else {
            HashSet<LeFile> HashSet = new HashSet<>();
            HashSet.add(file);
            hashFileMap.put(hash, HashSet);
        }
    }

    public HashSet<LeFile> getSingles() {
        HashSet<LeFile> result = new HashSet<>();
        hashFileMap.forEach((hash, files) -> {
            if (files.size() < 2)
                result.addAll(files);
        });
        return result;
    }

    public HashSet<LeFile> get(String hash) {
        if (hashFileMap.containsKey(hash)) {
            HashSet<LeFile> result = (HashSet<LeFile>) hashFileMap.get(hash);
            if (result != null)
                return (HashSet<LeFile>) result.clone();
        }
        return null;
    }

    public void backup() {
        backup = new HashMap<>();
        hashFileMap.forEach((hash, hashset) -> {
            backup.put(hash, new HashSet<>(hashset));
        });

    }

    public void restore() {
        hashFileMap = backup;
        backup = null;
    }

    public void removeFile(LeFile leFile) {
        HashSet<LeFile> copies = hashFileMap.get(leFile.getHash());
        if (copies != null) {
            copies.remove(leFile);
            if (copies.size() < 2) {
                copies.forEach(f -> f.hide());
                hashFileMap.remove(leFile.getHash());
            }
        }
    }
}
