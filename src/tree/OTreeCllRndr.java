package tree;

import io.FsRelated;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class OTreeCllRndr extends DefaultTreeCellRenderer {

	private static final Color DEFAULT_FOREGROUND = Color.BLACK;
	private static final Color SELECTED_FOREGROUND = new Color(180, 0, 0);

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
				setForeground(SELECTED_FOREGROUND);
				setFont(f.deriveFont(f.getStyle() | Font.BOLD));

			} else {
				setForeground(DEFAULT_FOREGROUND);
				setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));

			}
		}
		return this;
	}

}
