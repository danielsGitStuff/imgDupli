package filefinder;

import interfaces.ISearchController;
import main.Settings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests the size pre-filter in {@link FileFinderRunnable#addChecked}: only files sharing a size with
 * at least one other file may be hashed, and every such file must be hashed exactly once.
 */
public class FileFinderRunnableTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void identicalPairIsFound() throws Exception {
        File a = write("a/x.jpg", content(5000, 1));
        File b = write("b/x.jpg", content(5000, 1));

        Result r = search();

        assertEquals(1, r.collisions.size());
        assertEquals(paths(a, b), paths(r.collisions.values().iterator().next()));
    }

    @Test
    public void identicalTripleListsEveryFileExactlyOnce() throws Exception {
        File a = write("a/y.jpg", content(7000, 2));
        File b = write("b/y.jpg", content(7000, 2));
        File c = write("b/y2.jpg", content(7000, 2));

        Result r = search();

        assertEquals(1, r.collisions.size());
        List<File> group = r.collisions.values().iterator().next();
        assertEquals("a file appears more than once in its duplicate group", 3, group.size());
        assertEquals(paths(a, b, c), paths(group));
    }

    @Test
    public void sameSizeButDifferentContentIsNotADuplicate() throws Exception {
        write("c/z1.jpg", content(9000, 3));
        write("c/z2.jpg", content(9000, 4));
        write("c/z3.jpg", content(9000, 5));

        Result r = search();

        assertTrue("unique files reported as duplicates: " + r.collisions.values(), r.collisions.isEmpty());
    }

    @Test
    public void uniqueSizesAreNotHashed() throws Exception {
        write("u1.jpg", content(100, 1));
        write("u2.jpg", content(200, 1));
        write("u3.jpg", content(300, 1));

        Result r = search();

        assertEquals(0, r.hashed);
        assertTrue(r.collisions.isEmpty());
    }

    @Test
    public void everyCandidateIsHashedOnceAndCountedForProgress() throws Exception {
        write("a/x.jpg", content(5000, 1));   // size group of 2
        write("b/x.jpg", content(5000, 1));
        write("a/y.jpg", content(7000, 2));   // size group of 3
        write("b/y.jpg", content(7000, 2));
        write("b/y2.jpg", content(7000, 2));
        write("unique.jpg", content(1234, 9)); // unique size -> skipped

        Result r = search();

        assertEquals("files hashed", 5, r.hashed);
        assertEquals("progress maximum must match hashed files", r.hashed, r.found);
    }

    @Test
    public void groupsSpanningSeveralBatchesAreComplete() throws Exception {
        // batch size 2 forces the size group to be split across HashRunnables
        List<File> copies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            copies.add(write("d" + i + "/p.jpg", content(4000, 7)));
        }

        Result r = search(2);

        assertEquals(1, r.collisions.size());
        assertEquals(paths(copies.toArray(new File[0])), paths(r.collisions.values().iterator().next()));
        assertEquals(5, r.hashed);
    }

    // ---------------------------------------------------------------- helpers

    private static final class Result {
        Map<String, List<File>> collisions;
        int hashed;
        int found;
    }

    private Result search() throws InterruptedException {
        return search(200);
    }

    private Result search(int fileThreadRatio) throws InterruptedException {
        Settings settings = new Settings();
        settings.setRootPath(tmp.getRoot().getAbsolutePath());
        settings.setFileTypes(List.of("jpg"));
        settings.setFileThreadRatio(fileThreadRatio);

        CountDownLatch done = new CountDownLatch(1);
        AtomicInteger hashed = new AtomicInteger();
        AtomicInteger found = new AtomicInteger();
        Result result = new Result();

        ISearchController listener = new ISearchController() {
            @Override
            public void onFileHashed() {
                hashed.incrementAndGet();
            }

            @Override
            public void onHashingDone(FileFinder fileFinder) {
                result.collisions = new HashMap<>(fileFinder.getResultMap().getHashCollisions());
                done.countDown();
            }

            @Override
            public void onSearchAborted() {
            }

            @Override
            public void onInterestingFileFound(File file) {
                found.incrementAndGet();
            }

            @Override
            public void onInterestingFilesFoundDone() {
            }
        };

        new FileFinder(settings, listener).start();
        assertTrue("search did not finish within 10s", done.await(10, TimeUnit.SECONDS));
        result.hashed = hashed.get();
        result.found = found.get();
        return result;
    }

    private File write(String relativePath, byte[] data) throws IOException {
        File file = new File(tmp.getRoot(), relativePath);
        Files.createDirectories(file.getParentFile().toPath());
        Files.write(file.toPath(), data);
        return file;
    }

    /** deterministic content; same seed -> same bytes, different seed -> different bytes */
    private static byte[] content(int size, long seed) {
        byte[] data = new byte[size];
        new Random(seed).nextBytes(data);
        return data;
    }

    /** sorted list so duplicates inside a group are kept and show up in the assertion */
    private static List<String> paths(Collection<File> files) {
        return files.stream().map(File::getAbsolutePath).sorted().collect(Collectors.toList());
    }

    private static List<String> paths(File... files) {
        return paths(Arrays.asList(files));
    }
}
