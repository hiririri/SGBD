package sgbd.tp;

import java.util.*;

public sealed interface BufferManagerInterface {
    void accessPage(String pageId);

    record LRU(int capacity) implements BufferManagerInterface {
        private static Map<String, Integer> buffer;
        private static int misses;

        public LRU {
            buffer = new LinkedHashMap<>() {
                protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                    return size() > capacity;
                }
            };
        }

        @Override
        public void accessPage(String pageId) {
            if (!buffer.containsKey(pageId)) {
                misses++;
            }
            buffer.remove(pageId); // Remove and re-insert to update access order
            buffer.put(pageId, 0); // The value is not used, so we can just store 0
        }

        public int misses() {
            return misses;
        }
    }

    record FIFO(int capacity) implements BufferManagerInterface {
        private static Queue<String> buffer;
        private static int misses;

        public FIFO {
            buffer = new LinkedList<>();
        }

        @Override
        public void accessPage(String pageId) {
            if (!buffer.contains(pageId)) {
                misses++;
                if (buffer.size() == capacity) {
                    buffer.remove();
                }
                buffer.add(pageId);
            }
        }

        public int misses() {
            return misses;
        }
    }

    record Clock(int capacity) implements BufferManagerInterface {
        private class Node {
            String pageId;
            boolean accessed;
            Node next;

            public Node(String pageId) {
                this.pageId = pageId;
                this.accessed = false;
            }
        }

        private static Node head;
        private static Node hand;
        private static int misses;
        private static int size;

        @Override
        public void accessPage(String pageId) {
            // TODO: Implement
        }

        private void insert(String pageId) {

        }

        private Node find(String pageId) {
            return null;
        }

        public int misses() {
            return misses;
        }
    }
}
