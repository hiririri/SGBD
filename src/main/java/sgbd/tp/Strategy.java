package sgbd.tp;

public sealed interface Strategy permits Clock, FIFO, LRU {
    void accessPage(String pageId);
}
