package sgbd.tp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class StrategyTest {
    List<String> seq_fifo;
    List<String> seq_lru;
    List<String> seq_clock;
    Map<String, Integer> perf;
    private static final int CAPACITY = 4;

    @BeforeEach
    void setUp() {
        seq_fifo = Stream.of(6, 4, 5, 9, 4, 6, 0, 5, 0, 9, 0, 2, 6, 4, 7, 2, 4, 5, 7, 1).map(String::valueOf).toList();
        seq_lru = Stream.of(1, 5, 3, 7, 5, 5, 1, 7, 3, 8, 1, 9, 2, 3, 1, 5, 3, 8, 6, 6).map(String::valueOf).toList();
        seq_clock = Stream.of(0, 9, 9, 8, 1, 5, 0, 8, 0, 4, 9, 2, 8, 8, 0, 4, 6, 0, 3, 7).map(String::valueOf).toList();
    }

    @Test
    void test_fifo() {
        System.out.println(seq_fifo);
        perf = new HashMap<>();

        run(new LRU(CAPACITY), seq_fifo);
        run(new FIFO(CAPACITY), seq_fifo);
        run(new Clock(CAPACITY), seq_fifo);

        evaluate();
        System.out.println("In this sequence, there are instances where pages are reused, but the reuse is not immediate or short-term. Therefore, FIFO is the best strategy.");
    }

    @Test
    void test_lru() {
        System.out.println(seq_lru);
        perf = new HashMap<>();

        run(new LRU(CAPACITY), seq_lru);
        run(new FIFO(CAPACITY), seq_lru);
        run(new Clock(CAPACITY), seq_lru);

        evaluate();
        System.out.println("This sequence has several instances where pages are reused shortly after their previous use. Therefore, LRU is the best strategy.");
    }

    @Test
    void test_clock() {
        System.out.println(seq_clock);
        perf = new HashMap<>();
        run(new LRU(CAPACITY), seq_clock);
        run(new FIFO(CAPACITY), seq_clock);
        run(new Clock(CAPACITY), seq_clock);

        evaluate();
        System.out.println("The sequence includes both frequently and infrequently accessed pages. Therefore, Clock is the best strategy.");
    }

    private void run(Strategy manager, List<String> sequence) {
        sequence.forEach(manager::accessPage);
        switch (manager) {
            case LRU lru -> perf.put("LRU", lru.misses());
            case FIFO fifo -> perf.put("FIFO", fifo.misses());
            case Clock clock -> perf.put("Clock", clock.misses());
        }
    }

    private void evaluate() {
        perf.forEach((key, value) -> System.out.println(key + " : " + value));
        perf.entrySet().stream().min(Map.Entry.comparingByValue()).ifPresentOrElse(
                entry -> System.out.println("The best strategy is " + entry.getKey() + " with " + entry.getValue() + " misses."),
                () -> System.out.println("All strategies are the same performance.")
        );
    }
}