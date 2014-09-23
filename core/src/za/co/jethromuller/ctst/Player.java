package za.co.jethromuller.ctst;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.BitSet;

/**
 * The player class is the entity that the player controls.
 */
public class Player extends Entity {

    private int speed;
    private float radius;
    protected Circle circleBounds;

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
        circleBounds.set(x + radius, y + radius, radius);
    }

    /**
     * Handles player movement and calls collision detection.
     */
    @Override
    public void update() {
        float deltaX = 0;
        float deltaY = 0;

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
    private void collisionDetection(float newX, float newY) {
        boolean collision = false;

        for (Entity entity : game.getEntities(this, newX, newY)) {
            if (entity instanceof Player) {
                continue;
            }

            Circle newCircle = new Circle(newX + radius, newY + radius, radius);

            if (Intersector.overlaps(newCircle, entity.getBoundingRectangle())) {
                int x_start = (int) Math.max(newX, entity.getX());
                int y_start = ((int) Math.max(newY, entity.getY()));

                int x_end = ((int) Math.min(newX + this.getWidth(),
                                            entity.getX() + entity.getWidth()));
                int y_end = ((int) Math.min(newY + this.getHeight(),
                                            entity.getY() + entity.getHeight()));

                for (int y = 0; y < Math.abs(y_end - y_start); y++) {
                    System.out.println(y);
                    int y_test1 = Math.abs((y_start - (int) newY)) + y;
                    int y_test2 = Math.abs(y_start - (int) entity.getY()) + y;
                    int x_test1 = Math.abs(((int) (x_start - newX)));
                    int x_test2 = Math.abs(((int) (x_start - entity.getX())));
                    System.out.println("x_test1 = " + x_test1);
                    System.out.println("y_test1 = " + y_test1);
                    System.out.println("x_test2 = " + x_test2);
                    System.out.println("y_test2 = " + y_test2);
                    BitSet overlayEntity = entity.bitSet[y_test2].get(x_test2,
                                                                      x_test2 + Math.abs(x_end -
                                                                                             x_start));
                    bitSet = getBitMask(new Pixmap(current_file));
                    BitSet overlayPlayer = bitSet[y_test1].get(x_test1,
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
}
