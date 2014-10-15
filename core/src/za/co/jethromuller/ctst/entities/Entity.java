package za.co.jethromuller.ctst.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
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

    protected Circle boundingCircle;

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

        boundingCircle = new Circle(x + xOffset, y + xOffset, (getWidth() / 2));
    }

    public void setLevel(Level level) {
        this.currentLevel = level;
    }

    public void setCurrentFile(String filePath) {
        setTexture(new Texture(filePath));
        current_file = Gdx.files.internal(filePath);
    }

    /**
     * Empty method that can be overriden.
     * It's called before drawing.
     */
    public void update() {
        //Normal entities don't move
        boundingCircle.setPosition(getX() + xOffset, getY() + xOffset);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity entity = (Entity) obj;

        return (entity.getX() == getX()) && (entity.getY() == getY());
    }

    protected void dispose() {
        currentLevel.killEntity(this);
        this.getTexture().dispose();
    }
}
