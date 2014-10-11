package za.co.jethromuller.ctst.pathfinding;


import com.badlogic.gdx.math.Circle;

public class Waypoint {
    private float xpoint;
    private float ypoint;

    public Waypoint(float x, float y) {
        xpoint = x;
        ypoint = y;
    }

    public float[] getAsComponents(float x2, float y2, float distance) {
        float xpart = this.getX() - x2;
        float ypart = this.getY() - y2;
        float angle = 0;

        if (xpart != 0) {
            angle = ((float) Math.atan(ypart / xpart));
        } else {
            angle = ((float) (Math.PI / 2));
        }

        float xComp = ((float) Math.abs(distance * (Math.cos(angle))));
        xComp = (xpart >= 0) ? xComp: -xComp;
        float yComp = ((float) Math.abs(distance * (Math.sin(angle))));
        yComp = (ypart >= 0) ? yComp: -yComp;

        return new float[] {xComp, yComp};
    }

    public float getX() {
        return xpoint;
    }

    public float getY() {
        return ypoint;
    }

    public Circle getCircle() {
        return new Circle(getX(), getY(), 5);
    }

    public String toString() {
        String toString = "";

        toString += "(X: " + getX() + ", Y: " + getY() + ")";

        return toString;
    }
}
