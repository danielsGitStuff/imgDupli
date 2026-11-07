package io;

import interfaces.IFileRepresentation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class HashRelatedFileStorage<T extends IFileRepresentation> {
    private final Map<String, HashSet<T>> hashLeFileMap = new HashMap<>();

    public void put(String hash, T file) {
        if (hashLeFileMap.containsKey(hash)) {
            hashLeFileMap.get(hash).add(file);
        } else {
            HashSet<T> HashSet = new HashSet<>();
            HashSet.add(file);
            hashLeFileMap.put(hash, HashSet);
        }
    }

    public HashSet<T> getSingles() {
        HashSet<T> result = new HashSet<>();
        hashLeFileMap.forEach((hash, files) -> {
            if (files.size() < 2)
                result.addAll(files);
        });
        return result;
    }

    public HashSet<T> get(String hash) {
        if (hashLeFileMap.containsKey(hash)) {
            HashSet<T> result = hashLeFileMap.get(hash);
            if (result != null)
                return (HashSet<T>) result.clone();
        }
        return null;
    }

    public boolean atLeastTwoCopies(String hash) {
        HashSet<T> result = get(hash);
        return result != null && result.size() > 1;
    }

    public void removeFile(T leFile) {
        HashSet<T> copies = hashLeFileMap.get(leFile.getHash());
        if (copies != null) {
            copies.remove(leFile);
            if (copies.size() < 2) {
                copies.forEach(IFileRepresentation::hide);
                hashLeFileMap.remove(leFile.getHash());
            }
        }
    }
}
