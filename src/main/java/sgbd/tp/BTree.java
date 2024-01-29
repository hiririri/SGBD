package sgbd.tp;

import java.util.*;

public class BTree {
    private static int order;
    private List<Leaf> leaves;

    public BTree(int order) {
        BTree.order = order;
        leaves = new ArrayList<>();
    }

    public void insert(String pageId) {
        if (leaves.stream().anyMatch(leaf -> !leaf.contains(pageId))) {
            return;
        }
    }

    @Override
    public String toString() {
        return leaves.toString();
    }

    private record Leaf(LinkedHashSet<String> values) {
        public void add(String pageId) {
            values.add(pageId);
        }

        public boolean contains(String pageId) {
            return values.contains(pageId);
        }

        public boolean isFull() {
            return values.size() == order;
        }

        public boolean isBetween(String pageId) {
            return values.getFirst().compareTo(pageId) < 0 && values.getLast().compareTo(pageId) > 0;
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }
}
