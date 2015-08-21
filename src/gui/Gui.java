package gui;

import data.Directory;
import data.LeFile;
import interfaces.IGuiEventHandler;
import main.Settings;
import tree.OJTree;
import tree.OTreeCllRndr;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Gui {

	final JFrame frame = new JFrame();
	final JPanel canvas = new JPanel();
	final JSplitPane spTreeImages;
	final JScrollPane scrollPaneTree;
	final JTextField txtFileThreadRatio;
	final JTextField txtFileTypes;
	final JSplitPane spImages;
	final OJTree tree;
	final JPanel pnlSouth;
	final JPanel pnlNorth;
	final JScrollPane scrollPaneSouth;
	JTextField txtPath;
	JProgressBar progressBar;
	JButton btnStop;
	JButton btnRevert;
	IGuiEventHandler eventHandler;
	JPanel pnlSettings;
	JButton btnStart;
	JButton btnWrite;
	JPanel pnlViewContainer;
	JPanel pnlSideBar;
	JToggleButton btnToggleMenu;
	JLabel lblSearchSettings;
	JLabel lblPath_1;
	JLabel lblMisc;
	OSplitPane spMenu;
	JPanel containerMenu;

	public Gui(IGuiEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		progressBar = new JProgressBar();
		btnStop = new JButton("Stop");
		btnStop.setBackground(GuiColours.BTN_BACKGRND);
		btnRevert = new JButton("Revert Changes");
		btnRevert.setBackground(GuiColours.BTN_BACKGRND);
		btnRevert.setEnabled(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setBackground(Color.white);
		GridBagLayout gbl_canvas = new GridBagLayout();
		gbl_canvas.rowWeights = new double[] { 0.0, 1.0 };
		gbl_canvas.columnWeights = new double[] { 1.0 };
		canvas.setBackground(GuiColours.PNL_CONTAINER_BACKGR);
		canvas.setLayout(gbl_canvas);
		canvas.setVisible(false);

		pnlSettings = new JPanel();
		pnlSettings.setBackground(GuiColours.PNL_CONTAINER_BACKGR);
		GridBagConstraints gbc_pnlSettings = new GridBagConstraints();
		gbc_pnlSettings.insets = new Insets(2, 2, 2, 2);
		gbc_pnlSettings.weightx = 0.5;
		gbc_pnlSettings.anchor = GridBagConstraints.NORTH;
		gbc_pnlSettings.fill = GridBagConstraints.BOTH;
		gbc_pnlSettings.gridx = 0;
		gbc_pnlSettings.gridy = 0;
		canvas.add(pnlSettings, gbc_pnlSettings);
		GridBagLayout gbl_pnlSettings = new GridBagLayout();
		gbl_pnlSettings.columnWeights = new double[] { 0.01, 0.45, 0.05, 0.5, 0.5 };
		pnlSettings.setLayout(gbl_pnlSettings);

		btnStart = new JButton("Start");
		btnStart.setBackground(GuiColours.BTN_BACKGRND);
		btnStart.addActionListener(arg0 -> {
			System.out.println("start.clicked");
			eventHandler.onBtnStartClicked();
		});
		btnStop.addActionListener(e -> eventHandler.onBtnStopClicked());

		btnToggleMenu = new JToggleButton("Settings");
		btnToggleMenu.setBackground(GuiColours.BTN_BACKGRND);
		GridBagConstraints gbc_tglbtnM = new GridBagConstraints();
		gbc_tglbtnM.gridx = 0;
		gbc_tglbtnM.gridy = 0;
		btnToggleMenu.addActionListener(e -> eventHandler.onBtnToggleMenuClicked());
		pnlSettings.add(btnToggleMenu, gbc_tglbtnM);
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.gridwidth = 2;
		gbc_btnStart.insets = new Insets(2, 2, 2, 5);
		gbc_btnStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStart.gridx = 1;
		gbc_btnStart.gridy = 0;
		pnlSettings.add(btnStart, gbc_btnStart);

		btnWrite = new JButton("Write!");
		btnWrite.setBackground(GuiColours.BTN_BACKGRND);
		btnWrite.setEnabled(false);
		btnWrite.setForeground(Color.RED);
		GridBagConstraints gbc_btnWrite = new GridBagConstraints();
		gbc_btnWrite.insets = new Insets(2, 2, 2, 2);
		gbc_btnWrite.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnWrite.gridx = 4;
		gbc_btnWrite.gridy = 0;
		pnlSettings.add(btnWrite, gbc_btnWrite);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 2, 2, 5);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		pnlSettings.add(btnRevert, gridBagConstraints);

		tree = new OJTree();
		tree.setBackground(GuiColours.TREE_BACKGRND);
		tree.setModel(null);
		tree.setCellRenderer(new OTreeCllRndr());
		tree.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClickedOnTree(e);
			}
		});
		GridBagConstraints gbc_tree = new GridBagConstraints();
		gbc_tree.weightx = 0.1;
		gbc_tree.insets = new Insets(0, 0, 0, 5);
		gbc_tree.fill = GridBagConstraints.BOTH;
		gbc_tree.gridx = 0;
		gbc_tree.gridy = 0;
		scrollPaneTree = new JScrollPane(tree);

		pnlNorth = new JPanel();
		pnlNorth.setBackground(GuiColours.PNL_IMG_CONTAINER_BACKGRND);
		GridBagLayout gbl_pnlNorth = new GridBagLayout();
		gbl_pnlNorth.columnWeights = new double[] {};
		gbl_pnlNorth.rowWeights = new double[] {};
		pnlNorth.setLayout(gbl_pnlNorth);
		GridBagConstraints gbc_pnlNorth = new GridBagConstraints();
		gbc_pnlNorth.weighty = 1.0;
		gbc_pnlNorth.weightx = 1.0;
		gbc_pnlNorth.insets = new Insets(0, 0, 5, 0);
		gbc_pnlNorth.fill = GridBagConstraints.BOTH;
		gbc_pnlNorth.gridx = 0;
		gbc_pnlNorth.gridy = 0;
		JScrollPane scrollPaneNorth = new JScrollPane(pnlNorth);

		pnlSouth = new JPanel();
		pnlSouth.setBackground(GuiColours.PNL_IMG_CONTAINER_BACKGRND);
		GridBagLayout gbl_pnlSouth = new GridBagLayout();
		gbl_pnlSouth.columnWeights = new double[] {};
		gbl_pnlSouth.rowWeights = new double[] {};
		pnlSouth.setLayout(gbl_pnlSouth);
		GridBagConstraints gbc_scrollPaneSouth = new GridBagConstraints();
		gbc_scrollPaneSouth.weighty = 1.0;
		gbc_scrollPaneSouth.weightx = 1.0;
		gbc_scrollPaneSouth.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneSouth.gridx = 0;
		gbc_scrollPaneSouth.gridy = 1;
		scrollPaneSouth = new JScrollPane(pnlSouth);
		spImages = new OSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneNorth, scrollPaneSouth);
		spImages.setResizeWeight(.5d);
		pnlViewContainer = new JPanel();
		pnlViewContainer.setLayout(new BorderLayout());
		pnlViewContainer.add(spImages);
		spTreeImages = new OSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneTree, pnlViewContainer);
		spTreeImages.setResizeWeight(0.2);
		spTreeImages.setBackground(GuiColours.TREE_BACKGRND);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 2, 2, 2);
		gbc_splitPane.anchor = GridBagConstraints.NORTH;
		gbc_splitPane.weighty = 1.0;
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;

		canvas.add(spTreeImages, gbc_splitPane);

		/*
		 * SideBar stuff comes here
		 */
		String pathText = System.getProperty("user.home");
		GridBagConstraints gbc_lblSearchSettings = new GridBagConstraints();
		gbc_lblSearchSettings.gridwidth = 2;
		gbc_lblSearchSettings.insets = new Insets(0, 0, 5, 0);
		gbc_lblSearchSettings.gridx = 0;
		gbc_lblSearchSettings.gridy = 0;

		btnWrite.addActionListener(e -> eventHandler.onBtnWriteClicked());
		btnRevert.addActionListener(e -> eventHandler.onBtnRevertClicked());
		containerMenu = new JPanel();
		GridBagLayout gbl_containerMenu = new GridBagLayout();
		gbl_containerMenu.columnWidths = new int[] { 0 };
		gbl_containerMenu.rowHeights = new int[] { 0 };
		gbl_containerMenu.columnWeights = new double[] { 0.0 };
		gbl_containerMenu.rowWeights = new double[] { 0.0 };
		containerMenu.setLayout(gbl_containerMenu);
		containerMenu.setBackground(GuiColours.PNL_CONTAINER_BACKGR);
		spMenu = new OSplitPane(JSplitPane.HORIZONTAL_SPLIT, containerMenu, canvas);

		JButton btnPath = new JButton("Choose");
		btnPath.setBackground(GuiColours.BTN_BACKGRND);
		btnPath.addActionListener((e) -> {
			File file = new File(txtPath.getText());
			JFileChooser chooser = new JFileChooser();
			if (file.exists() && file.isDirectory()) {
				chooser.setCurrentDirectory(file);
			}
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				txtPath.setText(chooser.getSelectedFile().getAbsolutePath());
			} else {
				System.out.println("No Selection ");
			}
		});
		GridBagConstraints gbc_btnPath = new GridBagConstraints();
		gbc_btnPath.insets = new Insets(2, 2, 2, 2);
		gbc_btnPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPath.gridx = 1;
		gbc_btnPath.gridy = 2;

		txtPath = new JTextField();
		txtPath.setBorder(BorderFactory.createEmptyBorder());
		txtPath.setBackground(GuiColours.TXTEDIT_BACKGRND);
		txtPath.setText(pathText);
		GridBagConstraints gbc_txtPath = new GridBagConstraints();
		gbc_txtPath.insets = new Insets(2, 2, 2, 2);
		gbc_txtPath.fill = GridBagConstraints.BOTH;
		gbc_txtPath.weightx = 0.5;
		gbc_txtPath.gridx = 1;
		gbc_txtPath.gridy = 1;
		txtPath.setColumns(10);

		pnlSideBar = new JPanel();
		pnlSideBar.setBackground(GuiColours.PNL_MENU);
		// frame.getContentPane().add(pnlSideBar, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlSideBar = new GridBagLayout();
		gbl_pnlSideBar.columnWidths = new int[] { 0, 0 };
		gbl_pnlSideBar.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_pnlSideBar.columnWeights = new double[] { 0.0, 0.0 };
		gbl_pnlSideBar.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		pnlSideBar.setLayout(gbl_pnlSideBar);

		lblSearchSettings = new JLabel("Settings");
		lblSearchSettings.setForeground(GuiColours.LBL_MENU_MAIN_FORE);
		JPanel pnlLblSearchSettings = new JPanel();
		pnlLblSearchSettings.setBackground(GuiColours.LBL_MENU_MAIN_BACKGRND);
		GridBagConstraints gbc_pnlLblSearchSettings = new GridBagConstraints();
		gbc_pnlLblSearchSettings.weightx = 0.5;
		gbc_pnlLblSearchSettings.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlLblSearchSettings.insets = new Insets(0, 0, 5, 0);
		gbc_pnlLblSearchSettings.anchor = GridBagConstraints.NORTH;
		gbc_pnlLblSearchSettings.gridwidth = 2;
		gbc_pnlLblSearchSettings.gridx = 0;
		gbc_pnlLblSearchSettings.gridy = 0;
		pnlSideBar.add(pnlLblSearchSettings, gbc_pnlLblSearchSettings);
		pnlLblSearchSettings.add(lblSearchSettings);
		// pnlSideBar.add(lblSearchSettings, gbc_lblSearchSettings);

		lblPath_1 = new JLabel("Path");
		GridBagConstraints gbc_lblPath_1 = new GridBagConstraints();
		gbc_lblPath_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblPath_1.anchor = GridBagConstraints.EAST;
		gbc_lblPath_1.gridx = 0;
		gbc_lblPath_1.gridy = 1;

		pnlSideBar.add(lblPath_1, gbc_lblPath_1);
		pnlSideBar.add(btnPath, gbc_btnPath);
		pnlSideBar.add(txtPath, gbc_txtPath);

		lblMisc = new JLabel("Misc");
		lblMisc.setForeground(GuiColours.LBL_MENU_MAIN_FORE);

		JPanel pnlLblMisc = new JPanel();
		pnlLblMisc.setBackground(GuiColours.LBL_MENU_MAIN_BACKGRND);
		GridBagConstraints gbc_pnlLblMisc = new GridBagConstraints();
		gbc_pnlLblMisc.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlLblMisc.insets = new Insets(0, 0, 5, 0);
		gbc_pnlLblMisc.anchor = GridBagConstraints.NORTH;
		gbc_pnlLblMisc.gridwidth = 2;
		gbc_pnlLblMisc.gridx = 0;
		gbc_pnlLblMisc.gridy = 4;
		pnlLblMisc.add(lblMisc);
		pnlSideBar.add(pnlLblMisc, gbc_pnlLblMisc);

		txtFileThreadRatio = new JTextField();
		txtFileThreadRatio.setText("200");
		txtFileThreadRatio.setBorder(BorderFactory.createEmptyBorder());
		txtFileThreadRatio.setBackground(GuiColours.TXTEDIT_BACKGRND);
		GridBagConstraints gbc_txtFileThreadRatio = new GridBagConstraints();
		gbc_txtFileThreadRatio.insets = new Insets(2, 2, 2, 2);
		gbc_txtFileThreadRatio.fill = GridBagConstraints.BOTH;
		gbc_txtFileThreadRatio.anchor = GridBagConstraints.WEST;
		gbc_txtFileThreadRatio.gridx = 1;
		gbc_txtFileThreadRatio.gridy = 5;
		txtFileThreadRatio.setColumns(5);
		pnlSideBar.add(txtFileThreadRatio, gbc_txtFileThreadRatio);

		JLabel lblFileTypes = new JLabel("file types");
		// lblFileTypes.setForeground(GuiColours.LBL_MENU);
		GridBagConstraints gbc_fileTypes = new GridBagConstraints();
		gbc_fileTypes.insets = new Insets(0, 5, 5, 5);
		gbc_fileTypes.anchor = GridBagConstraints.EAST;
		gbc_fileTypes.gridx = 0;
		gbc_fileTypes.gridy = 3;
		pnlSideBar.add(lblFileTypes, gbc_fileTypes);

		txtFileTypes = new JTextField();
		txtFileTypes.setBorder(BorderFactory.createEmptyBorder());
		txtFileTypes.setBackground(GuiColours.TXTEDIT_BACKGRND);
		txtFileTypes.setText("jpg,jpeg,png,bmp,psd");
		GridBagConstraints gbc_txtFileTypes = new GridBagConstraints();
		gbc_txtFileTypes.insets = new Insets(2, 2, 2, 2);
		gbc_txtFileTypes.fill = GridBagConstraints.BOTH;
		gbc_txtFileTypes.gridx = 1;
		gbc_txtFileTypes.gridy = 3;

		txtFileTypes.setColumns(10);
		pnlSideBar.add(txtFileTypes, gbc_txtFileTypes);

		JLabel lblThreadRatio = new JLabel("F/T Ratio");
		lblThreadRatio.setToolTipText("File to Thread Ratio");
		GridBagConstraints gbc_ThreadRatio = new GridBagConstraints();
		gbc_ThreadRatio.insets = new Insets(5, 5, 5, 5);
		gbc_ThreadRatio.anchor = GridBagConstraints.EAST;
		gbc_ThreadRatio.gridx = 0;
		gbc_ThreadRatio.gridy = 5;
		pnlSideBar.add(lblThreadRatio, gbc_ThreadRatio);
		GridBagConstraints gbc_pnlSideBar = new GridBagConstraints();
		gbc_pnlSideBar.weighty = 1.0;
		gbc_pnlSideBar.weightx = 1.0;
		gbc_pnlSideBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlSideBar.insets = new Insets(0, 0, 0, 5);
		gbc_pnlSideBar.anchor = GridBagConstraints.NORTH;
		gbc_pnlSideBar.gridx = 0;
		gbc_pnlSideBar.gridy = 0;
		containerMenu.add(pnlSideBar, gbc_pnlSideBar);
		containerMenu.setVisible(false);
		spMenu.setResizeWeight(0.0);
		spMenu.setDividerSize(0);
		frame.getContentPane().add(spMenu, BorderLayout.CENTER);
		frame.setSize(1024, 768);
	}

	public JSplitPane getSpTreeImages() {
		return spTreeImages;
	}

	protected void handleMouseClickedOnTree(MouseEvent e) {
		if (e.getButton() == 3) {
			OJTree tree = (OJTree) e.getSource();
			TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
			DefaultMutableTreeNode clickedNnode = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object userObject = clickedNnode.getUserObject();
			JPopupMenu menu = new JPopupMenu("bla");

			JMenuItem menuItemDelAllButThis = null;
			JMenuItem menuItemDelThis = null;

			if (userObject instanceof LeFile) {
				LeFile leFile = (LeFile) userObject;
				menuItemDelAllButThis = new JMenuItem("delete all other copies");
				menuItemDelAllButThis.addActionListener(ev -> eventHandler.onFileDeleteFilesEverywhereElse(leFile));
				menuItemDelThis = new JMenuItem("delete this File");
				menuItemDelThis.addActionListener(ev -> eventHandler.onFileDeleteFile(leFile));
			} else if (userObject instanceof Directory) {
				Directory directory = (Directory) userObject;
				menuItemDelAllButThis = new JMenuItem("delete copies from everwhere else");
				menuItemDelAllButThis.addActionListener(ev -> eventHandler.onDirDeleteFilesEverywhereElse(directory));
				menuItemDelThis = new JMenuItem("delete these Files");
				menuItemDelThis.addActionListener(ev -> eventHandler.onDirDeleteFiles(directory));
			}
			menu.add(menuItemDelAllButThis);
			menu.add(menuItemDelThis);
			menu.show(tree, e.getX(), e.getY());
			System.out.println();
		}

	}

	public void show() {
		canvas.setVisible(true);
	}

	public Settings getProgSettings() {
		Settings result = new Settings();
		result.setFileThreadRatio(Integer.parseInt(txtFileThreadRatio.getText()));
		result.setRootPath(txtPath.getText());
		List<String> fileTypes = Arrays.stream(txtFileTypes.getText().split(",")).map(String::toLowerCase)
				.collect(Collectors.toList());
		result.setFileTypes(fileTypes);
		return result;
	}

	public void setTreeModel(TreeModel treeModel) {
		tree.setModel(treeModel);
	}

	public void setTreeSelectionListener(TreeSelectionListener listener) {
		for (TreeSelectionListener l : tree.getTreeSelectionListeners()) {
			tree.removeTreeSelectionListener(l);
		}
		tree.addTreeSelectionListener((e) -> {
			listener.valueChanged(e);
			tree.repaint();
		});
	}

	public void enableWrite() {
		btnWrite.setEnabled(true);
	}

	public void disableWrite() {
		btnWrite.setEnabled(false);
	}

	public void enableRevert() {
		btnRevert.setEnabled(true);
	}

	public void disableRevert() {
		btnRevert.setEnabled(false);
	}

	public void setWindowTitle(String windowTitle) {
		frame.setTitle(windowTitle);
	}

	public JPanel getPnlNorth() {
		return pnlNorth;
	}

	public JPanel getPnlSouth() {
		return pnlSouth;
	}

	public JScrollPane getScrollPaneSouth() {
		return scrollPaneSouth;
	}

	public JSplitPane getSpImages() {
		return spImages;
	}
}
