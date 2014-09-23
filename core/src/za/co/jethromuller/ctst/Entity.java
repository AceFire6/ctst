package za.co.jethromuller.ctst;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.BitSet;

/**
 * A level entity that requires collision detection.
 * Extends the Sprite class.
 */
public class Entity extends Sprite {

    private boolean isDoor;
    protected FileHandle current_file;
    /**
     * The bitmask of the given texture.
     */
    protected BitSet[] bitSet;
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
        setBitmask();
//        printBitmask();
    }

    public Entity() {
        super();
    }

    /**
     * For big map things.
     * @param level
     * @param texturePath
     */
    public Entity(Level level, String texturePath) {
        super(new Texture(texturePath));
        setLevel(level);
        setCurrentFile(texturePath);
        setBitmask();
        setPosition(0, 0);
    }

    public void setLevel(Level level) {
        this.currentLevel = level;
    }

    public void setCurrentFile(String filePath) {
        setTexture(new Texture(filePath));
        current_file = new FileHandle(filePath);
    }

    public void setBitmask() {
        bitSet = getBitMask(new Pixmap(current_file));
    }

    /**
     * Prints the bitmask as 1s and 0s
     * to show that they are actually being generated.
     */
    private void printBitmask() {
        System.out.println(current_file.name());
        for (BitSet set : bitSet) {
            for (int i = 0; i < getTexture().getWidth(); i++) {
                if (set.get(i)) {
                    System.out.print("1 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println("");
        }
    }

    /**
     * Empty method that can be overriden.
     * It's called before drawing.
     */
    public void update() {
        //Normal entities don't move
    }

    /**
     * Generates a bitmask and stores it in an array of bitsets.
     * @param pixmap    The pixmap gotten from the texture.
     * @return          An array of bitsets that each contain one of the image's bitmask.
     */
    public BitSet[] getBitMask(Pixmap pixmap) {
        BitSet[] bitmask = new BitSet[pixmap.getHeight()];
        for (int i = 0; i < bitmask.length; i++) {
            bitmask[i] = new BitSet(pixmap.getWidth());
        }
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                if ((pixmap.getPixel(x, y) & 0x000000ff) != 0x00) {
                    bitmask[y].set(x);
                }
            }
        }
        System.out.println("bitmask.length = " + bitmask.length);
        return bitmask;
    }

    public void setDoor() {
        isDoor = true;
    }

    public boolean isDoor() {
        return isDoor;
    }
}
