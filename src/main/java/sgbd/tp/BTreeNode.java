package sgbd.tp;

import java.util.ArrayList;
import java.util.List;

import static sgbd.tp.BTree.ORDER;

public class BTreeNode {
    List<Integer> keys = new ArrayList<>();
    List<BTreeNode> children = new ArrayList<>();
    boolean leaf = true;

    // Constructor
    BTreeNode(boolean leaf) {
        this.leaf = leaf;
    }

    // Insert key within a node
    void insertNonFull(int key) {
        int i = keys.size() - 1;
        if (leaf) {
            // Find the position to insert the new key
            while (i >= 0 && keys.get(i) > key) {
                i--;
            }
            // Insert the new key at found position
            keys.add(i + 1, key);
        } else {
            // Find the child which is going to have the new key
            while (i >= 0 && keys.get(i) > key) {
                i--;
            }
            i++;
            // Check if the found child is full
            if (children.get(i).keys.size() == ORDER - 1) {
                List<Integer> tempKeysBeforeSplit = new ArrayList<>(children.get(i).keys);
                tempKeysBeforeSplit.add(key);
                tempKeysBeforeSplit.sort(Integer::compareTo);
                splitChild(i, children.get(i), tempKeysBeforeSplit);
                if (key > keys.get(i)) {
                    i++;
                }
            } else {
                children.get(i).insertNonFull(key);
            }
        }
    }

    // Split the child y of this node. i is index of y in child array children.
    void splitChild(int i, BTreeNode leftChild, List<Integer> tempKeysBeforeSplit) {
        BTreeNode rightChild = new BTreeNode(leftChild.leaf);

        leftChild.keys = new ArrayList<>(tempKeysBeforeSplit.subList(0, ORDER / 2));
        rightChild.keys = new ArrayList<>(tempKeysBeforeSplit.subList(ORDER / 2 + 1, ORDER));

        if (!leftChild.leaf) {
            for (int j = 0; j < ORDER / 2; j++) {
                rightChild.children.add(leftChild.children.remove(ORDER / 2));
            }
        }
        children.add(i + 1, rightChild);
        // Add the median key to parent node
        int median = tempKeysBeforeSplit.get(ORDER / 2);
        keys.add(i, median);
    }
}
