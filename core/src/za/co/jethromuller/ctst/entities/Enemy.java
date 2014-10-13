package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import za.co.jethromuller.ctst.Level;
import za.co.jethromuller.ctst.pathfinding.Waypoint;

import java.util.Random;
import java.util.Stack;

/**
 * Enemy class that has all the behavioural information for the enemies.
 * It handles collisions and player detection.
 *
 * This class is meant to model enemies that are zombies. This should explain their erratic
 * movements and the way they follow the player.
 *
 * An enemy added to a level will randomly patrol until they can either, hear or see the player.
 * If the player is visible, they will head directly to the player.
 * If they player isn't visible, they will find the shortest path to the player and follow it.
 */
public class Enemy extends Entity {

    public Circle visionRange;
    public Circle hearingRange;
    public Circle smellRange;

    public Ray visionRay;

    private int visionRadius = 80;
    private int smellRadius = 15;
    private int hearingRadius = 130;

    private long pastTime;
    private long pathTime;

    private float deltaX = 0;
    private float deltaY = 0;

    private boolean seen;
    private Player player;

    private Random randTime;

    private Stack<Waypoint> waypoints;
    private Waypoint target;
    private boolean moving;
    private int collisionCounterX;
    private int collisionCounterY;
    private boolean wasCollision;
    private float collisionTimerX = 0F;
    private float collisionTimerY = 0F;


    /**
     * Creates an enemy object at the given coordinates that interacts with the specified level.
     * @param level Level object that holds the world information that is necessary for the enemy
     *              to act.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public Enemy(Level level, float x, float y) {
        super(level, x, y, "entities/enemy/enemy_down.png");
        visionRange = new Circle(x + xOffset, y + yOffset, visionRadius);
        hearingRange = new Circle(x + xOffset, y + yOffset, hearingRadius);
        smellRange = new Circle(x + xOffset, y + yOffset, smellRadius);

        pastTime = 0;
        randTime = new Random();

        pathTime = 0;

        moving = false;
        waypoints = new Stack<>();
    }

    /**
     * Returns the list of all the current waypoints.
     * Mainly used for drawing them.
     * @return Stack<Waypoint> object that has all the waypoints in the correct order so the
     * enemy moves towards the player.
     */
    public Stack<Waypoint> getWaypoints() {
        return waypoints;
    }

