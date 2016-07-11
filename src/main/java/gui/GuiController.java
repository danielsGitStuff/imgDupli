package gui;

import data.Directory;
import data.DuplicationStructureBuilder;
import data.LeFile;
import interfaces.IDuplicateSearchListener;
import interfaces.IGuiEventHandler;
import io.FsDirectory;
import main.SearchController;
import main.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GuiController implements IDuplicateSearchListener, IGuiEventHandler {

    private final GuiPresenter presenter;
    private SearchController searchController;
    private DuplicationStructureBuilder structureBuilder;

    public GuiController() {
        this.presenter = new GuiPresenter(this);
    }

    public void setSearchController(SearchController searchController) {
        this.searchController = searchController;
    }

    public void show() {
        presenter.show();
    }



    private void highlightFile(LeFile f) {
        f.highlight();
        Directory directory = f.getDirectory();
        while (directory != null && directory != structureBuilder.getRootDirectory()) {
            directory.highlight();
            directory = directory.getParentDirectory();
        }
    }

    void handleFileSelection(LeFile file) {
        structureBuilder.resetHighlighted();
        HashSet<LeFile> relatedFiles = structureBuilder.getHashRelatedLeFiles(file.getHash());
        relatedFiles.forEach(this::highlightFile);
        presenter.showSingleImage(file);
    }

    void handleDirSelection(Directory directory) {
        structureBuilder.resetHighlighted();
        List<LeFile> duplicates = new ArrayList<>();
        HashSet<String> hashes = directory.getHashes();
        hashes.forEach(hash -> {
            HashSet<LeFile> files = structureBuilder.getHashRelatedLeFiles(hash);
            if (files != null && files.size() > 0) {
                LeFile f = files.iterator().next();
                files.forEach(this::highlightFile);
                duplicates.add(f);
            }
        });
        FsDirectory fsDirectory = directory.getFsDirectory();
        File[] images = fsDirectory.getFile().listFiles(searchController.getSettings().getInterestingFileFilter());
        presenter.showFolder(duplicates, images);
    }

    @Override
    public void onAllInterestingFilesFound(int sum) {

    }

    @Override
    public void onCompleted(DuplicationStructureBuilder structureBuilder) {
        this.structureBuilder = structureBuilder;
        presenter.geraffel(structureBuilder);
        presenter.hideProgressStuff();
    }

    @Override
    public void onSearchAborted() {
        presenter.hideProgressStuff();

    }

    @Override
    public void onFileHashed(int count) {
        presenter.showFilesProcessed(count);
    }

    @Override
    public void onInterestingFileFound(int interestingFilesCount) {
        presenter.showFoundFilesCount(interestingFilesCount);
    }

    @Override
    public void onBtnStartClicked() {
        Settings settings = presenter.getSettings();
        searchController.search(settings);
        presenter.showProgressStuff();
    }

    @Override
    public void onBtnStopClicked() {
        searchController.stop();
    }

    @Override
    public void onBtnRevertClicked() {
        structureBuilder.rebuildDirectoryGraph();
        presenter.geraffel(structureBuilder);
        presenter.disableWrite();
        presenter.disableRevert();
    }

    @Override
    public void onBtnWriteClicked() {
        structureBuilder.write();
        structureBuilder.getRootDirectory().cleanGraph();
        presenter.geruffel();
        presenter.disableWrite();
    }


    @Override
    public void onFileDeleteFilesEverywhereElse(LeFile leFile) {
        HashSet<LeFile> copies = structureBuilder.getHashRelatedLeFiles(leFile.getHash());
        copies.forEach(f -> {
            f.hide();
            structureBuilder.removeLeFile(f);
            if (f != leFile) {
                f.getFsFile().setMarkedForDeletion(true);
            }
        });
        presenter.geruffel();
        presenter.enableWrite();
        presenter.enableRevert();
    }

    @Override
    public void onFileDeleteFile(LeFile leFile) {
        leFile.getFsFile().setMarkedForDeletion(true);
        leFile.hide();
        structureBuilder.removeLeFile(leFile);
        presenter.geruffel();
        presenter.enableWrite();
        presenter.enableRevert();
    }

    @Override
    public void onDirDeleteFilesEverywhereElse(Directory directory) {
        directory.getHashes().forEach(hash -> {
            HashSet<LeFile> hashSet = structureBuilder.getHashRelatedLeFiles(hash);
            hashSet.stream().filter(file -> file.getDirectory() != directory).forEach(file -> {
                file.getFsFile().setMarkedForDeletion(true);
                file.hide();
                structureBuilder.removeLeFile(file);
                System.out.println(
                        "GuiController.onDirDeleteFilesEverywhereElse(" + file.getFsFile().getAbsolutePath() + ")");
            });
        });
        presenter.geruffel();
        presenter.enableWrite();
        presenter.enableRevert();
    }

    @Override
    public void onDirDeleteFiles(Directory directory) {
        directory.getHashes().forEach(hash -> {
            HashSet<LeFile> hashSet = structureBuilder.getHashRelatedLeFiles(hash);
            hashSet.stream().filter(file -> file.getDirectory() == directory).forEach(file -> {
                //important: last copy might be in here
                if (structureBuilder.atLeastTwoLeFiles(file.getHash())) {
                    file.getFsFile().setMarkedForDeletion(true);
                    file.hide();
                    structureBuilder.removeLeFile(file);
                    System.out.println("GuiController.onDirDeleteFiles(" + file.getFsFile().getAbsolutePath() + ")");
                }
            });
        });
        presenter.geruffel();
        presenter.enableWrite();
        presenter.enableRevert();
    }

	@Override
	public void onBtnToggleMenuClicked() {
		presenter.toggleMenu();
	}

}
