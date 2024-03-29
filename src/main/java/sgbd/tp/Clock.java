package sgbd.tp;

public non-sealed class Clock implements Strategy {
    private class Node {
        String pageId;
        boolean accessed;
        Node next;

        public Node(String pageId) {
            this.pageId = pageId;
            this.accessed = false;
        }
    }

    private Node head;
    private Node pointer;
    private int misses;
    private int size;
    private final int capacity;

    public Clock(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.misses = 0;
        this.head = null;
        this.pointer = null;
    }

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
                while (pointer.accessed) {
                    // Clear the accessed bit and move the hand
                    pointer.accessed = false;
                    pointer = pointer.next;
                }
                // Replace the page the hand is pointing to
                pointer.pageId = pageId;
                pointer.accessed = true;
            }
        }
//            printBuffer(pageId);
    }

    private void insert(String pageId) {
        Clock.Node node = new Clock.Node(pageId);
        node.accessed = true;
        if (head == null) {
            node.next = node; // Point to itself
            head = node;
            pointer = node;
        } else {
            // Insert the new node to the end of the circular list
            Clock.Node tail = head;
            while (tail.next != head) {
                tail = tail.next;
            }
            tail.next = node;
            node.next = head; // Form a circular list
        }
        size++;
    }

    private Clock.Node find(String pageId) {
        if (head == null) {
            return null;
        }
        Clock.Node node = head;
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
        Clock.Node node = head;
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
