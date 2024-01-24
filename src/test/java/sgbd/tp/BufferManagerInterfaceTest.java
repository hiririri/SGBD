package sgbd.tp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BufferManagerInterfaceTest {
    List<String> pages;
    private static final int CAPACITY = 4;
    private static final int PAGES_MISSED_LRU = 10;
    private static final int PAGES_MISSED_FIFO = 14;
    private static final int PAGES_MISSED_CLOCK = 12;

    @BeforeEach
    void setUp() {
        pages = List.of("1", "2", "3", "4", "2", "1", "5", "6", "2", "1", "2", "3", "7", "6", "3", "2", "1", "2", "3", "6");
    }

    @Test
    void test_lru() {
        run(new BufferManagerInterface.LRU(CAPACITY));
    }

    @Test
    void test_fifo() {
        run(new BufferManagerInterface.FIFO(CAPACITY));
    }

    @Test
    void test_clock() {
        run(new BufferManagerInterface.Clock(CAPACITY));
    }

    private void run(BufferManagerInterface manager) {
        pages.forEach(manager::accessPage);
        switch (manager) {
            case BufferManagerInterface.LRU lru -> assertEquals(PAGES_MISSED_LRU, lru.misses());
            case BufferManagerInterface.FIFO fifo -> assertEquals(PAGES_MISSED_FIFO, fifo.misses());
            case BufferManagerInterface.Clock clock -> assertEquals(PAGES_MISSED_CLOCK, clock.misses());
        }
    }
}