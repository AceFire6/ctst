package za.co.jethromuller.ctst.entities;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import za.co.jethromuller.ctst.Level;

/**
 * A level entity that requires collision detection.
 * Extends the Sprite class.
 */
public class Entity extends Sprite {

    protected FileHandle current_file;
    protected Level currentLevel;

    protected float xOffset = getWidth()/2;
    protected float yOffset = getHeight()/2;

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
        Rectangle newBounds = new Rectangle(newX, newY, getWidth(), getHeight());
        if (Intersector.overlaps(currentLevel.getLightSource(), newBounds)) {
            return;
        }

        for (Object entity : currentLevel.getEntities(this, newX, newY)) {
            if (entity instanceof Entity) {
                Entity ent = (Entity) entity;
                if (!entity.equals(this)) {
                    if (Intersector.overlaps(newBounds, ent.getBoundingRectangle())) {
                        return;
                    }
                }
            } else if (entity instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) entity).getRectangle();
                if (Intersector.overlaps(newBounds, rect)) {
                    return;
                }
            }
        }


        setPosition(newX, newY);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity entity = (Entity) obj;

        return (entity.getX() == getX()) && (entity.getY() == getY());
    }
}
