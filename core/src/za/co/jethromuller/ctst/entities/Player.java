package za.co.jethromuller.ctst.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import za.co.jethromuller.ctst.Level;

/**
 * The player class is the entity that the player controls.
 */
public class Player extends Entity {

    private float radius;
    private Circle circleBounds;
    private boolean sneaking;

    //ALLLLL the textures
    Texture up = new Texture("entities/player_up.png");
    Texture down = new Texture("entities/player_down.png");
    Texture left = new Texture("entities/player_left.png");
    Texture right = new Texture("entities/player_right.png");

    Texture upLeft = new Texture("entities/player_up_left.png");
    Texture upRight = new Texture("entities/player_up_right.png");

    Texture downLeft = new Texture("entities/player_down_left.png");
    Texture downRight = new Texture("entities/player_down_right.png");

    /**
     * Creates a player object with the given parameters.
     * @param level        The level that created this entity.
     * @param x           The x coordinate of the player entity.
     * @param y           The y coordinate of the player entity.
     * @param fileName    The filename of the texture for this entity.
     */
    public Player(Level level, float x, float y, String fileName) {
        super(level, x, y, fileName);
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

        if (Intersector.overlaps(newCircle, currentLevel.getLightSource())) {
            return;
        }

        for (RectangleMapObject rect: currentLevel.getObstacles()) {
            if (Intersector.overlaps(newCircle, rect.getRectangle())) {
                return;
            }
        }

        for (Entity entity : currentLevel.getEntities(this, newX, newY)) {
            if (Intersector.overlaps(newCircle, entity.getBoundingRectangle())) {
                return;
            }
        }

        setPosition(newX, newY);
        circleBounds.setPosition(newX + circleBounds.radius, newY + circleBounds.radius);

    }

    public Circle getCircleBounds() {
        return circleBounds;
    }
}