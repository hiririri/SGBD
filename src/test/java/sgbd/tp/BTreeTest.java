package sgbd.tp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sgbd.tp.BTree;

import java.util.List;
import java.util.stream.Stream;

class BTreeTest {
    BTree bTree;

    @Test
    void insertNonNull() {
        bTree.insert(13);
    }

    @Test
    void split() {
        bTree.insert(13);
        bTree.insert(14);
    }

    @Test
    void test() {
        BTree tree = new BTree();
        tree.insert(10);
        tree.insert(15);
        tree.insert(30);
        tree.insert(27);
        tree.insert(35);
    }

    @Test
    void test2() {
        BTree tree = new BTree();
        List<Integer> keys = List.of(50, 55, 66, 68, 70, 71, 72, 73, 79, 81, 85, 90, 95);

        keys.forEach(tree::insert);
    }

    @Test
    void test3() {
        BTree tree = new BTree();
        List<Integer> keys = Stream.of(10, 15, 30, 27, 35, 40, 45, 37, 20, 50, 55, 46, 71, 66, 74, 85, 90, 79, 78, 95, 25, 81, 68, 60, 65).toList();

        keys.forEach(tree::insert);
    }
}