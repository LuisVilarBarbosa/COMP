package tuner;

import java.util.ArrayList;

public class AutoNode {
    private String info;
    private ArrayList<AutoNode> children;

    public AutoNode(String info) {
        if (info == null)
            throw new NullPointerException();
        this.info = info;
        this.children = new ArrayList<>();
    }

    public String getInfo() {
        return info;
    }

    public ArrayList<AutoNode> getChildren() {
        return children;
    }

    public void addChild(AutoNode n) {
        children.add(n);
    }

    public void removeChild(int index) {
        children.remove(index);
    }

    public AutoNode getParent(AutoNode autoNode) {
        for (AutoNode n : children) {
            if (n.equals(autoNode))
                return this;
        }
        for (AutoNode n : children) {
            AutoNode parent = n.getParent(autoNode);
            if (parent != null)
                return parent;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoNode)) return false;

        AutoNode autoNode = (AutoNode) o;

        if (!info.equals(autoNode.info)) return false;
        return children.equals(autoNode.children);
    }

}
