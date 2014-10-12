package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import za.co.jethromuller.ctst.Level;
import za.co.jethromuller.ctst.pathfinding.Waypoint;

import java.util.Random;
import java.util.Stack;

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

    public Stack<Waypoint> getWaypoints() {
        return waypoints;
    }

    @Override
    public void update() {
        if (player == null) {
            player = currentLevel.getPlayer();
        }

        if (Intersector.overlaps(player.getCircleBounds(), getBoundingRectangle())) {
            currentLevel.getGame().musicController.playDeathSound(1F, 1F, 0F);
            currentLevel.lose();
        }

        if ((Intersector.overlaps(player.getCircleBounds(), visionRange) &&
             !currentLevel.inShadow(player)) || (player.isMoving() && !player.isSneaking() &&
                    Intersector.overlaps(hearingRange, player.getNoiseMarker())) ||
                   Intersector.overlaps(smellRange, player.getCircleBounds())) {
            if (!seen) {
                currentLevel.seePlayer();
                seen = true;
            }
            visionRadius = 190;

            if (!canSeePlayer()) {
                if ((System.currentTimeMillis() - pathTime) > 500) {
                    pathTime = System.currentTimeMillis();
                    waypoints = currentLevel.pathFinder.getPath(new Waypoint(getX(), getY()),
                                                                new Waypoint(player.getX(),
                                                                             player.getY()));

                    if (waypoints != null && (waypoints.size() != 0)) {
                        moveTo(waypoints.pop());
                    }
                }
            } else {
                waypoints = null;
                moveTo(new Waypoint(player.getX(), player.getY()));
            }
        } else if (waypoints != null && (waypoints.size() != 0)) {
//            System.out.println(waypoints);
            if (!moving) {
                moving = true;
                target = waypoints.pop();
            }
            moveTo(target);
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
            collisionDetection(getX() + deltaX, getY());
            collisionDetection(getX(), getY() + deltaY);

            visionRange.set(getX() + xOffset, getY() + yOffset, visionRadius);
            hearingRange.setPosition(getX() + xOffset, getY() + yOffset);
            smellRange.setPosition(getX() + xOffset, getY() + yOffset);

            currentLevel.updatePositionInGrid(this);
        }
    }

    private boolean canSeePlayer() {
        if (currentLevel.inShadow(player)) {
            return false;
        }
        Ray vision = new Ray(new Vector3(getX(), getY(), 0), new Vector3(player.getX() - getX(),
                                                                         player.getY() - getY(),
                                                                         0));

        visionRay = vision;
        Vector2 enemyPosition = new Vector2(getX(), getY());
        Object closestObject = null;
        double distance = Double.MAX_VALUE;

        for (RectangleMapObject rectangleMapObject : currentLevel.getObstacles()) {
            Rectangle currentRect = rectangleMapObject.getRectangle();

            Vector3 minimum = new Vector3(currentRect.getX(), currentRect.getY(), 0);
            Vector3 maximum = new Vector3(currentRect.getX() + currentRect.width,
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

        if (Intersector.intersectRayBounds(vision, new BoundingBox(minimum, maximum),
                                           new Vector3())) {
            Vector2 currentVector = new Vector2(minimum.x, minimum.y);
            if (enemyPosition.dst(currentVector) < distance) {
                distance = enemyPosition.dst(currentVector);
                closestObject = currentLevel.getLightSource();
            }
        }

        if (closestObject != null) {
            Vector2 playerPosition = new Vector2(player.getX(), player.getY());
            if (distance > enemyPosition.dst(playerPosition)) {
                return true;
            }
        }
        return false;
    }

    private void moveTo(Waypoint waypoint) {
        float[] coords = waypoint.getAsComponents(getX(), getY(), 1.4F);
        deltaX = coords[0];
        deltaY = coords[1];

        if (Intersector.overlaps(new Circle(getX() + xOffset, getY() + yOffset, 15),
                                 waypoint.getCircle())) {
            moving = false;
        }
    }

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
