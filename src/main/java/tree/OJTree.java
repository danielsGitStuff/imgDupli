package tree;

import io.FsDirectory;
import io.FsFile;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class OJTree extends JTree {
	
	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		Object ins = treeNode.getUserObject();
		if (ins instanceof FsDirectory) {
			return ((FsDirectory) ins).getPath();
		} else if (ins instanceof FsFile) {
			FsFile file = (FsFile) ins;
			if (file.isMarkedForDeletion()) {
				return "deleted!";
			}
			return (file.getName());
		} else {
			return value.toString();
		}

	}
}
