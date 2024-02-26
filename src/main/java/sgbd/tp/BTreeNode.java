package sgbd.tp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BTreeNode {
    private final int order;
    private BTree bTree;
    private int height;
    private BTreeNode parent;
    private List<Integer> keys;
    private Map<Integer, BTreeNode> children;

    public BTreeNode(int order, BTreeNode parent, BTree bTree) {
        this(order, bTree);
        if (parent == null) {
            throw new IllegalArgumentException("Parent cannot be null");
        }
        this.parent = parent;
        height = parent.height + 1;
    }

    public BTreeNode(int order, BTree bTree) {
        this.bTree = bTree;
        this.order = order;
        keys = new ArrayList<>();
        children = new HashMap<>();
        parent = null;
        height = 1;
    }

    public BTreeNode(BTreeNode BTreeNode) {
        bTree = BTreeNode.bTree;
        order = BTreeNode.order;
        keys = new ArrayList<>(BTreeNode.keys);
        children = new HashMap<>(BTreeNode.children);
        parent = BTreeNode.parent;
        height = BTreeNode.height;
    }

    public int search(int key) {
        if (keys.contains(key)) {
            return height;
        }
        if (children.isEmpty()) {
            return -1;
        }
        for (BTreeNode child : children.values()) {
            int result = child.search(key);
            if (result != -1) {
                return result;
            }
        }
        return -1;
    }

    public void insert(int key) {
        if (isFull()) {
            handleFullNodeInsert(key);
        } else {
            handleNonFullNodeInsert(key);
        }
    }

    private boolean isFull() {
        return keys.size() == order - 1;
    }

    private void handleFullNodeInsert(int key) {
        if (isLeaf()) {
            insertAndUpKey(key);
        } else {
            int indexChild = getIndexChildToInsert(key);
            if (!children.containsKey(indexChild)) {
                children.put(indexChild, new BTreeNode(order, this, bTree));
            }
            children.get(indexChild).insert(key);
        }
    }

    private void handleNonFullNodeInsert(int key) {
        if (isLeaf()) {
            keys.add(key);
            keys.sort(Integer::compareTo);
        } else {
            int indexChild = getIndexChildToInsert(key);
            if (!children.containsKey(indexChild)) {
                children.put(indexChild, new BTreeNode(order, this, bTree));
            }
            children.get(indexChild).insert(key);
        }
    }

    private List<BTreeNode> insertAndUpKey(int key) {
        List<Integer> tmpSortedList = Stream.concat(keys.stream(), Stream.of(key)).sorted().collect(Collectors.toList());

        int medianKeyIndexInTmpSortedList = tmpSortedList.size() / 2;
        int medianKey = tmpSortedList.get(medianKeyIndexInTmpSortedList);

        if (parent == null) {
            BTreeNode parentBTreeNode = new BTreeNode(order, bTree);
            setParent(parentBTreeNode);
            bTree.setRoot(parentBTreeNode);
        }

        BTreeNode leftCurrentBTreeNode = new BTreeNode(this);
        BTreeNode rightCurrentBTreeNode = new BTreeNode(order, parent, bTree);

        leftCurrentBTreeNode.keys = new ArrayList<>(tmpSortedList.subList(0, medianKeyIndexInTmpSortedList));
        rightCurrentBTreeNode.keys = new ArrayList<>(tmpSortedList.subList(medianKeyIndexInTmpSortedList + 1, tmpSortedList.size()));

        if (parent.isFull()) {
            BTreeNode snapshotParentBTreeNode = new BTreeNode(parent);
            List<BTreeNode> splittedBTreeNodes = parent.insertAndUpKey(medianKey);
            int indexToAddMedianKey = insertMedianKeyToParent(medianKey, snapshotParentBTreeNode);

            BTreeNode leftSplittedBTreeNode = splittedBTreeNodes.get(0);
            leftSplittedBTreeNode.children.clear();
            BTreeNode rightSplittedBTreeNode = splittedBTreeNodes.get(1);
            rightSplittedBTreeNode.children.clear();

            snapshotParentBTreeNode.children.put(indexToAddMedianKey, leftCurrentBTreeNode);
            snapshotParentBTreeNode.children.put(indexToAddMedianKey + 1, rightCurrentBTreeNode);

            snapshotParentBTreeNode.children.forEach((indexChild, child) -> {
                int cutOffPoint = snapshotParentBTreeNode.children.size() / 2;
                if (indexChild < cutOffPoint) {
                    leftSplittedBTreeNode.children.put(indexChild, child);
                    if (child.equals(leftCurrentBTreeNode)) {
                        leftCurrentBTreeNode.setParent(leftSplittedBTreeNode);
                    } else {
                        rightCurrentBTreeNode.setParent(leftSplittedBTreeNode);
                    }
                } else {
                    rightSplittedBTreeNode.children.put(indexChild - cutOffPoint, child);
                    if (child.equals(leftCurrentBTreeNode)) {
                        leftCurrentBTreeNode.setParent(rightSplittedBTreeNode);
                    } else {
                        rightCurrentBTreeNode.setParent(rightSplittedBTreeNode);
                    }
                }
            });
        } else {
            int indexToAddMedianKey = insertMedianKeyToParent(medianKey, parent);
            parent.children.put(indexToAddMedianKey, leftCurrentBTreeNode);
            parent.children.put(indexToAddMedianKey + 1, rightCurrentBTreeNode);
        }
        return List.of(leftCurrentBTreeNode, rightCurrentBTreeNode);
    }

    private int insertMedianKeyToParent(int medianKey, BTreeNode parentBTreeNode) {
        int indexToAddMedianKey = 0;

        for (int i = 0; i <= parentBTreeNode.keys.size(); i++) {
            if (i == parentBTreeNode.keys.size()) {
                parentBTreeNode.keys.add(medianKey);
                indexToAddMedianKey = i;
                break;
            }
            if (medianKey < parentBTreeNode.keys.get(i)) {
                parentBTreeNode.keys.add(i, medianKey);
                indexToAddMedianKey = i;
                break;
            }
        }
        if (!parentBTreeNode.children.isEmpty()) {
            for (int i = parentBTreeNode.keys.size(); i >= indexToAddMedianKey + 2; i--) {
                parentBTreeNode.children.put(i, parentBTreeNode.children.get(i - 1));
            }
            parentBTreeNode.children.remove(indexToAddMedianKey);
            parentBTreeNode.children.remove(indexToAddMedianKey + 1);
        }
        return indexToAddMedianKey;
    }

    private int getIndexChildToInsert(int key) {
        int index = Collections.binarySearch(keys, key);
        return index < 0 ? -index - 1 : index;
    }

    private boolean isLeaf() {
        return children.isEmpty();
    }

    private void setParent(BTreeNode parent) {
        this.parent = parent;
        height = parent.height + 1;
    }

    private String printTree(String prefix, boolean isTail) {
        StringBuilder builder = new StringBuilder();

        builder.append(prefix).append(isTail ? "└── " : "├── ").append(keys.toString()).append("\n");
        for (int i = 0; i < children.size() - 1; i++) {
            builder.append(children.get(i).printTree(prefix + (isTail ? "    " : "│   "), false));
        }
        if (!children.isEmpty()) {
            builder.append(children.get(children.size() - 1).printTree(prefix + (isTail ? "    " : "│   "), true));
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return printTree("", true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BTreeNode other = (BTreeNode) obj;
        return Objects.equals(keys, other.keys);
    }

}
