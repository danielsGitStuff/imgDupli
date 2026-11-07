package tree;

import io.FsRelated;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Vector;

public class OTreeNode extends DefaultMutableTreeNode {
    public OTreeNode(Object object) {
        super(object);
    }

    public void check() {
        //nothing here -> remove from parent
        FsRelated fsRelated = (FsRelated) userObject;
        if (fsRelated.isHidden()) {
            OTreeNode parent = (OTreeNode) this.parent;
            parent.removeOTreeNode(this);
        }
        if (children != null) {
            Vector<TreeNode> children =  new Vector<>(this.children);
            children.forEach(child -> ((OTreeNode) child).check());
        }
    }

    private void removeOTreeNode(OTreeNode treeNode) {
        children.remove(treeNode);
    }
}
