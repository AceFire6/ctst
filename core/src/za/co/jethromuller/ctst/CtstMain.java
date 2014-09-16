package za.co.jethromuller.ctst;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

/**
 * Main class that handles all the rendering and map and entities.
 */
public class CtstMain extends ApplicationAdapter {
    private SpriteBatch batch;
    protected int[][] map = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,0,0,1,1,1,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,1,1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };
    protected int tileSize = 20;
    protected int mapWidth = map[0].length * tileSize;
    protected int mapHeight = map.length * tileSize;

    private Texture groundTexture;


    /**
     * Grid of the various map positions with each
     * grid square holding the entities that intersect the
     * square's coordinates.
     */
    private ArrayList<Entity>[][] mapGrid;
    /**
     * All the Entities
     */
    private ArrayList<Entity> entities;
    private int cellSize;

    int gridRows;
    int gridCols;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create () {
//        exit_door = new Sprite(new Texture("game_pieces/door.png"));

        batch = new SpriteBatch();
        Camera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(mapWidth, mapHeight, camera);
        viewport.apply(true);

        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        Player player = new Player(this, 40, 60, "entities/player.png");

        entities = new ArrayList<>();

        groundTexture = new Texture("game_pieces/ground.jpg");

        cellSize = ((int) (camera.viewportHeight / 10));
        gridRows = (int) camera.viewportHeight / cellSize;
        gridCols = (int) camera.viewportWidth / cellSize;

        mapGrid = new ArrayList[gridRows + 2][gridCols + 2];
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[0].length; j++) {
                mapGrid[i][j] = new ArrayList<>();
            }
        }

        addEntity(player);
        addEntity(new Entity(this, 50, 150, "entities/enemy.png"));
        addEntity(new Entity(this, 200, 200, "entities/enemy.png"));
        addEntity(new Entity(this, 180, 50, "entities/enemy.png"));
        getObstacles();
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
        return possibleEntities;
    }

    @Override
    public void render () {
        //Drawing
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        // Draws the bounding boxes if the spacebar is pressed.
        drawMap();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setColor(1, 1, 0, 1);
            drawEntities(true);
            shapeRenderer.end();
        } else {
            drawEntities(false);
        }
        batch.end();
    }

    /**
     * Gets and adds the obstacles in the map that aren't on the edges of the map.
     */
    private void getObstacles() {
        int x_pos;
        int y_pos = mapHeight;
        for (int[] ints : map) {
            x_pos = mapWidth;
            y_pos -= tileSize;
            for (int anInt : ints) {
                x_pos -= tileSize;
                if (anInt == 1) {
                    addEntity(new Entity(this, x_pos, y_pos, "game_pieces/block.png"));
                } else if (anInt == 2) {
                    Entity doorEntity = new Entity(this, x_pos, y_pos, "game_pieces/door.png");
                    doorEntity.setDoor();
                    addEntity(doorEntity);

                    if (x_pos >= mapWidth - tileSize) {
                        System.out.println("Rotating: 90 cw");
                        doorEntity.rotate90(true);
                    } else if (x_pos <= tileSize) {
                        System.out.println("Rotating: 90 ccw");
                        doorEntity.rotate90(false);
                    } else if (y_pos > (2 * tileSize)) {
                        System.out.println("Rotating: 180");
                        doorEntity.rotate(180);
                    }
                }
            }
        }
    }

    /**
     * Draws all the entities.
     * @param drawBounds    boolean indicating whether or not to draw the bounding boxes.
     */
    public void drawEntities(boolean drawBounds) {
        for (Entity entity : entities) {
            entity.update();
            entity.draw(batch);
            if (drawBounds) {
                if (entity instanceof Player &&  ((Player) entity).useCircle()) {
                    float circleRadius = ((Player) entity).circleBounds.radius;
                    shapeRenderer.circle(
                            entity.getX() + circleRadius, entity.getY() + circleRadius, ((Player) entity).getRadius());
                } else {
                    shapeRenderer.rect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
                }
            }
        }
    }

    /**
     * Draws the map based on the map's low resolution bitmap.
     */
    public void drawMap() {
        int x_pos;
        int y_pos = mapHeight;
        for (int[] ints : map) {
            x_pos = mapWidth;
            y_pos -= tileSize;
            for (int anInt :ints) {
                x_pos -= tileSize;
                if (anInt == 0) {
                    batch.draw(groundTexture, x_pos, y_pos);
                }
            }
        }
    }
}
