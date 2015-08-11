import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import interfaces.ISearchController;
import org.junit.Before;
import org.junit.Test;

import data.ConcurrentResultMap;
import data.DuplicationStructureBuilder;
import filefinder.FileFinder;
import interfaces.IDuplicateSearchListener;

public class TestConcurrentResultMap implements ISearchController {

    private ConcurrentResultMap map;

    @Before
    public void init() {
        map = new ConcurrentResultMap(this);
    }

    @Test
    public void testDuplicates() {
        String originalKey = new String("blubb");
        File originalFile = new File("");
        String duplicateKey = new String("blubb");
        File duplicateFile = new File("");
        map.put(originalKey, originalFile);
        map.put(duplicateKey, duplicateFile);
        List<String> result = map.getDuplicateKeys();
        if (result.size() != 1) {
            fail("size does not match");
        }
        List<File> files = map.getDuplicates(originalKey);
        assertSame("did not store all files", 2, files.size());
        assertSame(originalFile, files.get(0));
        assertSame(duplicateFile, files.get(1));
    }

    @Override
    public void onFileHashed() {

    }

    @Override
    public void onHashingDone(FileFinder fileFinder) {

    }

    @Override
    public void onSearchAborted() {

    }

    @Override
    public void onInterestingFileFound(File file) {

    }

    @Override
    public void onInterestingFilesFoundDone() {

    }
}
