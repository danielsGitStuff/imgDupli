package tree;

import io.FsRelated;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import gui.GuiColours;

import java.awt.*;

public class OTreeCllRndr extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		Object userObject = treeNode.getUserObject();

		if (userObject instanceof FsRelated) {
			FsRelated ifsRelated = (FsRelated) userObject;
			Font f = getFont();
			if (ifsRelated.isHighlighted()) {
				setForeground(GuiColours.SELECTED_FOREGROUND);
				setFont(f.deriveFont(f.getStyle() | Font.BOLD));

			} else {
				setForeground(GuiColours.DEFAULT_FOREGROUND);
				setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));

			}
		}
		return this;
	}
	
	 @Override
	    public Color getBackgroundNonSelectionColor() {
		 return GuiColours.TREE_BACKGRND;
	    }
	 
	 @Override
	public Color getBackgroundSelectionColor() {
		return GuiColours.TREE_SELECTED_BACKGRND;
	}

}
