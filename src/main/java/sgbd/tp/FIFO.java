package sgbd.tp;

import java.util.LinkedList;
import java.util.Queue;

public non-sealed class FIFO implements Strategy {
    private Queue<String> buffer;
    private int misses;
    private final int capacity;

    public FIFO(int capacity) {
        buffer = new LinkedList<>();
        this.capacity = capacity;
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
