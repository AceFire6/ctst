package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import za.co.jethromuller.ctst.Level;

import java.util.Random;

public class Enemy extends Entity {

    public Circle visionRange;
    public Circle hearingRange;
    public Circle smellRange;

    private int visionRadius = 80;
    private int smellRadius = 20;
    private int hearingRadius = 130;

    private long pastTime;

    private float deltaX = 0;
    private float deltaY = 0;

    private boolean seen;
    private Player player;

    private Random randTime;

    public Enemy(Level level, float x, float y) {
        super(level, x, y, "entities/enemy/enemy_down.png");
        visionRange = new Circle(x + xOffset, y + yOffset, visionRadius);
        hearingRange = new Circle(x + xOffset, y + yOffset, hearingRadius);
        smellRange = new Circle(x + xOffset, y + yOffset, smellRadius);

        pastTime = 0;
        randTime = new Random();

        setCollidable(true);
    }

    @Override
    public void update() {
        if (player == null) {
            player = currentLevel.getPlayer();
        }

        float speed = 0.9F;

        if (Intersector.overlaps(player.getCircleBounds(), getBoundingRectangle())) {
            currentLevel.lose();
        }

        if ((Intersector.overlaps(player.getCircleBounds(), visionRange) && !currentLevel.inShadow(player)) ||
            (player.isMoving() && !player.isSneaking() && Intersector.overlaps(hearingRange, player.getNoiseMarker()))
                || Intersector.overlaps(smellRange, player.getCircleBounds())) {
            if (!seen) {
                currentLevel.seePlayer();
                seen = true;
            }
            visionRadius = 190;
            speed = 1.1F;

            deltaX = (getX() < player.getX() - 10) ? speed: (getX() > player.getX() + 10) ?
                                                           -speed: 0;
            deltaY = (getY() < player.getY() - 10) ? speed: (getY() > player.getY() + 10) ?
                                                           -speed: 0;
        } else {
            seen = false;
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

        if (deltaY > 0) { // up
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_upRight());
            } else if (deltaX == 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_up());
            } else {
                setTexture(currentLevel.getGame().textureController.getEnemy_upLeft());
            }
        } else if  (deltaY == 0) {  // left or right
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_right());
            } else if (deltaX == 0) {
                return;
            } else {
                setTexture(currentLevel.getGame().textureController.getEnemy_left());
            }
        } else { // down
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_downRight());
            } else if (deltaX == 0) {
                setTexture(currentLevel.getGame().textureController.getEnemy_down());
            } else {
                setTexture(currentLevel.getGame().textureController.getEnemy_downLeft());
            }
        }

        if (deltaX != 0 || deltaY != 0) {
            if ((Math.abs(deltaX) == speed) && (Math.abs(deltaY) == speed)) {
                deltaX *= 0.725;
                deltaY *= 0.725;
            }
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
        visionRange.set(getX() + xOffset, getY() + yOffset, visionRadius);
        hearingRange.setPosition(getX() + xOffset, getY() + yOffset);
        smellRange.setPosition(getX() + xOffset, getY() + yOffset);
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
