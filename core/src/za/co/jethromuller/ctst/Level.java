package za.co.jethromuller.ctst;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import java.util.ArrayList;

public class Level {
    /**
     * Grid of the various map positions with each
     * grid square holding the entities that intersect the
     * square's coordinates.
     */
    private ArrayList<Entity>[][] mapGrid;

    private Texture gameMap;
    private Texture gameMapDark;
    private Texture shadowMap;
    private Entity levelObstacles;

    private Texture displayedMap;

    private ArrayList<Entity> entities;

    private static String levelPath = "levels/*/*_#.png";
    private static int cellSize = 40;


    public Level(String level, Camera camera) {
        levelPath = levelPath.replace("*", level);
        gameMap = new Texture(levelPath.replace("_#", ""));
        levelObstacles = new Entity(this, levelPath.replace("#", "collisionMap"));
        gameMapDark = new Texture(levelPath.replace("#", "no_light"));
        shadowMap = new Texture(levelPath.replace("#", "shadows"));
        displayedMap = gameMap;

        entities = new ArrayList<>();

        cellSize = ((int) (camera.viewportHeight / 10));
        int gridRows = (int) camera.viewportHeight / cellSize;
        int gridCols = (int) camera.viewportWidth / cellSize;

        mapGrid = new ArrayList[gridRows + 2][gridCols + 2];
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[0].length; j++) {
                mapGrid[i][j] = new ArrayList<>();
            }
        }
    }

    public void lightsOn() {
        displayedMap = gameMap;
    }

    public void lightsOff() {
        displayedMap = gameMapDark;
    }

    /**
     * Adds an entity to the entities list and the
     * mapGrid in the correct cells.
     * @param entity The entity to be added.
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
        int topLeftX = ((int) (entity.getX() / cellSize));
        int topLeftY = ((int) (entity.getY() / cellSize));

        int bottomRightX = ((int) ((entity.getX() + entity.getWidth()) / cellSize));
        int bottomRightY = ((int) ((entity.getY() + entity.getHeight()) / cellSize));

        for (int i = topLeftY; i <= bottomRightY; i++) {
            for (int j = topLeftX; j <= bottomRightX; j++) {
                mapGrid[i][j].add(entity);
            }
        }
    }

    /**
     * Returns an ArrayList of the entities in the grid cells
     * that are intersected by the entity given as a parameter.
     * @param entity    The entity that will cause collisions.
     * @return  ArrayList of entities that could be collided with.
     */
    public ArrayList<Entity> getEntities(Entity entity, float newX, float newY) {
        ArrayList<Entity> possibleEntities = new ArrayList<>();
        int topLeftX = ((int) (newX / cellSize));
        int topLeftY = ((int) (newY / cellSize));

        int bottomRightX = ((int) ((newX + entity.getWidth()) / cellSize));
        int bottomRightY = ((int) ((newY + entity.getHeight()) / cellSize));

        for (int i = topLeftY; i <= bottomRightY; i++) {
            for (int j = topLeftX; j <= bottomRightX; j++) {
                for (Entity possibleEntity : mapGrid[i][j]) {
                    possibleEntities.add(possibleEntity);
                }
            }
        }
        possibleEntities.add(levelObstacles);
        return possibleEntities;
    }

    /**
     * Draws all the entities.
     * @param drawBounds    boolean indicating whether or not to draw the bounding boxes.
     */
    public void drawEntities(boolean drawBounds, SpriteBatch batch, ShapeRenderer shapeRenderer) {
        for (Entity entity : entities) {
            entity.update();
            entity.draw(batch);
            if (drawBounds) {
                if (entity instanceof Player) {
                    Circle circle = ((Player) entity).circleBounds;
                    shapeRenderer.circle(circle.x, circle.y, circle.radius);
                } else {
                    shapeRenderer.rect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
                }
            }
        }
    }

    public void drawMap(SpriteBatch batch) {
        batch.draw(gameMap, 0, 0);
    }
}
