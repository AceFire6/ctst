package za.co.jethromuller.ctst;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.BitSet;

/**
 * The player class is the entity that the player controls.
 */
public class Player extends Entity {

    private int speed;
    private float radius;
    private boolean useCircle;
    protected Circle circleBounds;

    /**
     * Creates a player object with the given parameters.
     * @param game        The game that created this entity.
     * @param x           The x coordinate of the player entity.
     * @param y           The y coordinate of the player entity.
     * @param fileName    The filename of the texture for this entity.
     */
    public Player(CtstMain game, float x, float y, String fileName) {
        super(game, x, y, fileName);
        speed = 2;
        radius = (getWidth() / 2);
        circleBounds = new Circle();
        circleBounds.set(x, y, radius);
        useCircle = false;
    }

    /**
     * Handles player movement and calls collision detection.
     */
    @Override
    public void update() {
        float deltaX = 0;
        float deltaY = 0;

        if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)) {
            useCircle = !useCircle;
        }

        if(Gdx.input.isKeyPressed(Keys.UP)) {
            deltaY = speed;
        }
        if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            deltaY = -speed;
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            deltaX = -speed;
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            deltaX = speed;
        }

        collisionDetection(getX() + deltaX, getY());
        collisionDetection(getX(), getY() + deltaY);
    }

    /**
     * Checks to see if, given the new X value, the player will intersect
     * the game's borders.
     * @param newX    The new x coordinate of the player.
     * @return  boolean indicating whether or not a collision occurs.
     */
    public boolean worldBorderCollisionX(float newX) {
        int tileSize = game.tileSize;
        int x_val;

        if (newX < tileSize - 2) {
            x_val = tileSize - 2;
            setX(x_val);
            circleBounds.setPosition(x_val + circleBounds.radius, circleBounds.y + circleBounds.radius);
            return true;
        } else if (newX > tileSize * (game.map[0].length - 2) - 3) {
            x_val = tileSize * (game.map[0].length - 2) - 3;
            setX(x_val);
            circleBounds.setPosition(x_val + circleBounds.radius, circleBounds.y + circleBounds.radius);
            return true;
        }
        return false;
    }

    /**
     * Checks to see if, given the new Y value, the player will intersect
     * the game's borders.
     * @param newY    The new y coordinate of the player.
     * @return  boolean indicating whether or not a collision occurs.
     */
    public boolean worldBorderCollisionY(float newY) {
        int tileSize = game.tileSize;
        int y_val;
        if (newY < tileSize - 2) {
            y_val = tileSize - 2;
            setY(y_val);
            circleBounds.setPosition(circleBounds.x + circleBounds.radius, y_val + circleBounds.radius);
            return true;
        } else if (newY > tileSize * (game.map.length - 2) - 3) {
            y_val = tileSize * (game.map.length - 2) - 3;
            setY(y_val);
            circleBounds.setPosition(circleBounds.x + circleBounds.radius, y_val + circleBounds.radius);
            return true;
        }
        return false;
    }

    /**
     * Performs all collision tests given the new X and Y coordinates of the player.
     * @param newX    New x coordinate of the player.
     * @param newY    New y coordinate of the player.
     */
    private void collisionDetection(float newX, float newY) {
        boolean collision = false;
        if (worldBorderCollisionX(newX)) {
            return;
        }
        if (worldBorderCollisionY(newY)) {
            return;
        }

        for (Entity entity : game.getEntities(this, newX, newY)) {
            if (entity.equals(this)) {
                continue;
            }

            Circle newCircle = null;
            if (useCircle) {
                newCircle = new Circle(newX + radius, newY + radius, radius);
            }
            Rectangle newRectangle = new Rectangle(newX, newY, getWidth(), getHeight());
            if (((useCircle) ?
                 Intersector.overlaps(newCircle, entity.getBoundingRectangle()) :
                 Intersector.intersectRectangles(newRectangle, entity.getBoundingRectangle(),
                                                 new Rectangle())))
            {
                int x_start = (int) Math.max(newX, entity.getX());
                int y_start = ((int) Math.max(newY, entity.getY()));

                int x_end = ((int) Math.min(newX + this.getWidth(),
                                            entity.getX() + entity.getWidth()));
                int y_end = ((int) Math.min(newY + this.getHeight(),
                                            entity.getY() + entity.getHeight()));

                for (int y = 1; y < Math.abs(y_end - y_start); y++) {
                    int y_test1 = Math.abs(((int) (y_start - newY))) + y;
                    int y_test2 = Math.abs(y_start - (int) entity.getY()) + y;
                    int x_test1 = Math.abs(((int) (x_start - newX)));
                    int x_test2 = Math.abs(((int) (x_start - entity.getX())));
                    BitSet overlayEntity = entity.bitSet[y_test2].get(x_test2,
                                                                      x_test2 + 1 + Math.abs(x_end -
                                                                                             x_start));
                    BitSet overlayPlayer = bitSet[y_test1 - 1].get(x_test1,
                                                               x_test1 + Math.abs(x_end - x_start));
                    overlayPlayer.and(overlayEntity);
                    if (overlayPlayer.cardinality() != 0) {
                        collision = true;
                        break;
                    }
                }
            }
        }

        if (!collision) {
            setPosition(newX, newY);
            circleBounds.setPosition(newX + circleBounds.radius, newY + circleBounds.radius);
        }
    }

    public float getRadius() {
        return radius;
    }

    public boolean useCircle() {
        return useCircle;
    }
}
