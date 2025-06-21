package entity.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathNodeTest {
    static class DummyPathNode implements PathNode {
        private final int id;
        DummyPathNode(int id) { this.id = id; }
        @Override
        public int getId() { return id; }
    }

    @Test
    void testGetIdReturnsCorrectValue() {
        PathNode node = new DummyPathNode(42);
        assertEquals(42, node.getId());
    }

    @Test
    void testMultipleNodesHaveDifferentIds() {
        PathNode node1 = new DummyPathNode(1);
        PathNode node2 = new DummyPathNode(2);
        assertNotEquals(node1.getId(), node2.getId());
    }

    @Test
    void testImplementsPathNodeInterface() {
        PathNode node = new DummyPathNode(5);
        assertTrue(node instanceof PathNode);
    }
}
