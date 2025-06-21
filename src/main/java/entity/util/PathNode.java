package entity.util;

/**
 * Interface representing a node in a pathfinding algorithm.
 * It is used to mark classes that can be part of a path.
 * PathHandler will use this interface to identify path nodes,
 * and provide instructions to Dynamic entities to follow the path created by the nodes
 */
public interface PathNode {


    int getId();
//
//    PathNode nextNode();
//    PathNode prevNode();



}
