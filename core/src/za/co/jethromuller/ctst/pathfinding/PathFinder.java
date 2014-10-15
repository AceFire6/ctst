package za.co.jethromuller.ctst.pathfinding;


import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import za.co.jethromuller.ctst.Level;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * The class that does all the heavy-lifting with regards to the A* pathfinding.
 *
 * The pathfinder object of a level generates a tileMap which is essentially the search graph.
 * Whenever the enemy objects need to find a shortest path to the player,
 * they use this object to generate one.
 *
 * The found path is returned as a stack of Waypoints.
 */
public class PathFinder {

    private Tile[][] tileMap;
    private int columns;
    private int rows;
    private Level level;
    private int gridSize = 20;


    public PathFinder(Level level) {
        rows = level.getRows();
        columns = level.getCols();
        this.level = level;
        tileMap = new Tile[rows * 2][columns * 2];
        tileMap = populateTileMap();
    }

    private Tile[][] populateTileMap() {
        for (int i = 0; i < rows * 2; i++) {
            for (int j = 0; j < columns * 2; j++) {
                tileMap[i][j] = new Tile(gridSize * j, (gridSize * i), gridSize, gridSize, j, i);
                if (isCollision((gridSize * j), (gridSize * i))) {
                    System.out.print("X ");
                } else {
                    tileMap[i][j].setTraversable();
                    System.out.print(". ");
                }
            }
            System.out.println("");
        }
//        System.out.println();
//        printTileMap();
        return tileMap;
    }

    /**
     * Prints a text representation of the tileMap, showing all the tiles and whether or not they
     * are marked as traversable.
     */
    public void printTileMap() {
        for (Tile[] tileRow : tileMap) {
            for (Tile tile : tileRow) {
                if (tile.getSteps() != 0) {
                    System.out.print("(" + tile.getSteps() + ")");
                } else if (tile.isTraversable()) {
                    System.out.print(". ");
                } else {
                    System.out.print("X ");
                }
            }
            System.out.println("");
        }
    }


