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
            Node next, prev;

            public Node(String pageId) {
                this.pageId = pageId;
                this.accessed = false;
            }
        }

        private static Map<String, Node> buffer;
        private static Node head, tail;
        private static int size;
        private static int misses;

        public Clock {
            buffer = new HashMap<>();
            size = 0;
        }

        @Override
        public void accessPage(String pageId) {
            if (!buffer.containsKey(pageId)) {
                misses++; // Increment misses if the page is not in the buffer
                if (size == capacity) {
                    replacePage(); // Replace a page if the buffer is full
                }
                addPage(pageId); // Add the new page to the buffer
            } else {
                Node pageNode = buffer.get(pageId);
                pageNode.accessed = true; // Mark the page as accessed
            }
        }

        private void replacePage() {
            while (head.accessed) {
                head.accessed = false; // Reset the flag
                moveToEnd(head); // Move the page to the end of the list
            }
            // Remove the least recently used (unaccessed) page
            Node leastRecentlyUsed = head;
            removePage(leastRecentlyUsed);
        }

        private void addPage(String pageId) {
            Node newNode = new Node(pageId);
            buffer.put(pageId, newNode);
            if (size == 0) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            size++;
        }

        private void removePage(Node node) {
            buffer.remove(node.pageId);
            if (head == tail) {
                head = tail = null;
            } else {
                head = head.next;
                head.prev = null;
            }
            size--;
        }

        private void moveToEnd(Node node) {
            if (tail == node) return;
            if (head == node) head = head.next;
            if (node.prev != null) node.prev.next = node.next;
            if (node.next != null) node.next.prev = node.prev;
            tail.next = node;
            node.prev = tail;
            node.next = null;
            tail = node;
        }

        public int misses() {
            return misses;
        }
    }
}
