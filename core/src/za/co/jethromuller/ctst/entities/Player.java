package za.co.jethromuller.ctst.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
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

    //ALLLLL the textures
    private Texture up = new Texture("entities/player/player_up.png");
    private Texture down = new Texture("entities/player/player_down.png");
    private Texture left = new Texture("entities/player/player_left.png");
    private Texture right = new Texture("entities/player/player_right.png");

    private Texture upLeft = new Texture("entities/player/player_up_left.png");
    private Texture upRight = new Texture("entities/player/player_up_right.png");

    private Texture downLeft = new Texture("entities/player/player_down_left.png");
    private Texture downRight = new Texture("entities/player/player_down_right.png");

    /**
     * Creates a player object with the given parameters.
     * @param level        The level that created this entity.
     * @param x           The x coordinate of the player entity.
     * @param y           The y coordinate of the player entity.
     */
    public Player(Level level, float x, float y) {
        super(level, x, y, "entities/player/player_down.png");
        radius = (getWidth() / 2);
        circleBounds = new Circle();
        circleBounds.set(x + radius, y + radius, radius);
        sneaking = false;
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
            this.setTexture(up);
            deltaY = speed;
        }
        if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            this.setTexture(down);
            deltaY = -speed;
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            this.setTexture(left);
            deltaX = -speed;
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            this.setTexture(right);
            deltaX = speed;
        }

        if (deltaY > 0) {
            if (deltaX > 0) {
                setTexture(upRight);
            } else if (deltaX < 0) {
                setTexture(upLeft);
            }
        } else if (deltaY < 0) {
            if (deltaX > 0) {
                setTexture(downRight);
            } else if (deltaX < 0) {
                setTexture(downLeft);
            }
        }

        if (deltaX != 0 || deltaY != 0) {
            collisionDetection(getX() + deltaX, getY());
            collisionDetection(getX(), getY() + deltaY);
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
                    System.out.println(currentLevel.getScore());
                    return;
                }
            } else if (entity instanceof Entity) {
                Entity ent = (Entity) entity;
                if (Intersector.overlaps(newCircle, ent.getBoundingRectangle())) {
                    return;
                }
            } else if (entity instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) entity).getRectangle();
                if (Intersector.overlaps(newCircle, rect)) {
                    return;
                }
            }
        }

        setPosition(newX, newY);
        circleBounds.setPosition(newX + circleBounds.radius, newY + circleBounds.radius);

    }

    public Circle getCircleBounds() {
        return circleBounds;
    }
}
