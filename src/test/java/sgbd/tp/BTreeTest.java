package sgbd.tp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class BTreeTest {
    List<Integer> pages;

    @BeforeEach
    void setUp() {
        pages = Stream.of(10, 15, 30, 27, 35, 40, 45, 37, 20, 50, 55, 46, 71, 66, 74, 85, 90, 79, 78, 95, 25, 81, 68, 60, 65).toList();
    }

    @Test
    void test_btree() {
        BTree btree = new BTree(3);

        for (Integer page : pages) {
            btree.insert(page);
        }
        btree.traverse();
    }
}