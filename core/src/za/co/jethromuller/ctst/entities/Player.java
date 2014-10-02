package za.co.jethromuller.ctst.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import za.co.jethromuller.ctst.Level;

/**
 * The player class is the entity that the player controls.
 */
public class Player extends Entity {

    private float radius;
    private Circle circleBounds;
    private boolean sneaking;

    private Circle noiseMarker;
    private int noiseRadius = 60;

    private boolean moving;

    /**
     * Creates a player object with the given parameters.
     * @param level        The level that created this entity.
     * @param x           The x coordinate of the player entity.
     * @param y           The y coordinate of the player entity.
     */
    public Player(Level level, float x, float y) {
        super(level, x, y, "entities/player/player_down.png");
        radius = (getWidth() / 2);
        circleBounds = new Circle(x + radius, y + radius, radius);
        noiseMarker = new Circle(x + radius, y + radius, noiseRadius);
        sneaking = false;
        moving = false;
    }

    /**
     * Handles player movement and calls collision detection.
     */
    @Override
    public void update() {
        float deltaX = 0;
        float deltaY = 0;
        int speed;

        if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)) {
            sneaking = !sneaking;
        }

        if (!sneaking) {
            speed = 2;
        } else {
            speed = 1;
        }

        if(Gdx.input.isKeyPressed(Keys.UP)) {
            this.setTexture(currentLevel.getGame().textureController.getPlayer_up());
            deltaY = speed;
        } else if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            this.setTexture(currentLevel.getGame().textureController.getPlayer_down());
            deltaY = -speed;
        }

        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            this.setTexture(currentLevel.getGame().textureController.getPlayer_left());
            deltaX = -speed;
        } else if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            this.setTexture(currentLevel.getGame().textureController.getPlayer_right());
            deltaX = speed;
        }

        if (deltaY > 0) {
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_upRight());
            } else if (deltaX < 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_upLeft());
            } else {
                setTexture(currentLevel.getGame().textureController.getPlayer_up());
            }
        } else if (deltaY < 0) {
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_downRight());
            } else if (deltaX < 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_downLeft());
            } else {
                setTexture(currentLevel.getGame().textureController.getPlayer_down());
            }
        }

        if (deltaX != 0 || deltaY != 0) {
            collisionDetection(getX() + deltaX, getY());
            collisionDetection(getX(), getY() + deltaY);
        } else {
            moving = false;
        }
    }

    /**
     * Performs all collision tests given the new X and Y coordinates of the player.
     * @param newX    New x coordinate of the player.
     * @param newY    New y coordinate of the player.
     */
    protected void collisionDetection(float newX, float newY) {
        Circle newCircle = new Circle(newX + radius, newY + radius, radius);

        if (Intersector.overlaps(newCircle, currentLevel.getStaircase())) {
            currentLevel.win();
            return;
        }

        if (Intersector.overlaps(newCircle, currentLevel.getLightSource())) {
            return;
        }

        for (Object entity : currentLevel.getEntities(this, newX, newY)) {
            if (entity instanceof Treasure) {
                Treasure treasure = (Treasure) entity;
                if (Intersector.overlaps(newCircle, treasure.getBoundingRectangle())) {
                    currentLevel.addScore(treasure.collect());
                    moving = false;
                    return;
                }
            } else if (entity instanceof Entity) {
                Entity ent = (Entity) entity;
                if (Intersector.overlaps(newCircle, ent.getBoundingRectangle())) {
                    moving = false;
                    return;
                }
            } else if (entity instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) entity).getRectangle();
                if (Intersector.overlaps(newCircle, rect)) {
                    moving = false;
                    return;
                }
            }
        }

        setPosition(newX, newY);
        moving = true;
        circleBounds.setPosition(newX + xOffset, newY + yOffset);
        noiseMarker.setPosition(newX + xOffset, newY + yOffset);
    }

    public boolean isMoving() {
        return moving;
    }

    public Circle getCircleBounds() {
        return circleBounds;
    }

    public Circle getNoiseMarker() {
        return noiseMarker;
    }

    public boolean isSneaking() {
        return sneaking;
    }
}
