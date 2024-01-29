package sgbd.tp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class BTreeTest {
    List<String> pages;

    @BeforeEach
    void setUp() {
        pages = Stream
                .of(10, 15, 30, 27)
                .map(String::valueOf)
                .toList();
    }

    @Test
    void test_btree() {
        BTree btree = new BTree(5);
        pages.forEach(btree::insert);
        System.out.println(btree);
    }
}