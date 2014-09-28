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
        visionRange = new Circle(x + visionRadius, y + visionRadius, visionRadius);
        hearingRange = new Circle(x + hearingRadius, y + hearingRadius, hearingRadius);
        pastTime = 0;
        randTime = new Random();
    }

    @Override
    public void update() {
        Player player = currentLevel.getPlayer();

        if (Intersector.overlaps(player.getCircleBounds(), getBoundingRectangle())) {
            currentLevel.lose();
        }

        if (Intersector.overlaps(player.getCircleBounds(), visionRange) &&
            !currentLevel.inShadow(player)) {
            if (!seen) {
                currentLevel.seePlayer();
                seen = true;
            }
            speed = 0.9F;
            visionRadius = 180;
            if (getX() < player.getX()) {
                deltaX = speed;
            } else if (getX() > player.getX()) {
                deltaX = -speed;
            }

            if (getY() < player.getY()) {
                deltaY = speed;
            } else if (getY() > player.getY()) {
                deltaY = -speed;
            }
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
        visionRange.set(getX(), getY(), visionRadius);
        hearingRange.set(getX(), getY(), hearingRadius);
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
