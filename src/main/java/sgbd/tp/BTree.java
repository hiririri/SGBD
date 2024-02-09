package sgbd.tp;

import java.util.ArrayList;
import java.util.List;

public class BTree {
    public static final int ORDER = 5;
    static BTreeNode root; // Root of B-Tree

    public BTree() {
        root = new BTreeNode(true);
    }

    // Insert a key into the B-Tree
    public void insert(int key) {
        System.out.println("Inserted key ==> " + key);

        BTreeNode r = root;

        if (r.keys.size() == ORDER - 1) {
            if (r.isInsertable(key)) {
                r.insertNonFull(key);
            } else {
                BTreeNode s = new BTreeNode(false);
                root = s;
                s.children.add(r);
                List<Integer> tempKeysBeforeSplit = new ArrayList<>(r.keys);
                tempKeysBeforeSplit.add(key);
                tempKeysBeforeSplit.sort(Integer::compareTo);
                s.splitChild(0, r, tempKeysBeforeSplit);
            }
        } else {
            r.insertNonFull(key);
        }

        printTree();
    }

    // Method to print the tree (for visualization)
    private String printTree(BTreeNode node, String prefix, boolean isTail) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(isTail ? "└── " : "├── ").append(node.keys).append("\n");
        for (int i = 0; i < node.children.size() - 1; i++) {
            sb.append(printTree(node.children.get(i), prefix + (isTail ? "    " : "│   "), false));
        }
        if (!node.children.isEmpty()) {
            sb.append(printTree(node.children.getLast(), prefix + (isTail ? "    " : "│   "), true));
        }
        return sb.toString();
    }

    public void printTree() {
        System.out.println(printTree(root, "", true));
    }
}
