package data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import interfaces.ISearchController;

public class ConcurrentResultMap extends ConcurrentHashMap<String, File> {
    private final ISearchController searchController;
    private final Map<String, List<File>> hashCollisions = new HashMap<>();

    public ConcurrentResultMap(ISearchController searchController) {
        this.searchController = searchController;
    }

    public ConcurrentResultMap(ISearchController hashEventListener, int size) {
        super(size);
        this.searchController = hashEventListener;
    }

	@Override
	public synchronized File put(String key, File value) {
        searchController.onFileHashed();
        if (containsKey(key) || hashCollisions.containsKey(key)) {
			if (hashCollisions.containsKey(key)) {
				hashCollisions.get(key).add(value);
			} else {
				List<File> list = new ArrayList<>();
				hashCollisions.put(key, list);
				list.add(get(key));
				list.add(value);
			}
			remove(key);
            //	if (searchController != null) {
            //		searchController.onFoundDuplicate(key, value);
            //	}
        }
		return super.put(key, value);
	}

	public List<String> getDuplicateKeys() {
		return hashCollisions.keySet().stream().collect(Collectors.toList());
	}

	public List<File> getDuplicates(String key) {
		return hashCollisions.get(key);
	}

	public Map<String, List<File>> getHashCollisions() {
		return hashCollisions;
	}

}
