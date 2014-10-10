package za.co.jethromuller.ctst.pathfinding;


public class Tile implements Comparable<Tile> {
    private float x;
    private float y;
    private int width;
    private int height;
    private Tile parent;

    private int xIndex;
    private int yIndex;

    private int steps; //g
    private int heuristic; //h
    //private int cost; // f = g + h

    private boolean traversable = false;

    public Tile(float x, float y, int width, int height, int xIndex, int yIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    public void setTraversable() {
        traversable = true;
    }

    public boolean isTraversable() {
        return traversable;
    }

    public int getxIndex() {
        return xIndex;
    }

    public int getyIndex() {
        return yIndex;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setParent(Tile parent) {
        this.parent = parent;
    }

    public Tile getParent() {
        return parent;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public int getCost() {
        return steps + heuristic;
    }

    public Waypoint getAsWaypoint() {
        return new Waypoint(x, y);
    }

    public String toString() {
        return "Steps: " + getSteps() +
               "\nHeuristic: " + getHeuristic() +
               "\nCost: " + getCost() +
                "\nX: " + x +
                "\nY: " + y;
    }

    @Override
    public int compareTo(Tile tile) {
        if (tile.getCost() > this.getCost()) {
            return -1;
        } else if (tile.getCost() < this.getCost()) {
            return 1;
        } else {
            if (tile.getHeuristic() > this.getHeuristic()) {
                return -1;
            } else if (tile.getHeuristic() < this.getHeuristic()) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tile tile = (Tile) o;

        if (height != tile.height) {
            return false;
        }
        if (heuristic != tile.heuristic) {
            return false;
        }
        if (steps != tile.steps) {
            return false;
        }
        if (traversable != tile.traversable) {
            return false;
        }
        if (width != tile.width) {
            return false;
        }
        if (Float.compare(tile.x, x) != 0) {
            return false;
        }
        if (xIndex != tile.xIndex) {
            return false;
        }
        if (Float.compare(tile.y, y) != 0) {
            return false;
        }
        if (yIndex != tile.yIndex) {
            return false;
        }
        if (parent != null ? !parent.equals(tile.parent) : tile.parent != null) {
            return false;
        }

        return true;
    }


    public Tile copy() {
        Tile newTile = new Tile(getX(), getY(), width, height, xIndex, yIndex);
        if (traversable) {
            newTile.setTraversable();
        }
        newTile.setHeuristic(heuristic);
        newTile.setSteps(steps);
        newTile.setParent(parent);

        return newTile;
    }

    public void reset() {
        setSteps(0);
        setParent(null);
        setHeuristic(0);
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + xIndex;
        result = 31 * result + yIndex;
        result = 31 * result + steps;
        result = 31 * result + heuristic;
        result = 31 * result + (traversable ? 1 : 0);
        return result;
    }
}
