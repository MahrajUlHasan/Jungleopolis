package entity.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PathHandlerTest {
    private PathHandler handler;
    private PathNode node1, node2, node3;

    @BeforeEach
    void setUp() {
        handler = new PathHandler();
        node1 = mock(PathNode.class);
        node2 = mock(PathNode.class);
        node3 = mock(PathNode.class);
    }

    @Test
    void testAddNodeAndGetNodes() {
        handler.addNode(node1);
        handler.addNode(node2);
        List<PathNode> nodes = handler.getNodes();
        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));
        // Should not allow duplicates
        handler.addNode(node1);
        assertEquals(2, handler.getNodeCount());
    }

    @Test
    void testRemoveNode() {
        handler.addNode(node1);
        handler.addNode(node2);
        handler.addNode(node3);
        handler.removeNode(node2);
        assertEquals(2, handler.getNodeCount());
        assertFalse(handler.getNodes().contains(node2));
        // Remove current node
        handler.removeNode(node1);
        assertEquals(1, handler.getNodeCount());
        assertEquals(node3, handler.getCurrent());
        // Remove last node
        handler.removeNode(node3);
        assertEquals(0, handler.getNodeCount());
        assertNull(handler.getCurrent());
    }

    @Test
    void testNextNodeAndCircular() {
        handler.addNode(node1);
        handler.addNode(node2);
        handler.addNode(node3);
        assertEquals(node1, handler.getCurrent());
        assertEquals(node2, handler.nextNode());
        assertEquals(node3, handler.nextNode());
        assertEquals(node1, handler.nextNode()); // Circular
    }

    @Test
    void testGetCurrentAndHasNodes() {
        assertNull(handler.getCurrent());
        assertFalse(handler.hasNodes());
        handler.addNode(node1);
        assertTrue(handler.hasNodes());
        assertEquals(node1, handler.getCurrent());
    }

    @Test
    void testGetNodeCount() {
        assertEquals(0, handler.getNodeCount());
        handler.addNode(node1);
        assertEquals(1, handler.getNodeCount());
        handler.addNode(node2);
        assertEquals(2, handler.getNodeCount());
    }
}
