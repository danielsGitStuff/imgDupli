package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import data.ConcurrentResultMap;
import data.Directory;
import data.DuplicationStructureBuilder;
import data.LeFile;
import image.ImagefolderPanelPresenter;
import interfaces.IGuiController;
import interfaces.IGuiEventHandler;
import io.FileSystem;
import main.SearchController;
import main.Settings;

public class GuiController implements IGuiController, IGuiEventHandler {

    private final GuiPresenter presenter;
    private SearchController searchController;
    private DuplicationStructureBuilder structureBuilder;
    private final ImagefolderPanelPresenter imagefolderPanelPresenter;

    public GuiController() {
        this.presenter = new GuiPresenter(this);
        imagefolderPanelPresenter = presenter.getImagefolderPanelPresenter();
    }

    public void setSearchController(SearchController searchController) {
        this.searchController = searchController;
    }

    public void show() {
        presenter.show();
    }


    @Override
    public void setResultMap(ConcurrentResultMap result) {

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
        HashSet<LeFile> relatedFiles = structureBuilder.getHashRelatedFileStorage().get(file.getHash());
        relatedFiles.forEach(this::highlightFile);
    }

    void handleDirSelection(Directory directory) {
        structureBuilder.resetHighlighted();
        imagefolderPanelPresenter.reset();
        List<LeFile> duplicates = new ArrayList<>();
        HashSet<String> hashes = directory.getHashes();
        hashes.forEach(hash -> {
            HashSet<LeFile> files = structureBuilder.getHashRelatedFileStorage().get(hash);
            if (files != null && files.size() > 0) {
                LeFile f = files.iterator().next();
                files.forEach(this::highlightFile);
                duplicates.add(f);
            }
        });
        imagefolderPanelPresenter.setDuplicates(duplicates);
        FileSystem fileSystem = directory.getFileSystem();
        File[] images = fileSystem.getFile().listFiles(searchController.getSettings().getInterestingFileFilter());
        imagefolderPanelPresenter.setFolderImages(images);
    }

    @Override
    public void onAllInterestingFilesFound(int sum) {

    }

    @Override
    public void onCompleted(DuplicationStructureBuilder structureBuilder) {
        this.structureBuilder = structureBuilder;
        structureBuilder.backup();
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
        structureBuilder.restore();
        presenter.geraffel(structureBuilder);
        presenter.disableWrite();
    }

    @Override
    public void onBtnWriteClicked() {
        structureBuilder.getRoot().write();
        structureBuilder.rebuild();
        presenter.geraffel(structureBuilder);
        presenter.disableWrite();
    }

    @Override
    public void onFileDeleteFilesEverywhereElse(LeFile leFile) {
        leFile.getCustomFile().setMarkedForDeletion(true);
        HashSet<LeFile> copies = structureBuilder.getHashRelatedFileStorage().get(leFile.getHash());
        copies.forEach(f -> {
            f.hide();
            structureBuilder.getHashRelatedFileStorage().removeFile(f);
            if (f != leFile) {
                f.getCustomFile().setMarkedForDeletion(true);
            }
        });
        presenter.geraffel(structureBuilder);
        presenter.enableWrite();
    }

    @Override
    public void onFileDeleteFile(LeFile leFile) {
        leFile.getCustomFile().setMarkedForDeletion(true);
        leFile.hide();
        structureBuilder.getHashRelatedFileStorage().removeFile(leFile);
        presenter.geraffel(structureBuilder);
        presenter.enableWrite();
    }

    @Override
    public void onDirDeleteFilesEverywhereElse(Directory directory) {
        directory.getHashes().forEach(hash -> {
            HashSet<LeFile> hashSet = structureBuilder.getHashRelatedFileStorage().get(hash);
            hashSet.stream().filter(file -> file.getDirectory() != directory).forEach(file -> {
                file.getCustomFile().setMarkedForDeletion(true);
                file.hide();
                structureBuilder.getHashRelatedFileStorage().removeFile(file);
                System.out.println(
                        "GuiController.onDirDeleteFilesEverywhereElse(" + file.getCustomFile().getAbsolutePath() + ")");
            });
            ;
        });
       // presenter.geraffel(structureBuilder);
        presenter.geruffel();
        presenter.enableWrite();
    }

    @Override
    public void onDirDeleteFiles(Directory directory) {
        directory.getHashes().forEach(hash -> {
            HashSet<LeFile> hashSet = structureBuilder.getHashRelatedFileStorage().get(hash);
            hashSet.stream().filter(file -> file.getDirectory() == directory).forEach(file -> {
                file.getCustomFile().setMarkedForDeletion(true);
                file.hide();
                structureBuilder.getHashRelatedFileStorage().removeFile(file);
                System.out.println("GuiController.onDirDeleteFiles(" + file.getCustomFile().getAbsolutePath() + ")");
            });
            ;
        });
       // presenter.geraffel(structureBuilder);
        presenter.geruffel();
        presenter.enableWrite();
    }

}
