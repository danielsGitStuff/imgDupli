package gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import data.Directory;
import data.LeFile;
import io.CustomFile;

public class OTreeNode extends DefaultMutableTreeNode {
    public OTreeNode(Object object) {
        super(object);
    }

    @Override
    public int getChildCount() {
        if (children == null) {
            return 0;
        }

        String str = "none";
        if (userObject instanceof Directory) {
            str = ((Directory) userObject).getFileSystem().getPath();
        } else if (userObject instanceof LeFile) {
            str = ((LeFile) userObject).getCustomFile().getName();
            LeFile leFile = (LeFile) userObject;
            leFile.getDirectory();
        }
        int result = (int) children.stream().map(c -> ((OTreeNode) c).getUserObject()).filter(lf -> {
            if (lf instanceof LeFile) {
                LeFile f = (LeFile) lf;
                return !f.isHidden();
            } else if (lf instanceof Directory) {
                Directory directory = (Directory) lf;
                return !directory.isHidden();
            }
            return true;
        }).count();

        System.out.println("OTreeNode.getChildCount(" + str + ")=" + result);
        if (str.equals("sub")) {
            System.out.println();
        }

        return result;
    }

    @Override
    public TreeNode getChildAfter(TreeNode aChild) {
        System.out.println("OTreeNode.getChildAfter()");
        return super.getChildAfter(aChild);
    }

    @Override
    public TreeNode getChildAt(int index) {
        //skip files marked for deletion
        OTreeNode child = (OTreeNode) children.get(index);
        if (child.getUserObject() instanceof CustomFile) {
            CustomFile f = (CustomFile) child.getUserObject();
            if (f.isMarkedForDeletion()) {
                return getChildAt(index + 1);
            }
        }
        return child;
    }
}
