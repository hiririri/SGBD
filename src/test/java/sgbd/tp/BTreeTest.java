package sgbd.tp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class BTreeTest {
    BTree bTree;

    @BeforeEach
    void setUp() {
        bTree = new BTree(5);
    }

    @Test
    void test1() {
        List<Integer> keys = List.of(50, 55, 66, 68, 70, 71, 72, 73, 79, 81, 85, 90, 95);

        keys.forEach(bTree::insert);
    }

    @Test
    void test2() {
        List<Integer> keys = Stream.of(10, 15, 30, 27, 35, 40, 45, 37, 20, 50, 55, 46, 71, 66, 74,
                        85, 90, 79, 78, 95, 25, 81, 68, 60, 65)
                .sorted()
                .toList();

        keys.forEach(bTree::insert);
    }
}