package sgbd.tp;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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
            Node node = find(pageId);
            if (node != null) {
                // Page is in memory, mark it as accessed
                node.accessed = true;
            } else {
                // Page is not in memory, need to replace a page
                misses++;
                if (size < capacity) {
                    // Still have room in memory, just add the page
                    insert(pageId);
                } else {
                    // No room in memory, need to replace a page
                    while (hand.accessed) {
                        // Clear the accessed bit and move the hand
                        hand.accessed = false;
                        hand = hand.next;
                    }
                    // Replace the page the hand is pointing to
                    hand.pageId = pageId;
                    hand.accessed = true;
                }
            }
            printBuffer(pageId);
        }

        private void insert(String pageId) {
            Node node = new Node(pageId);
            node.accessed = true;
            if (head == null) {
                node.next = node; // Point to itself
                head = node;
                hand = node;
            } else {
                // Insert the new node to the end of the circular list
                Node tail = head;
                while (tail.next != head) {
                    tail = tail.next;
                }
                tail.next = node;
                node.next = head; // Form a circular list
            }
            size++;
        }

        private Node find(String pageId) {
            if (head == null) {
                return null;
            }
            Node node = head;
            do {
                if (node.pageId.equals(pageId)) {
                    return node;
                }
                node = node.next;
            } while (node != head);
            return null;
        }

        private void printBuffer(String pageId) {
            if (head == null) {
                System.out.println("Buffer is empty");
                return;
            }
            StringBuilder bufferStr = new StringBuilder();
            Node node = head;
            do {
                bufferStr.append("[");
                bufferStr.append(node.pageId);
                bufferStr.append("]");
                bufferStr.append("-");
                bufferStr.append("(");
                bufferStr.append(node.accessed ? 1 : 0);
                bufferStr.append(")");
                if (node.next != head) {
                    bufferStr.append(", ");
                }
                node = node.next;
            } while (node != head);
            System.out.println("Buffer " + pageId + " -> : " + bufferStr);
        }

        public int misses() {
            return misses;
        }
    }
}
