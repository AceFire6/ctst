package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import za.co.jethromuller.ctst.Level;

import java.util.Random;

public class Enemy extends Entity {

    public Circle visionRange;
    public Circle hearingRange;

    private int visionRadius = 80;
    private int hearingRadius = 130;

    private long pastTime;

    private float deltaX = 0;
    private float deltaY = 0;
    private float speed = 0.8F;

    private boolean seen;

    private Random randTime;

    public Enemy(Level level, float x, float y) {
        super(level, x, y, "entities/enemy/enemy_down.png");
        visionRange = new Circle(x + xOffset, y + yOffset, visionRadius);
        hearingRange = new Circle(x + xOffset, y + yOffset, hearingRadius);
        pastTime = 0;
        randTime = new Random();
    }

    @Override
    public void update() {
        Player player = currentLevel.getPlayer();

        if (Intersector.overlaps(player.getCircleBounds(), getBoundingRectangle())) {
            currentLevel.lose();
        }

        if ((Intersector.overlaps(player.getCircleBounds(), visionRange) && !currentLevel.inShadow(player)) ||
            (player.isMoving() && !player.isSneaking() && Intersector.overlaps(hearingRange, player.getNoiseMarker()))) {
            if (!seen) {
                currentLevel.seePlayer();
                seen = true;
            }
            speed = 0.8F;
            visionRadius = 190;

            deltaX = (getX() < player.getX()) ? speed: -speed;
            deltaY = (getY() < player.getY()) ? speed: -speed;
        } else {
            seen = false;
            speed = 0.6F;
            visionRadius = 80;
            if ((System.currentTimeMillis() - pastTime) > (randTime.nextInt(2000) + 2000)) {
                Random randSign = new Random();
                Random randFloat = new Random();
                deltaX = randFloat.nextFloat();
                deltaX = randSign.nextBoolean() ? -deltaX: deltaX;
                deltaY = randFloat.nextFloat();
                deltaY = randSign.nextBoolean() ? -deltaY: deltaY;
                pastTime = System.currentTimeMillis();
            }
        }

        if (deltaY > 0) {
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().enemyTextureController.getUpRight());
            } else if (deltaX < 0) {
                setTexture(currentLevel.getGame().enemyTextureController.getUpLeft());
            } else {
                setTexture(currentLevel.getGame().enemyTextureController.getUp());
            }
        } else if (deltaY < 0) {
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().enemyTextureController.getDownRight());
            } else if (deltaX < 0) {
                setTexture(currentLevel.getGame().enemyTextureController.getDownLeft());
            } else {
                setTexture(currentLevel.getGame().enemyTextureController.getDown());
            }
        } else {
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().enemyTextureController.getRight());
            } else if (deltaX < 0) {
                setTexture(currentLevel.getGame().enemyTextureController.getLeft());
            }
        }

        if (deltaX != 0 || deltaY != 0) {
            collisionDetection(getX() + deltaX, getY());
            collisionDetection(getX(), getY() + deltaY);
            currentLevel.updatePositionInGrid(this);
        }
    }

    /**
     * Performs all collision tests given the new X and Y coordinates of the player.
     * @param newX    New x coordinate of the player.
     * @param newY    New y coordinate of the player.
     */
    protected void collisionDetection(float newX, float newY) {
        super.collisionDetection(newX, newY);
        visionRange.setPosition(getX() + xOffset, getY() + yOffset);
        hearingRange.setPosition(getX() + xOffset, getY() + yOffset);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Enemy)) {
            return false;
        }

        Enemy enemy = (Enemy) obj;

        return (enemy.getX() == getX()) && (enemy.getY() == getY());
    }
}
