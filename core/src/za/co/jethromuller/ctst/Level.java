package za.co.jethromuller.ctst;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class Level {
    /**
     * Grid of the various map positions with each
     * grid square holding the entities that intersect the
     * square's coordinates.
     */
    private Array<Entity>[][] mapGrid;

    private TiledMap gameMap;

    private Array<RectangleMapObject> obstacles;
    private Circle roomLight;
    private Array<PolygonMapObject> shadows;

    private Array<Entity> entities;

    private static String levelPath = "levels/*/*.tmx";
    private static int cellSize = 40;
    private boolean lightsOff;


    public Level(String level, Camera camera) {
        gameMap = new TmxMapLoader().load(levelPath.replace("*", level));
        obstacles = gameMap.getLayers().get("obstacles").getObjects().getByType(RectangleMapObject
                                                                                                .class);

        Ellipse ellipse = ((EllipseMapObject) gameMap.getLayers().get("obstacles").getObjects().get
                ("fire")).getEllipse();
        int radius = ((int) (ellipse.circumference() / 6));
        roomLight = new Circle(ellipse.x + radius, ellipse.y + radius, radius);

        shadows = gameMap.getLayers().get("shadows").getObjects().getByType(PolygonMapObject.class);

        lightsOff = false;
        levelPath = levelPath.replace("*", level);

        entities = new Array<>();

        cellSize = ((int) (camera.viewportHeight / 10));
        int gridRows = (int) camera.viewportHeight / cellSize;
        int gridCols = (int) camera.viewportWidth / cellSize;

        mapGrid = new Array[gridRows + 2][gridCols + 2];
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[0].length; j++) {
                mapGrid[i][j] = new Array<>();
            }
        }
    }

    public void lightsOn() {
        gameMap.getLayers().get("gameMapLight").setVisible(true);
        lightsOff = false;
    }

    public void lightsOff() {
        gameMap.getLayers().get("gameMapLight").setVisible(false);
        lightsOff = true;
    }

    private int[] getGridCoords(Entity entity) {
        int topLeftX = ((int) (entity.getX() / cellSize));
        int topLeftY = ((int) (entity.getY() / cellSize));

        int bottomRightX = ((int) ((entity.getX() + entity.getWidth()) / cellSize));
        int bottomRightY = ((int) ((entity.getY() + entity.getHeight()) / cellSize));

        return new int[] {topLeftY, bottomRightY, topLeftX, bottomRightX};
    }

    /**
     * Adds an entity to the entities list and the
     * mapGrid in the correct cells.
     * @param entity The entity to be added.
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
        if (!(entity instanceof Player)) {
            int[] coords = getGridCoords(entity);

            for (int i = coords[0]; i <= coords[1]; i++) {
                for (int j = coords[2]; j <= coords[3]; j++) {
                    mapGrid[i][j].add(entity);
                }
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

    public boolean inShadow(Player player) {
        if (lightsOff) {
            return true;
        } else {
            for (PolygonMapObject shadow : shadows) {
                Polygon shadowPolygon = shadow.getPolygon();
                float x1 = player.getBoundingRectangle().getX();
                float x2 = x1 + player.getBoundingRectangle().getWidth()/2;
                float y1 = player.getBoundingRectangle().getY();
                float y2 = x1 + player.getBoundingRectangle().getHeight()/2;

                for (float y = y1; y < y2; y++) {
                    for (float x = x1; x < x2; x++) {
                        if (shadowPolygon.contains(x, y)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Array<RectangleMapObject> getObstacles() {
        return obstacles;
    }

    public TiledMap getGameMap() {
        return gameMap;
    }

    public Circle getLightSource() {
        return roomLight;
    }

    public void drawBounds(ShapeRenderer shapeRenderer) {
        for (RectangleMapObject rectangleMapObject : getObstacles()) {
            Rectangle rect = rectangleMapObject.getRectangle();
            shapeRenderer.rect(rect.x, rect.y, rect.getWidth(), rect.getHeight());
        }
        shapeRenderer.circle(roomLight.x, roomLight.y, roomLight.radius);
    }
}
