package za.co.jethromuller.ctst;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.BitSet;

/**
 * A game entity that requires collision detection.
 * Extends the Sprite class.
 */
public class Entity extends Sprite {

    CtstMain game;
    private boolean isDoor;
    protected FileHandle current_file;
    /**
     * The bitmask of the given texture.
     */
    protected BitSet[] bitSet;

    /**
     * Creates a new Entity with the given parameters.
     * @param game        The game that is making the entity.
     * @param x           The x coordinate of the entity.
     * @param y           The y coordinate of the entity.
     * @param fileName    The filename of the texture.
     */
    public Entity(CtstMain game, float x, float y, String fileName) {
        super(new Texture(fileName));
        setPosition(x, y);
        this.game = game;
        current_file = new FileHandle(fileName);
        bitSet = getBitMask(new Pixmap(current_file));
        printBitmask();
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
        return bitmask;
    }

    public void setDoor() {
        isDoor = true;
    }

    public boolean isDoor() {
        return isDoor;
    }
}
