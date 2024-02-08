package sgbd.tp;

import java.util.*;

public class BTree {
    public static final int ORDER = 5;
    BTreeNode root; // Root of B-Tree

    public BTree() {
        root = new BTreeNode(true);
    }

    // Insert a key into the B-Tree
    public void insert(int key) {
        System.out.println("Inserted key ==> " + key);

        BTreeNode r = root;
        if (r.keys.size() == ORDER - 1) {
            BTreeNode s = new BTreeNode(false);
            root = s;
            s.children.add(r);
            List<Integer> tempKeysBeforeSplit = new ArrayList<>(r.keys);
            tempKeysBeforeSplit.add(key);
            tempKeysBeforeSplit.sort(Integer::compareTo);
            s.splitChild(0, r, tempKeysBeforeSplit);
        } else {
            r.insertNonFull(key);
        }

        printTree();
    }

    // Method to print the tree (for visualization)
    private void printTree(BTreeNode node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.keys);
        for (int i = 0; i < node.children.size() - 1; i++) {
            printTree(node.children.get(i), prefix + (isTail ? "    " : "│   "), false);
        }
        if (!node.children.isEmpty()) {
            printTree(node.children.getLast(), prefix + (isTail ?"    " : "│   "), true);
        }
    }

    public void printTree() {
        printTree(root, "", true);
    }
}
