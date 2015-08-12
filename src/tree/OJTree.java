package tree;

import io.CustomFile;
import io.FileSystem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class OJTree extends JTree {
	
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
