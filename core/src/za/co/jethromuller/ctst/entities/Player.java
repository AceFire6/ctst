package za.co.jethromuller.ctst.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import za.co.jethromuller.ctst.Level;

/**
 * The player class is the entity that the player controls.
 */
public class Player extends Entity {

    private float radius;
    private Circle circleBounds;
    private boolean sneaking;

    private Circle noiseMarker;
    private int noiseRadius = 60;

    private boolean moving;

    private float noiseCounter;
    private float shootTimer;

    private boolean throwRock;

    private int rockCount = 5;
    private int waterCount = 1;

    /**
     * Creates a player object with the given parameters.
     * @param level        The level that created this entity.
     * @param x           The x coordinate of the player entity.
     * @param y           The y coordinate of the player entity.
     */
    public Player(Level level, float x, float y) {
        super(level, x, y, "entities/player/player_down.png");
        radius = (getWidth() / 2);
        circleBounds = new Circle(x + radius, y + radius, radius);
        noiseMarker = new Circle(x + radius, y + radius, noiseRadius);
        sneaking = false;
        moving = false;
        noiseCounter = 0;
        shootTimer = 2F;

        throwRock = true;
    }

    /**
     * Handles player movement and calls collision detection.
     */
    @Override
    public void update() {
        noiseCounter += Gdx.graphics.getDeltaTime();
        shootTimer += Gdx.graphics.getDeltaTime();
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
            this.setTexture(currentLevel.getGame().textureController.getPlayer_up());
            deltaY = speed;
        } else if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            this.setTexture(currentLevel.getGame().textureController.getPlayer_down());
            deltaY = -speed;
        }

        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            this.setTexture(currentLevel.getGame().textureController.getPlayer_left());
            deltaX = -speed;
        } else if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            this.setTexture(currentLevel.getGame().textureController.getPlayer_right());
            deltaX = speed;
        }

        if (deltaY > 0) {
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_upRight());
            } else if (deltaX < 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_upLeft());
            } else {
                setTexture(currentLevel.getGame().textureController.getPlayer_up());
            }
        } else if (deltaY < 0) {
            if (deltaX > 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_downRight());
            } else if (deltaX < 0) {
                setTexture(currentLevel.getGame().textureController.getPlayer_downLeft());
            } else {
                setTexture(currentLevel.getGame().textureController.getPlayer_down());
            }
        }

        if (deltaX != 0 || deltaY != 0) {
            if ((Math.abs(deltaX) == speed) && (Math.abs(deltaY) == speed)) {
                deltaX *= 0.725;
                deltaY *= 0.725;
            }
            collisionDetection(getX() + deltaX, getY());
            collisionDetection(getX(), getY() + deltaY);
        } else {
            moving = false;
        }

        if (Gdx.input.isKeyJustPressed(Keys.Q)) {
            throwRock = !throwRock;
        }

        if (shootTimer > 2F) {
            if (Gdx.input.isKeyJustPressed(Keys.W)) {
                makeProjectile(0, 1);
            } else if (Gdx.input.isKeyJustPressed(Keys.A)) {
                makeProjectile(-1, 0);
            } else if (Gdx.input.isKeyJustPressed(Keys.S)) {
                makeProjectile(0, -1);
            } else if (Gdx.input.isKeyJustPressed(Keys.D)) {
                makeProjectile(1, 0);
            }
        }
    }

    private void makeProjectile(int xDir, int yDir) {
        String projectileFile;
        if (!throwRock) {
            projectileFile = "entities/water-bomb.png";
            waterCount -= 1;
        } else {
            projectileFile = "entities/rock.png";
            rockCount -= 1;
        }

        if ((!throwRock && waterCount >= 0) || (throwRock && rockCount >= 0)) {
            Projectile projectile = new Projectile(currentLevel, getX(), getY(), xDir, yDir, projectileFile);
            currentLevel.addMapObject(projectile);
        } else {
            currentLevel.getGame().musicController.playSelectSound(0.5F, 0.5F, 0F);
        }
        shootTimer = 0;
    }

    public boolean isNoiseReady() {
        return (noiseCounter > 0.3);
    }

    public void resetNoiseCounter() {
        noiseCounter = 0;
    }

    /**
     * Performs all collision tests given the new X and Y coordinates of the player.
     * @param newX    New x coordinate of the player.
     * @param newY    New y coordinate of the player.
     */
    protected void collisionDetection(float newX, float newY) {
        Circle newCircle = new Circle(newX + radius, newY + radius, radius);

        if (Intersector.overlaps(newCircle, currentLevel.getStaircase())) {
            currentLevel.win();
            return;
        }

        if (Intersector.overlaps(newCircle, currentLevel.getLightSource())) {
            return;
        }

        for (Object entity : currentLevel.getEntities(getWidth(), getHeight(), newX, newY)) {
            if (entity instanceof Treasure) {
                Treasure treasure = (Treasure) entity;
                if (Intersector.overlaps(newCircle, treasure.getBoundingRectangle())) {
                    currentLevel.addScore(treasure.collect());
                    currentLevel.getGame().musicController.playCollectSound();
                    return;
                }
            } else if (entity instanceof Entity) {
                Entity ent = (Entity) entity;
                if (Intersector.overlaps(newCircle, ent.getBoundingRectangle())) {
                    return;
                }
            } else if (entity instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) entity).getRectangle();
                if (Intersector.overlaps(newCircle, rect)) {
                    return;
                }
            }
        }

        setPosition(newX, newY);
        moving = true;
        circleBounds.setPosition(newX + xOffset, newY + yOffset);
        noiseMarker.setPosition(newX + xOffset, newY + yOffset);
    }

    public boolean isMoving() {
        return moving;
    }

    public Circle getCircleBounds() {
        return circleBounds;
    }

    public Circle getNoiseMarker() {
        return noiseMarker;
    }

    public boolean isSneaking() {
        return sneaking;
    }
}
