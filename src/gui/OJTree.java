package gui;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import io.CustomFile;
import io.FileSystem;

class OJTree extends JTree {
	
	
	
	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		Object ins = treeNode.getUserObject();
		if (ins instanceof FileSystem) {
			return ((FileSystem) ins).getPath();
		} else if (ins instanceof CustomFile) {
			CustomFile file = (CustomFile) ins;
			if (file.isMarkedForDeletion()) {
				return "deleted!";
			}
			return (file.getName());
		} else {
			return value.toString();
		}

	}
}