    /**
     * Checks to see if a tile is traversable or not.
     * @param newX  The x-coordinate to start the check at.
     * @param newY  The y-coordinate to start the check at.
     * @return boolean stating whether or not there is a collision at the given coordinates.
     */
    public boolean isCollision(float newX, float newY) {
        Rectangle newRectangle = new Rectangle(newX, newY, gridSize, gridSize);

        if (Intersector.overlaps(level.getLightSource(), newRectangle)) {
            return true;
        }

        for (Object entity : level.getObstacles()) {
            if (entity instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) entity).getRectangle();
                if (Intersector.overlaps(newRectangle, rect)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Generates the path from the startWaypoint to the endWaypoint.
     * @param startWaypoint Waypoint object representing the starting point of the path.
     * @param endWaypoint   Waypoint object representing the ending point of the path.
     * @return  A Stack<Waypoint> that has in it the entire path if there is one,
     * otherwise it returns null.
     */
    public Stack<Waypoint> getPath(Waypoint startWaypoint, Waypoint endWaypoint) {
        for (Tile[] tiles : tileMap) {
            for (Tile tile : tiles) {
                tile.reset();
            }
        }
        PriorityQueue<Tile> openList = new PriorityQueue<>();
        int xIndex = (Math.round((startWaypoint.getX()) / gridSize));
        int yIndex = (Math.round((startWaypoint.getY()) / gridSize));
        openList.add(tileMap[yIndex][xIndex]);
        System.out.println("START TILE: " + tileMap[yIndex][xIndex].getAsWaypoint());

        ArrayList<Tile> closedList = new ArrayList<>();
        Tile currentTile;

        int xPlayer = (Math.round((endWaypoint.getX() + 10) / gridSize));
        int yPlayer = (Math.round((endWaypoint.getY() + 10)/ gridSize));
        Tile endTile = tileMap[yPlayer][xPlayer];
        System.out.println("END TILE: " + endTile.getAsWaypoint());

        while (!openList.isEmpty()) {
            currentTile = openList.poll();

            if (currentTile.equals(endTile)) {
                return addWaypointsToStack(currentTile);
            } else {
                closedList.add(currentTile);
                for (Tile neighbour : getAdjacentTiles(currentTile)) {
                    if (closedList.contains(neighbour)) {
                        continue;
                    }
                    int tenativeSteps = currentTile.getSteps() + 1;

                    if (!openList.contains(neighbour) ||
                        (tenativeSteps < neighbour.getSteps())) {
                        neighbour.setParent(currentTile);
                        neighbour.setSteps(tenativeSteps);
                        neighbour.setHeuristic(generateHeuristic(neighbour, endTile));
                        if (!openList.contains(neighbour)) {
                            openList.add(neighbour);
                        }
                    }
                }
            }
//            printTileMap();
        }
        System.out.println("NO PATH BROSEPH");
        return null;
    }

    /**
     * Gets the tiles adjacent to the given tile.
     * @param currentTile  The tile whose adjacents are to be found.
     * @return All the tiles that are adjacent to currentTile.
     */
    private ArrayList<Tile> getAdjacentTiles(Tile currentTile) {
        ArrayList<Tile> adjacents = new ArrayList<>();
//        int yIndex = currentTile.getyIndex();
//        int xIndex = currentTile.getxIndex();
//
//        int[][] positions = new int[][] {new int[] {xIndex, yIndex + 1}, new int[] {xIndex, yIndex - 1},
//                                  new int[] {xIndex - 1, yIndex}, new int[] {xIndex + 1, yIndex}};

//        for (int[] position : positions) {
//            if (tileMap[position[1]][position[0]] != null &&
//                tileMap[position[1]][position[0]].isTraversable() &&
//                !tileMap[position[1]][position[0]].equals(currentTile)) {
//                adjacents.add(tileMap[position[1]][position[0]]);
//            }
//        }
        int yIndex = currentTile.getyIndex();
        int yStart = ((yIndex - 1) > 0) ? yIndex - 1: 0;
        int yEnd = ((yIndex + 1) < tileMap.length) ? yIndex + 1: tileMap.length - 1;
        for (int y = yStart; y <= yEnd; y++) {
            int xIndex = currentTile.getxIndex();
            int xStart = ((xIndex - 1) > 0) ? xIndex - 1: 0;
            int xEnd = ((xIndex + 1) < tileMap[y].length) ? xIndex + 1: tileMap[y].length - 1;
            for (int x = xStart; x <= xEnd; x++) {
                if (tileMap[y][x].isTraversable() && !tileMap[y][x].equals(currentTile)) {
                    adjacents.add(tileMap[y][x]);
                }
            }
        }
        return adjacents;
    }

    /**
     * Generates the heuristic of the start Tile.
     * The heuristic used is the deltaMax heuristic.
     * @param start The tile whose heuristic is being generated.
     * @param end  The target tile.
     * @return  The heuristic value to be assigned to the start tile.
     */
    private int generateHeuristic(Tile start, Tile end) {
        float deltaX = Math.abs(start.getX() - end.getX());
        float deltaY = Math.abs(start.getY() - end.getY());

        return ((int) Math.max(deltaX, deltaY));
    }

    /**
     * Follows the tile's parents until the parent reached is null.
     * Each tile is gotten in waypoint form and added to the waypoint stack to be returned to the
     * the enemy object.
     * @param currentTile The end tile whose lineage you trace to find the path.
     * @return The generated Stack of waypoints.
     */
    private Stack<Waypoint> addWaypointsToStack(Tile currentTile) {
        Stack<Waypoint> pathStack = new Stack<>();
        Tile tile = currentTile.copy();
        while (tile != null) {
            pathStack.add(tile.getAsWaypoint());
            if (tile.getParent() != null) {
                tile = tile.getParent().copy();
            } else {
                break;
            }
        }
        return pathStack;
    }
}
