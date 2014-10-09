package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import za.co.jethromuller.ctst.Level;
import za.co.jethromuller.ctst.pathfinding.Waypoint;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class Enemy extends Entity {

    public Circle visionRange;
    public Circle hearingRange;
    public Circle smellRange;

    private int visionRadius = 80;
    private int smellRadius = 15;
    private int hearingRadius = 130;

    private long pastTime;

    private float deltaX = 0;
    private float deltaY = 0;

    private boolean seen;
    private Player player;

    private Random randTime;

    private long waypointTimer;

    private Queue<Waypoint> waypoints;
    private Waypoint lastSeenPosition;

    public Enemy(Level level, float x, float y) {
        super(level, x, y, "entities/enemy/enemy_down.png");
        visionRange = new Circle(x + xOffset, y + yOffset, visionRadius);
        hearingRange = new Circle(x + xOffset, y + yOffset, hearingRadius);
        smellRange = new Circle(x + xOffset, y + yOffset, smellRadius);

        pastTime = 0;
        randTime = new Random();

        setCollidable(true);
        waypoints = new PriorityQueue<>();
    }

    @Override
    public void update() {
        if (player == null) {
            player = currentLevel.getPlayer();
        }

        float speed = 0.9F;

        if (Intersector.overlaps(player.getCircleBounds(), getBoundingRectangle())) {
            currentLevel.lose();
        }

        if ((Intersector.overlaps(player.getCircleBounds(), visionRange) && !currentLevel.inShadow(player)) ||
            (player.isMoving() && !player.isSneaking() && Intersector.overlaps(hearingRange, player.getNoiseMarker()))
                || Intersector.overlaps(smellRange, player.getCircleBounds())) {
            if (!seen) {
                currentLevel.seePlayer();
                seen = true;
            }
            visionRadius = 190;
            speed = 1.1F;

            if ((waypointTimer % 0.1) == 0) {
                moveTo(new Waypoint(player.getX(), player.getY()));
            }

            if (waypointTimer > 0.3) {
                waypointTimer = 0;
                lastSeenPosition = new Waypoint(player.getX(), player.getY());
            } else {
                waypointTimer += Gdx.graphics.getDeltaTime();
            }
        } else if (seen && (waypoints.size() != 0)) {
            moveTo(lastSeenPosition);
        } else {
            seen = false;
            visionRadius = 80;
            if ((System.currentTimeMillis() - pastTime) > (randTime.nextInt(2000) + 2000)) {
                Random randSign = new Random();
                Random randFloat = new Random();
                deltaX = randFloat.nextFloat();
                deltaX = randSign.nextBoolean() ? -deltaX: deltaX;
                deltaY = randFloat.nextFloat();
                deltaY = randSign.nextBoolean() ? -deltaY: deltaY;
                pastTime = System.currentTimeMillis();
            }
        }

        setTexture();

        if (deltaX != 0 || deltaY != 0) {
            if ((Math.abs(deltaX) == speed) && (Math.abs(deltaY) == speed)) {
                deltaX *= 0.725;
                deltaY *= 0.725;
            }
            super.collisionDetection(getX() + deltaX, getY());
            super.collisionDetection(getX(), getY() + deltaY);

            visionRange.set(getX() + xOffset, getY() + yOffset, visionRadius);
            hearingRange.setPosition(getX() + xOffset, getY() + yOffset);
            smellRange.setPosition(getX() + xOffset, getY() + yOffset);

            currentLevel.updatePositionInGrid(this);
        }
    }

    private void moveTo(Waypoint waypoint) {
        waypoints = currentLevel.pathFinder.getPath(waypoint);
        System.out.println(waypoints);
        System.out.println(waypoint);
        float[] coords = waypoint.getAsComponents(getX(), getY());
        deltaX = coords[0];
        deltaY = coords[1];
    }

    public void setTexture() {
        if (deltaY > 0) { // up
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_upRight());
            } else if (deltaX == 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_up());
            } else {
                setTexture(currentLevel.getGame().textureController.getEnemy_upLeft());
            }
        } else if  (deltaY == 0) {  // left or right
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_right());
            } else if (deltaX == 0) {
                return;
            } else {
                setTexture(currentLevel.getGame().textureController.getEnemy_left());
            }
        } else { // down
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_downRight());
            } else if (deltaX == 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_down());
            } else {
                setTexture(currentLevel.getGame().textureController.getEnemy_downLeft());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Enemy)) {
            return false;
        }

        Enemy enemy = (Enemy) obj;

        return (enemy.getX() == getX()) && (enemy.getY() == getY());
    }
}