    @Override
    public void update() {
        if (player == null) { // Get the player object if there isn't already one.
            player = currentLevel.getPlayer();
        }

        // If the enemy reaches the player, the player loses.
        if (Intersector.overlaps(player.getCircleBounds(), getBoundingRectangle())) {
            currentLevel.getGame().musicController.playDeathSound(1F, 1F, 0F);
            currentLevel.lose();
        }

        // All the detection circles and conditions.
        if (canSeePlayer()) { // If the player can be seen. Move towards them.
            if (!seen) { // If the player gets seen, they lose points.
                currentLevel.seePlayer();
                seen = true;
            }
            visionRadius = 190;

            waypoints = null;
            moveTo(new Waypoint(player.getX(), player.getY()));
        } else if (player.isMoving() && !player.isSneaking() && Intersector.overlaps(hearingRange, player.getNoiseMarker())) {
            // If the player can't be seen and it's been 0.5s since the
            // last check, find a path.
            if ((System.currentTimeMillis() - pathTime) > 500) {
                pathTime = System.currentTimeMillis();
                waypoints = currentLevel.pathFinder.getPath(new Waypoint(getX(), getY()),
                                                            new Waypoint(player.getX(),
                                                                         player.getY()));

                if (waypoints != null && (waypoints.size() != 0)) {
                    moveTo(waypoints.pop());
                }
            }
        } else if (waypoints != null && (waypoints.size() != 0)) {
        // If there are waypoints to move to, move to them.
//            System.out.println(waypoints);
            if (!moving) {
                moving = true;
                target = waypoints.pop();
            }
            moveTo(target);
        } else { // Random Movement
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

        // If there is movement, update all the necessary circles and do collision detection.
        if (deltaX != 0 || deltaY != 0) {
            collisionDetection(getX() + deltaX, getY());
            collisionDetection(getX(), getY() + deltaY);

            visionRange.set(getX() + xOffset, getY() + yOffset, visionRadius);
            hearingRange.setPosition(getX() + xOffset, getY() + yOffset);
            smellRange.setPosition(getX() + xOffset, getY() + yOffset);

            currentLevel.updatePositionInGrid(this);
        }
    }

    /**
     * Uses ray casting to see if the player is visible.
     * If the player is in the shadows, the player is automatically not visible.
     *
     * It casts a ray towards the player and gets all intersected objects. Using these,
     * it creates vectors and determines if the player is the closest object or not.
     * @return  boolean saying whether or not the enemy can see the player.
     */
    private boolean canSeePlayer() {
        if (currentLevel.inShadow(player)) {
            return false;
        }
        if (Intersector.overlaps(player.getCircleBounds(), visionRange)) {
            Ray vision = new Ray(new Vector3(getX() + 10, getY() + 10, 0), new Vector3(
                    player.getX() + 10 - getX(), player.getY() + 10 - getY(), 0));

            visionRay = vision; // This is done so it can be rendered.
            Vector2 enemyPosition = new Vector2(getX(), getY());
            Object closestObject = null;
            double distance = Double.MAX_VALUE;

            for (RectangleMapObject rectangleMapObject : currentLevel.getObstacles()) {
                Rectangle currentRect = rectangleMapObject.getRectangle();

                Vector3 minimum = new Vector3(currentRect.getX(), currentRect.getY(), 0);
                Vector3 maximum = new Vector3(
                        currentRect.getX() + currentRect.width,
                        currentRect.getY() + currentRect.height, 0);

                BoundingBox collisionBox = new BoundingBox(minimum, maximum);

                if (Intersector.intersectRayBounds(vision, collisionBox, new Vector3())) {
                    Vector2 currentVector = new Vector2(currentRect.getX(), currentRect.getY());

                    if (enemyPosition.dst(currentVector) < distance) {
                        distance = enemyPosition.dst(currentVector);
                        closestObject = rectangleMapObject;
                    }
                }
            }

            float circleX = currentLevel.getLightSource().x;
            float circleY = currentLevel.getLightSource().y;
            float radius = currentLevel.getLightSource().radius;

            Vector3 minimum = new Vector3(circleX - radius, circleY - radius, 0);
            Vector3 maximum = new Vector3(circleX + radius, circleY + radius, 0);

            if (Intersector.intersectRayBounds(vision, new BoundingBox(minimum, maximum), new Vector3())) {

                Vector2 currentVector = new Vector2(minimum.x, minimum.y);
                if (enemyPosition.dst(currentVector) < distance) {
                    distance = enemyPosition.dst(currentVector);
                    closestObject = currentLevel.getLightSource();
                }
            }

            // If there is a closest object, check to see if the player is closer.
            if (closestObject != null) {
                Vector2 playerPosition = new Vector2(player.getX(), player.getY());
                return distance > enemyPosition.dst(playerPosition);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the enemy towards the waypoint.
     * @param waypoint Waypoint containing the x and y coordinates to move to.
     */
    private void moveTo(Waypoint waypoint) {
        float[] coords = waypoint.getAsComponents(getX() + xOffset, getY() + yOffset, 1.4F);
        deltaX = coords[0];
        deltaY = coords[1];

        if (Intersector.overlaps(new Circle(getX() + xOffset, getY() + yOffset, 10),
                                 waypoint.getCircle())) {
            moving = false;
        }
    }

    /**
     * Detects collisions with static game objects.
     * @param newX    New x coordinate of the enemy.
     * @param newY    New y coordinate of the enemy.
     */
    protected void collisionDetection(float newX, float newY) {
        Rectangle newBounds = new Rectangle(newX, newY, getWidth(), getHeight());
        if (Intersector.overlaps(currentLevel.getLightSource(), newBounds)) {
            return;
        }

        for (Object entity : currentLevel.getEntities(getWidth(), getHeight(), newX, newY)) {
            if (entity instanceof Enemy) {
                continue;
            }
            if (entity instanceof Entity) {
                Entity ent = (Entity) entity;
                if (!entity.equals(this)) {
                    if (Intersector.overlaps(newBounds, ent.getBoundingRectangle())) {
                        return;
                    }
                }
            } else if (entity instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) entity).getRectangle();
                if (newBounds.overlaps(rect)) {
                    return;
                }
            }
        }
        setPosition(newX, newY);
    }

    /**
     * Sets the texture to the appropraite one given the enemies heading.
     */
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
