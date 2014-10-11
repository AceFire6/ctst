package za.co.jethromuller.ctst.pathfinding;


import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import za.co.jethromuller.ctst.Level;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

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

    public void printTileMap() {
        for (Tile[] aTileMap : tileMap) {
            for (Tile anATileMap : aTileMap) {
                if (anATileMap.getSteps() != 0) {
                    System.out.print("(" + anATileMap.getSteps() + ")");
                } else if (anATileMap.isTraversable()) {
                    System.out.print(". ");
                } else {
                    System.out.print("X ");
                }
            }
            System.out.println("");
        }
    }

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

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public Stack<Waypoint> getPath(Waypoint startWaypoint, Waypoint endWaypoint) {
        for (Tile[] tiles : tileMap) {
            for (Tile tile : tiles) {
                tile.reset();
            }
        }
        PriorityQueue<Tile> openList = new PriorityQueue<>();
        int xIndex = ((int) ((startWaypoint.getX() + 10) / gridSize));
        int yIndex = ((int) ((startWaypoint.getY() + 10) / gridSize));
        openList.add(tileMap[yIndex][xIndex]);
        System.out.println("START TILE: " + tileMap[yIndex][xIndex].getAsWaypoint());

        ArrayList<Tile> closedList = new ArrayList<>();
        Tile currentTile;

        int xPlayer = ((int) ((endWaypoint.getX() + 10) / gridSize));
        int yPlayer = ((int) ((endWaypoint.getY() + 10)/ gridSize));
        Tile endTile = tileMap[yPlayer][xPlayer];
        System.out.println("END TILE: " + endTile.getAsWaypoint());

        while (!openList.isEmpty() || (closedList.size() < 20)) {
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

    private ArrayList<Tile> getAdjacentTiles(Tile currentTile) {
        ArrayList<Tile> adjacents = new ArrayList<>();
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

    private int generateHeuristic(Tile start, Tile end) {
        float deltaX = Math.abs(start.getX() - end.getX());
        float deltaY = Math.abs(start.getY() - end.getY());

        return ((int) Math.max(deltaX, deltaY));
    }

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
