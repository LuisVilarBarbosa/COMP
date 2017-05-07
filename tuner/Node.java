package tuner;

import java.util.ArrayList;

public class Node {
    private String info;
    private ArrayList<Node> children;

    public Node(String info) {
        if (info == null)
            throw new NullPointerException();
        this.info = info;
        this.children = new ArrayList<>();
    }

    public String getInfo() {
        return info;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void addChild(Node n) {
        children.add(n);
    }

    public void removeChild(int index) {
        children.remove(index);
    }

    public Node getParent(Node node) {
        for (Node n : children) {
            if (n.equals(node))
                return this;
        }
        for (Node n : children) {
            Node parent = n.getParent(node);
            if (parent != null)
                return parent;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (!info.equals(node.info)) return false;
        return children.equals(node.children);
    }

}
