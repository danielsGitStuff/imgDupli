package gui;

import data.Directory;
import data.DuplicationStructureBuilder;
import data.LeFile;
import image.ImagePanel;
import image.ImagePanelController;
import image.ImagefolderPanelPresenter;
import main.Settings;
import tree.OTreeNode;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class GuiPresenter {

	private final GuiController controller;
	private final Gui gui;
	private final ImagefolderPanelPresenter imagefolderPanelPresenter;
	private final ImagePanelController imagePanelController = new ImagePanelController();
	private ListSelectionListener listSelectionListener;
	private boolean menuShown;

	public GuiPresenter(GuiController controller) {
		this.controller = controller;
		this.gui = new Gui(controller);
		gui.setTreeSelectionListener((e) -> this.handleTreeSelection(e));
		this.imagefolderPanelPresenter = new ImagefolderPanelPresenter(this.gui, controller);
	}

	public void showProgressStuff() {
		gui.pnlSettings.remove(gui.btnStart);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gui.pnlSettings.add(gui.progressBar, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 5);
		gui.btnWrite.setEnabled(false);
		gui.btnRevert.setEnabled(false);
		gui.pnlSettings.add(gui.btnStop, gbc);
		gui.pnlSettings.validate();
		gui.pnlSettings.repaint();
	}

	public void hideProgressStuff() {
		gui.pnlSettings.remove(gui.btnStop);
		gui.pnlSettings.remove(gui.progressBar);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 2, 2, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gui.pnlSettings.add(gui.btnStart, gbc);
		gui.pnlSettings.validate();
		gui.pnlSettings.repaint();
	}

	public void show() {
		gui.show();
	}

	public void geraffel(DuplicationStructureBuilder structureBuilder) {
		Directory rootDirectory = structureBuilder.getRootDirectory();
		if (rootDirectory != null) {
			OTreeNode rootNode = new OTreeNode(rootDirectory);
			fillNodes(rootDirectory, rootNode);
			fillNodesFiles(rootDirectory, rootNode);
			TreeModel treeModel = new DefaultTreeModel(rootNode);
			gui.setTreeModel(treeModel);
		}else {
			// no copies found
			gui.setTreeModel(null);
		}
		
	}

	public void geruffel() {
		System.out.println("GuiPresenter.geruffel!!!");
		OTreeNode rootNode = (OTreeNode) gui.tree.getModel().getRoot();
		rootNode.check();
		gui.tree.updateUI();
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
		gui.pnlViewContainer.removeAll();
		imagePanelController.reset();
		ImagePanel imagePanel = imagePanelController.createImagePanel(file.getFsFile());
		gui.pnlViewContainer.add(imagePanel);
		gui.pnlViewContainer.updateUI();
	}

	public Settings getSettings() {
		return gui.getProgSettings();
	}

	public void enableWrite() {
		gui.enableWrite();
	}

	public void enableRevert() {
		gui.enableRevert();
	}

	public void disableRevert() {
		gui.disableRevert();
	}

	public void disableWrite() {
		gui.disableWrite();
	}

	public void showFoundFilesCount(int foundFilesCount) {
		gui.progressBar.setMaximum(foundFilesCount);
		gui.progressBar.repaint();
		gui.setWindowTitle(foundFilesCount + " files found");
	}

	public void showFilesProcessed(int filesHashedCount) {
		gui.progressBar.setValue(filesHashedCount);
		gui.progressBar.repaint();
	}

	public void showFolder(List<LeFile> duplicates, File[] otherImages) {
		// configure GUI
		gui.pnlViewContainer.removeAll();
		gui.pnlViewContainer.add(gui.spImages);
		gui.pnlViewContainer.repaint();

		imagefolderPanelPresenter.reset();
		imagefolderPanelPresenter.setDuplicates(duplicates);
		imagefolderPanelPresenter.setFolderImages(otherImages);
	}

	public void toggleMenu() {
		if (menuShown) {
			gui.containerMenu.setVisible(false);
			gui.spMenu.setResizeWeight(0.0);
		} else {
			gui.containerMenu.setVisible(true);
			gui.spMenu.setResizeWeight(0.3);
			// Thread thread = new Thread(new Runnable() {
			// private double weight = 0.0;
			// private double stepwidth = 0.05;
			//
			// @Override
			// public void run() {
			// while (weight <= .3) {
			// weight += stepwidth;
			// System.out.println("GuiPresenter.toggleMenu().new Runnable()
			// {...}.run()"+weight);
			// gui.spMenu.setResizeWeight(weight);
			// gui.spMenu.updateUI();
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			//
			// }
			// });
			// thread.start();
		}
		gui.spMenu.updateUI();
		menuShown = !menuShown;
	}
}
