package gui;

import data.Directory;
import data.LeFile;
import interfaces.IGuiEventHandler;
import main.Settings;

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

	private final JFrame frame = new JFrame();
	private final JPanel canvas = new JPanel();

	private JTextField txtPath;
	private final JTextField txtFileThreadRatio;
	private final JTextField txtFileTypes;
	private JProgressBar progressBar;
	private JButton btnStop;

	private JButton btnRevert;
	private IGuiEventHandler eventHandler;

	public Gui(IGuiEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		progressBar = new JProgressBar();
		btnStop = new JButton("Stop");
		btnRevert = new JButton("Revert Changes");
		btnRevert.setEnabled(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setBackground(Color.white);
		frame.setSize(1024, 768);
		GridBagLayout gbl_canvas = new GridBagLayout();
		gbl_canvas.rowWeights = new double[] { 0.0, 1.0 };
		gbl_canvas.columnWeights = new double[] { 1.0 };
		canvas.setLayout(gbl_canvas);
		canvas.setVisible(false);
		frame.getContentPane().add(canvas);

		pnlSettings = new JPanel();
		pnlSettings.setBackground(Color.GRAY);
		GridBagConstraints gbc_pnlSettings = new GridBagConstraints();
		gbc_pnlSettings.weightx = 0.5;
		gbc_pnlSettings.anchor = GridBagConstraints.NORTH;
		gbc_pnlSettings.insets = new Insets(0, 0, 5, 0);
		gbc_pnlSettings.fill = GridBagConstraints.BOTH;
		gbc_pnlSettings.gridx = 0;
		gbc_pnlSettings.gridy = 0;
		canvas.add(pnlSettings, gbc_pnlSettings);
		GridBagLayout gbl_pnlSettings = new GridBagLayout();
		pnlSettings.setLayout(gbl_pnlSettings);

		JButton btnPath = new JButton("Path");
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
		gbc_btnPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPath.gridx = 0;
		gbc_btnPath.gridy = 0;
		pnlSettings.add(btnPath, gbc_btnPath);

		txtPath = new JTextField();
		txtPath.setText("~/Documents");
		GridBagConstraints gbc_txtPath = new GridBagConstraints();
		gbc_txtPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPath.anchor = GridBagConstraints.WEST;
		gbc_txtPath.weightx = 0.5;
		gbc_txtPath.gridx = 1;
		gbc_txtPath.gridy = 0;
		pnlSettings.add(txtPath, gbc_txtPath);
		txtPath.setColumns(10);

		JLabel lblNewLabel = new JLabel("File/Thread Ratio");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		pnlSettings.add(lblNewLabel, gbc_lblNewLabel);

		txtFileThreadRatio = new JTextField();
		txtFileThreadRatio.setText("200");
		GridBagConstraints gbc_txtFileThreadRatio = new GridBagConstraints();
		gbc_txtFileThreadRatio.anchor = GridBagConstraints.WEST;
		gbc_txtFileThreadRatio.gridx = 3;
		gbc_txtFileThreadRatio.gridy = 0;
		pnlSettings.add(txtFileThreadRatio, gbc_txtFileThreadRatio);
		txtFileThreadRatio.setColumns(5);

		JLabel lblNewLabel_1 = new JLabel("file types");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.gridx = 4;
		gbc_lblNewLabel_1.gridy = 0;
		pnlSettings.add(lblNewLabel_1, gbc_lblNewLabel_1);

		txtFileTypes = new JTextField();
		txtFileTypes.setText("jpg,jpeg,png,bmp,psd");
		GridBagConstraints gbc_txtFileTypes = new GridBagConstraints();
		gbc_txtFileTypes.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFileTypes.gridx = 5;
		gbc_txtFileTypes.gridy = 0;
		pnlSettings.add(txtFileTypes, gbc_txtFileTypes);
		txtFileTypes.setColumns(10);

		btnStart = new JButton("Start");
		btnStart.addActionListener(arg0 -> {
			System.out.println("start.clicked");
			eventHandler.onBtnStartClicked();
		});
		btnStop.addActionListener(e -> eventHandler.onBtnStopClicked());
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.gridwidth = 2;
		gbc_btnStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStart.gridx = 0;
		gbc_btnStart.gridy = 1;
		gbc_btnStart.gridwidth = 4;
		pnlSettings.add(btnStart, gbc_btnStart);

		btnWrite = new JButton("Write!");
		btnWrite.setEnabled(false);
		btnWrite.setForeground(Color.RED);
		GridBagConstraints gbc_btnWrite = new GridBagConstraints();
		gbc_btnWrite.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnWrite.gridx = 5;
		gbc_btnWrite.gridy = 1;
		pnlSettings.add(btnWrite, gbc_btnWrite);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 1;
		pnlSettings.add(btnRevert, gridBagConstraints);

		tree = new OJTree();
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
		JScrollPane scrollPaneTree = new JScrollPane(tree);

		pnlNorth = new JPanel();
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
		// pnlImagePresentation.add(scrollPaneSouth, gbc_scrollPaneSouth);
		pnlImagePresentation = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneNorth, scrollPaneSouth);
		pnlImagePresentation.setResizeWeight(.5d);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneTree, pnlImagePresentation);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.anchor = GridBagConstraints.NORTH;
		gbc_splitPane.weighty = 1.0;
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		canvas.add(splitPane, gbc_splitPane);
		btnWrite.addActionListener(e -> eventHandler.onBtnWriteClicked());
		btnRevert.addActionListener(e -> eventHandler.onBtnRevertClicked());
	}

	protected void handleMouseClickedOnTree(MouseEvent e) {
		if (e.getButton() == 3) {
			OJTree tree = (OJTree) e.getSource();
			TreePath path = tree.getSelectionPath();
			path = tree.getClosestPathForLocation(e.getX(), e.getY());
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

	private final JSplitPane pnlImagePresentation;
	private final OJTree tree;
	private final JPanel pnlSouth;
	private final JPanel pnlNorth;
	private final JScrollPane scrollPaneSouth;
	private JPanel pnlSettings;
	private JButton btnStart;
	private JButton btnWrite;

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

	public JSplitPane getPnlImagePresentation() {
		return pnlImagePresentation;
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

	public void showProgressStuff() {
		pnlSettings.remove(btnStart);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlSettings.add(progressBar, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlSettings.add(btnStop, gbc);
		pnlSettings.validate();
		pnlSettings.repaint();
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void hideProgressStuff() {
		pnlSettings.remove(btnStop);
		pnlSettings.remove(progressBar);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlSettings.add(btnStart, gbc);
		pnlSettings.validate();
		pnlSettings.repaint();
	}

    public OJTree getTree() {
        return tree;
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

}
