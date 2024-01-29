package sgbd.tp;

import java.util.Arrays;

public class BTree {
    private Node root;
    private int t; // Minimum degree

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    // The main function that inserts a new key in this B-Tree
    public void insert(int k) {
        // If tree is empty
        if (root == null) {
            root = new Node(t, true);
            root.keys[0] = k;
            root.n = 1;
        } else {
            // If root is full, then tree grows in height
            if (root.n == 2 * t - 1) {
                Node s = new Node(t, false);
                s.children[0] = root;
                splitChild(s, 0, root);
                int i = 0;
                if (s.keys[0] < k) {
                    i++;
                }
                insertNonFull(s.children[i], k);
                root = s;
            } else {
                insertNonFull(root, k);
            }
        }
    }

    // A utility function to split the child y of this node
    private void splitChild(Node x, int i, Node y) {
        Node z = new Node(y.t, y.leaf);
        x.children[i + 1] = z;
        x.keys[i] = y.keys[t - 1];
        z.n = t - 1;
        if (!y.leaf) {
            System.arraycopy(y.children, t, z.children, 0, t);
        }
        y.n = t - 1;
        System.arraycopy(x.children, i + 1, x.children, i + 2, x.n - i);
        System.arraycopy(x.keys, i, x.keys, i + 1, x.n - i);
        x.children[i] = y;
        x.n++;
    }

    // A utility function to insert a new key in this node
    private void insertNonFull(Node x, int k) {
        int i = x.n - 1;
        if (x.leaf) {
            x.keys = Arrays.copyOf(x.keys, x.keys.length + 1);
            while (i >= 0 && k < x.keys[i]) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i + 1] = k;
            x.n = x.n + 1;
        } else {
            while (i >= 0 && k < x.keys[i]) {
                i--;
            }
            i++;
            if (x.children[i].n == 2 * t - 1) {
                splitChild(x, i, x.children[i]);
                if (k > x.keys[i]) {
                    i++;
                }
            }
            insertNonFull(x.children[i], k);
        }
    }

    // Function to traverse all nodes in a subtree rooted with this node
    public void traverse() {
        traverse(root);
    }

    private void traverse(Node x) {
        int i;
        for (i = 0; i < x.n; i++) {
            if (!x.leaf) {
                traverse(x.children[i]);
            }
            System.out.print(x.keys[i] + " ");
        }
        if (!x.leaf) {
            traverse(x.children[i]);
        }
    }

    private class Node {
        int[] keys;
        int t; // Minimum degree (defines the range for number of keys)
        Node[] children;
        int n; // Current number of keys
        boolean leaf; // Is true when node is leaf. Otherwise false

        public Node(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;
            this.keys = new int[2 * t - 1];
            this.children = new Node[2 * t];
            this.n = 0;
        }
    }
}
