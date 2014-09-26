package za.co.jethromuller.ctst;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

/**
 * A level entity that requires collision detection.
 * Extends the Sprite class.
 */
public class Entity extends Sprite {

    protected FileHandle current_file;
    protected Level currentLevel;

    /**
     * Creates a new Entity with the given parameters.
     * @param level        The level that is making the entity.
     * @param x           The x coordinate of the entity.
     * @param y           The y coordinate of the entity.
     * @param fileName    The filename of the texture.
     */
    public Entity(Level level, float x, float y, String fileName) {
        super(new Texture(fileName));
        setPosition(x, y);
        setLevel(level);
        setCurrentFile(fileName);
    }

    public void setLevel(Level level) {
        this.currentLevel = level;
    }

    public void setCurrentFile(String filePath) {
        setTexture(new Texture(filePath));
        current_file = new FileHandle(filePath);
    }

    /**
     * Empty method that can be overriden.
     * It's called before drawing.
     */
    public void update() {
        //Normal entities don't move
    }

    /**
     * Performs all collision tests given the new X and Y coordinates of the player.
     * @param newX    New x coordinate of the player.
     * @param newY    New y coordinate of the player.
     */
    protected void collisionDetection(float newX, float newY) {
        Rectangle newBounds = new Rectangle(newX, newY, newX + getWidth(), newY + getHeight());
        for (RectangleMapObject rect: currentLevel.getObstacles()) {
            if (Intersector.overlaps(newBounds, rect.getRectangle())) {
                return;
            }
        }

        for (Entity entity : currentLevel.getEntities(this, newX, newY)) {
            if (Intersector.overlaps(newBounds, entity.getBoundingRectangle())) {
                return;
            }
        }

        setPosition(newX, newY);
    }
}
