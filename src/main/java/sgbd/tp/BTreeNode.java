package sgbd.tp;

import java.util.ArrayList;
import java.util.List;

import static sgbd.tp.BTree.ORDER;

public class BTreeNode {
    String keysValues;
    List<Integer> keys = new ArrayList<>();
    List<BTreeNode> children = new ArrayList<>();
    boolean leaf = true;
    BTreeNode parent;

    // Constructor
    BTreeNode(boolean leaf) {
        this.leaf = leaf;
    }

    // Insert key within a node
    void insertNonFull(int key) {
        int index = keys.size() - 1;
        if (leaf) {
            // Find the position to insert the new key
            while (index >= 0 && keys.get(index) > key) {
                index--;
            }
            // Insert the new key at found position
            keys.add(index + 1, key);
            keysValues = keys.toString();
        } else {
            // Find the child which is going to have the new key
            while (index >= 0 && keys.get(index) > key) {
                index--;
            }
            index++;
            // Check if the found child is full
            if (children.get(index).keys.size() == ORDER - 1) {
                if (!children.get(index).leaf) {
                    children.get(index).insertNonFull(key);
                } else {
                    List<Integer> tempKeysBeforeSplit = new ArrayList<>(children.get(index).keys);
                    tempKeysBeforeSplit.add(key);
                    tempKeysBeforeSplit.sort(Integer::compareTo);
                    splitChild(index, children.get(index), tempKeysBeforeSplit);
                }
            } else {
                children.get(index).insertNonFull(key);
            }
        }
    }

    // Split the child y of this node. i is index of y in child array children.
    void splitChild(int indexLeftChild, BTreeNode leftChild, List<Integer> tempKeysBeforeSplit) {
        BTreeNode rightChild = new BTreeNode(leftChild.leaf);

        leftChild.keys = new ArrayList<>(tempKeysBeforeSplit.subList(0, ORDER / 2));
        leftChild.keysValues = leftChild.keys.toString();
        leftChild.parent = this;
        rightChild.keys = new ArrayList<>(tempKeysBeforeSplit.subList(ORDER / 2 + 1, ORDER));
        rightChild.keysValues = rightChild.keys.toString();
        rightChild.parent = this;

        if (!leftChild.leaf) {
            int size = leftChild.children.size();
            rightChild.children = new ArrayList<>(leftChild.children.subList(size / 2, size));
            rightChild.children.forEach(c -> c.parent = rightChild);
            leftChild.children = new ArrayList<>(leftChild.children.subList(0, size / 2));
            leftChild.children.forEach(c -> c.parent = leftChild);
        }

        children.add(indexLeftChild + 1, rightChild);
        // Add the median key to parent node
        int median = tempKeysBeforeSplit.get(ORDER / 2);
        keys.add(indexLeftChild, median);
        keysValues = keys.toString();

        if (keys.size() == ORDER && parent == null) {
            System.out.println("Splitting root");
            BTreeNode s = new BTreeNode(false);
            s.children.add(this);
            List<Integer> tempKeysBeforeSplitRoot = new ArrayList<>(keys);
            tempKeysBeforeSplitRoot.sort(Integer::compareTo);
            keys.remove(Integer.valueOf(median));
            keysValues = keys.toString();
            s.splitChild(0, this, tempKeysBeforeSplitRoot);
            BTree.root = s;
        }
        if (keys.size() == ORDER) {
            tempKeysBeforeSplit = new ArrayList<>(keys);
            keys.remove(Integer.valueOf(median));
            keysValues = keys.toString();
            parent.splitChild(parent.children.indexOf(this), this, tempKeysBeforeSplit);
        }
    }

    // Method to search a key in the tree
    boolean isInsertable(int key) {
        int i = 0;
        while (i < keys.size() && key > keys.get(i)) {
            i++;
        }
        if (i < keys.size() && keys.get(i) == key) {
            return false;
        }
        if (i < ORDER - 1) {
            return true;
        }
        if (i == ORDER - 1 && leaf) {
            return false;
        }
        return children.get(i).isInsertable(key);
    }

    boolean search(int key) {
        int i = 0;
        while (i < keys.size() && key > keys.get(i)) {
            i++;
        }
        if (i < keys.size() && keys.get(i) == key) {
            return true;
        }
        if (leaf) {
            return false;
        }
        return children.get(i).search(key);
    }
}
