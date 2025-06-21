package entity.util;

import entity.Item.Jeep;
import entity.Item.Road;
import org.example.MapSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class jeepaAndVisitorHandlerTest {
    private MapSpace map;
    private jeepAndVisitorHandler handler;

    @BeforeEach
    void setUp() {
        map = mock(MapSpace.class);
        when(map.getGrid()).thenReturn(new Point[][]{
                {new Point(0, 0), new Point(0, 1)},
                {new Point(1, 0), new Point(1, 1)}
        });
        handler = new jeepAndVisitorHandler(map);
        // Reset static fields
        setField(jeepAndVisitorHandler.class, "roads", new Road[2][2]);
        setField(jeepAndVisitorHandler.class, "start", null);
    }

    @Test
    void testAddRoadAndRoadNum() {
        Road r = mock(Road.class);
        r.row = 0; r.col = 0;
        handler.addRoad(r);
        assertEquals(1, jeepAndVisitorHandler.roadNum());
        Road r2 = mock(Road.class);
        r2.row = 1; r2.col = 1;
        handler.addRoad(r2);
        assertEquals(2, jeepAndVisitorHandler.roadNum());
    }

    @Test
    void testHasRoad() {
        Road r = mock(Road.class);
        r.row = 0; r.col = 0;
        setField(jeepAndVisitorHandler.class, "start", r);
        Road[][] roads = new Road[2][2];
        roads[1][0] = mock(Road.class);
        setField(jeepAndVisitorHandler.class, "roads", roads);
        assertTrue(jeepAndVisitorHandler.hasRoad());
    }

    @Test
    void testAddJeepSetsStartAndPosition() {
        Jeep jeep = mock(Jeep.class);
        Road start = mock(Road.class);
        when(start.getX()).thenReturn(5);
        when(start.getY()).thenReturn(10);
        setField(jeepAndVisitorHandler.class, "start", start);
        handler.addJeep(jeep);
        // The addJeep method sets fields directly, not via setX/setY, so check fields instead
        assertEquals(5, jeep.x);
        assertEquals(10, jeep.y);
        assertEquals(start, jeep.currRoad);
        assertFalse(jeep.inReverse);
    }

    @Test
    void testAddVisitorAndRemoveVisitor() {
        int initial = getIntField(handler, "visitors");
        handler.addVisitor(5);
        assertEquals(initial + 5, getIntField(handler, "visitors"));
        handler.removeVisitor(3);
        assertEquals(initial + 2, getIntField(handler, "visitors"));
        handler.removeVisitor(initial + 2); // Remove all remaining visitors
        assertEquals(0, getIntField(handler, "visitors"));
    }

    @Test
    void testPickupVisitors() {
        Jeep jeep = mock(Jeep.class);
        jeep.maxPassengerCount = 10;
        setIntField(handler, "visitors", 8);
        handler.pickupVisitors(jeep);
        assertEquals(8, jeep.passengers);
        assertEquals(0, getIntField(handler, "visitors"));
    }

    // Reflection helpers for static and instance fields
    private void setField(Class<?> clazz, String field, Object value) {
        try {
            var f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private int getIntField(Object obj, String field) {
        try {
            var f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void setIntField(Object obj, String field, int value) {
        try {
            var f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.setInt(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
