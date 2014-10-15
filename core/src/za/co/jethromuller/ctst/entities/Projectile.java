package za.co.jethromuller.ctst.entities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import za.co.jethromuller.ctst.Level;


public class Projectile extends Entity {

    int yDir;
    int xDir;
    int speed = 4;
    boolean isWater;

    /**
     * Creates a new Entity with the given parameters.
     *
     * @param level    The level that is making the entity.
     * @param x        The x coordinate of the entity.
     * @param y        The y coordinate of the entity.
     * @param fileName The filename of the texture.
     */
    public Projectile(Level level, float x, float y, int xDir, int yDir, String fileName) {
        super(level, x, y, fileName);
        this.xDir = xDir;
        this.yDir = yDir;

        isWater = fileName.contains("water");
    }

    @Override
    public void update() {
        super.update();
        collisionDetection(getX() + (xDir * speed), getY() + (yDir * speed));
    }

    protected void collisionDetection(float newX, float newY) {
        if (Intersector.overlaps(currentLevel.getLightSource(), boundingCircle)) {
            if (isWater) {
                currentLevel.lightsOff();
            }
            explode();
            return;
        }

        for (Object entity : currentLevel.getEntities(getWidth(), getHeight(), newX, newY)) {
            if (entity instanceof Player) {
                continue;
            }
            if (entity instanceof Entity) {
                Entity ent = (Entity) entity;
                if (!entity.equals(this)) {
                    if (Intersector.overlaps(boundingCircle, ent.getBoundingRectangle())) {
                        explode();
                        return;
                    }
                }
            } else if (entity instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) entity).getRectangle();
                if (Intersector.overlaps(boundingCircle, rect)) {
                    explode();
                    return;
                }
            }
        }
        setPosition(newX, newY);
    }

    private void explode() {
        currentLevel.getGame().musicController.playWaterSound(1F, 1F, 0F);
        if (!isWater) {
            for (Enemy enemy : currentLevel.getEnemies()) {
                enemy.alertEnemy(getX() + xOffset, getY() + yOffset);
            }
        }
        dispose();
    }

}
