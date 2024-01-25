package sgbd.tp;

import java.util.LinkedHashMap;
import java.util.Map;

public non-sealed class LRU implements Strategy {
    private Map<String, Integer> buffer;
    private int misses;
    private final int capacity;

    public LRU(int capacity) {
        buffer = new LinkedHashMap<>() {
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > capacity;
            }
        };
        this.capacity = capacity;
    }

    @Override
    public void accessPage(String pageId) {
        if (!buffer.containsKey(pageId)) {
            misses++;
        }
        buffer.remove(pageId);
        buffer.put(pageId, 0);
    }

    public int misses() {
        return misses;
    }
}
