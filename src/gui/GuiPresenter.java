package gui;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import data.Directory;
import data.DuplicationStructureBuilder;
import data.LeFile;
import image.ImagePanel;
import image.ImagePanelController;
import image.ImagefolderPanelPresenter;
import main.Settings;

public class GuiPresenter {

    private final GuiController controller;
    private final Gui gui;
    private ListSelectionListener listSelectionListener;
    private final ImagefolderPanelPresenter imagefolderPanelPresenter;
    private final ImagePanelController imagePanelController = new ImagePanelController();

    public GuiPresenter(GuiController controller) {
        this.controller = controller;
        this.gui = new Gui(controller);
        gui.setTreeSelectionListener((e) -> this.handleTreeSelection(e));
        this.imagefolderPanelPresenter = new ImagefolderPanelPresenter(this.gui, controller);
    }

    public void showProgressStuff() {
        gui.showProgressStuff();
    }

    public void show() {
        gui.show();
    }

    public ImagefolderPanelPresenter getImagefolderPanelPresenter() {
        return imagefolderPanelPresenter;
    }

    public void geraffel(DuplicationStructureBuilder structureBuilder) {
        OTreeNode rootNode = new OTreeNode(structureBuilder.getRootDirectory());
        Directory rootDirectory = structureBuilder.getRootDirectory();
        fillNodes(rootDirectory, rootNode);
        fillNodesFiles(rootDirectory, rootNode);

        TreeModel treeModel = new DefaultTreeModel(rootNode);
        gui.setTreeModel(treeModel);
    }

    public void geruffel() {
        System.out.println("GuiPresenter.geruffel!!!");
        OTreeNode rootNode = (OTreeNode) gui.getTree().getModel().getRoot();
        rootNode.check();
        gui.getTree().updateUI();
    }

    private void fillNodes(Directory directory, OTreeNode node) {
        directory.getSubDirectories().forEach(dir -> {
            OTreeNode newNode = new OTreeNode(dir);
            node.add(newNode);
            fillNodesFiles(dir, newNode);
            fillNodes(dir, newNode);
        });
    }

    private void fillNodesFiles(Directory directory, OTreeNode node) {
        directory.getLeFiles().forEach(leFile -> {
            OTreeNode newNode = new OTreeNode(leFile);
            node.add(newNode);
        });
    }

    private void handleTreeSelection(TreeSelectionEvent e) {
        if (e.getNewLeadSelectionPath() == null || e.getNewLeadSelectionPath().getLastPathComponent() == null) {
            return;
        }
        OTreeNode node = (OTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
        Object userObject = node.getUserObject();
        if (userObject instanceof Directory) {
            controller.handleDirSelection((Directory) userObject);
        } else if (userObject instanceof LeFile) {
            controller.handleFileSelection((LeFile) userObject);
        }
    }

    public void showSingleImage(LeFile file) {
        JSplitPane spTreeImages = gui.getSpTreeImages();
        JSplitPane spImages = gui.getSpImages();
        if (spTreeImages.isAncestorOf(spImages)) {
            spTreeImages.remove(spImages);
            ImagePanel imagePanel = imagePanelController.createImagePanel(file.getCustomFile());
            spTreeImages.add(imagePanel);
        }
    }

    public void hideProgressStuff() {
        gui.hideProgressStuff();
    }

    public Settings getSettings() {
        return gui.getProgSettings();
    }

    public void enableWrite() {
        gui.enableWrite();
        gui.enableRevert();
    }

    public void disableWrite() {
        gui.disableWrite();
        gui.disableRevert();
    }

    public void showFoundFilesCount(int foundFilesCount) {
        gui.getProgressBar().setMaximum(foundFilesCount);
        gui.getProgressBar().repaint();
        gui.setWindowTitle(foundFilesCount + " files found");
    }

    public void showFilesProcessed(int filesHashedCount) {
        gui.getProgressBar().setValue(filesHashedCount);
        gui.getProgressBar().repaint();
    }
}
