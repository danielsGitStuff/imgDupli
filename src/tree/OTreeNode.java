package tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import io.FSRelated;

import java.util.Vector;

public class OTreeNode extends DefaultMutableTreeNode {
    public OTreeNode(Object object) {
        super(object);
    }

    public void check() {
        //nothing here -> remove from parent
        FSRelated fsRelated = (FSRelated) userObject;
        if (fsRelated.isHidden()) {
            OTreeNode parent = (OTreeNode) this.parent;
            parent.removeOTreeNode(this);
        }
        if (children != null) {
            Vector children = (Vector) this.children.clone();
            children.forEach(child -> ((OTreeNode) child).check());
        }
    }

    private void removeOTreeNode(OTreeNode treeNode) {
        children.remove(treeNode);
    }


    @Override
    public int getChildCount() {
        if (children == null) {
            return 0;
        }
        return children.size();

//        String str = "none";
//        if (userObject instanceof Directory) {
//            str = ((Directory) userObject).getFileSystem().getPath();
//        } else if (userObject instanceof LeFile) {
//            str = ((LeFile) userObject).getCustomFile().getName();
//            LeFile leFile = (LeFile) userObject;
//            leFile.getDirectory();
//        }
//        if (str.contains("picTest")) {
//            System.out.printf("bla");
//        }
//        int result = (int) children.stream().map(c -> ((OTreeNode) c).getUserObject()).filter(lf -> {
//            if (lf instanceof LeFile) {
//                LeFile f = (LeFile) lf;
//                return !f.isHidden();
//            } else if (lf instanceof Directory) {
//                Directory directory = (Directory) lf;
//                return !directory.isHidden();
//            }
//            return true;
//        }).count();
//
//        System.out.println("OTreeNode.getChildCount(" + str + ")=" + result);
//        if (str.equals("sub")) {
//            System.out.println();
//        }
//
//        return result;
    }

    @Override
    public TreeNode getChildAfter(TreeNode aChild) {
        System.out.println("OTreeNode.getChildAfter()");
        return super.getChildAfter(aChild);
    }

//    @Override
//    public TreeNode getChildAt(int index) {
//        String str = "none";
//        if (userObject instanceof Directory) {
//            str = ((Directory) userObject).getFileSystem().getPath();
//        } else if (userObject instanceof LeFile) {
//            str = ((LeFile) userObject).getCustomFile().getName();
//            LeFile leFile = (LeFile) userObject;
//            leFile.getDirectory();
//        }
//        if (str.contains("picTest")) {
//            System.out.printf("bla");
//        }
//        //skip files marked for deletion
//        OTreeNode child = (OTreeNode) children.get(index);
//        FSRelated fsRelated = (FSRelated) child.getUserObject();
//        if (fsRelated.isHidden())
//            return getChildAt(index + 1);
//        return child;
//    }
}
