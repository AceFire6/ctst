package za.co.jethromuller.ctst;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import za.co.jethromuller.ctst.entities.Enemy;
import za.co.jethromuller.ctst.entities.Entity;
import za.co.jethromuller.ctst.entities.Player;
import za.co.jethromuller.ctst.menus.GameOverScreen;
import za.co.jethromuller.ctst.menus.PauseMenu;

import java.util.ArrayList;

public class Level implements Screen {
    /**
     * Grid of the various map positions with each
     * grid square holding the entities that intersect the
     * square's coordinates.
     */
    private Array<Object>[][] mapGrid;

    private TiledMap gameMap;

    private Array<RectangleMapObject> obstacles;
    private Circle roomLight;
    private Array<PolygonMapObject> shadows;

    private Array<Entity> entities;

    private Player player;

    private static String levelPath = "levels/*/*.tmx";
    private String levelName;
    private static int cellSize = 40;
    private boolean lightsOff;

    private CtstGame game;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;

    public Level(CtstGame game, String level) {
        super();
        this.game = game;
        this.batch = game.getBatch();

        levelName = level;
        entities = new Array<>();
        cellSize = ((int) (game.getCamera().viewportHeight / 10));
        int gridRows = (int) game.getCamera().viewportHeight / cellSize;
        int gridCols = (int) game.getCamera().viewportWidth / cellSize;

        mapGrid = new Array[gridRows + 2][gridCols + 2];
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[0].length; j++) {
                mapGrid[i][j] = new Array<>();
            }
        }

        gameMap = new TmxMapLoader().load(levelPath.replace("*", level));
        placeEntities();
        obstacles = gameMap.getLayers().get("obstacles").getObjects().getByType(RectangleMapObject
                                                                                                .class);
        addMapObjects(obstacles);

        Ellipse ellipse = ((EllipseMapObject) gameMap.getLayers().get("obstacles").getObjects().get
                ("fire")).getEllipse();
        int radius = ((int) (ellipse.circumference() / 6));
        roomLight = new Circle(ellipse.x + radius, ellipse.y + radius, radius);

        shadows = gameMap.getLayers().get("shadows").getObjects().getByType(PolygonMapObject.class);

        lightsOff = false;
        levelPath = levelPath.replace("*", level);

        mapRenderer = new OrthogonalTiledMapRenderer(gameMap);
        mapRenderer.setView(game.getCamera());

        shapeRenderer = game.getShapeRenderer();
    }

    public void addMapObjects(Object obj) {
        if (obj instanceof Array<?>) {
            for (Object mapObject : ((Array) obj)) {
                addMapObject(mapObject);
            }
        }
    }

    public void placeEntities() {
        Array<RectangleMapObject> entities = gameMap.getLayers().get("entities").getObjects()
                .getByType(RectangleMapObject.class);

        for (RectangleMapObject entity : entities) {
            Rectangle entityRect = entity.getRectangle();
            if (entity.getName().equals("Player")) {
                player = new Player(this, entityRect.getX(), entityRect.getY());
                addMapObject(player);
            } else {
                addMapObject(new Enemy(this, entityRect.getX(), entityRect.getY()));
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

    private int[] getGridCoords(Object entity) {
        int topLeftX = 0;
        int topLeftY = 0;
        int bottomRightX = 0;
        int bottomRightY = 0;
        if (entity instanceof Entity) {
            topLeftX = ((int) (((Entity) entity).getX() / cellSize));
            topLeftY = ((int) (((Entity) entity).getY() / cellSize));

            bottomRightX = ((int) ((((Entity) entity).getX() + ((Entity) entity).getWidth()) /
                                       cellSize));
            bottomRightY = ((int) ((((Entity) entity).getY() + ((Entity) entity).getHeight()) /
                                cellSize));
        } else if (entity instanceof RectangleMapObject) {
            Rectangle rect = ((RectangleMapObject) entity).getRectangle();
            topLeftX = ((int) (rect.getX() / cellSize));
            topLeftY = ((int) (rect.getY() / cellSize));

            bottomRightX = ((int) ((rect.getX() + rect.getWidth()) / cellSize));
            bottomRightY = ((int) ((rect.getY() + rect.getHeight()) / cellSize));
        }

        return new int[] {topLeftY, bottomRightY, topLeftX, bottomRightX};
    }

    /**
     * Adds an entity to the entities list and the
     * mapGrid in the correct cells.
     * @param entity The entity to be added.
     */
    public void addMapObject(Object entity) {
        if (entity instanceof Entity) {
            entities.add(((Entity) entity));
        }
        if (!(entity instanceof Player)) {
            int[] coords = getGridCoords(entity);

            for (int i = coords[0]; i <= coords[1]; i++) {
                for (int j = coords[2]; j <= coords[3]; j++) {
                    mapGrid[i][j].add(entity);
                }
            }
        } else {
            player = ((Player) entity);
        }
    }

    public void updatePositionInGrid(Entity entity) {
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[0].length; j++) {
                mapGrid[i][j].removeValue(entity, false);
            }
        }

        int[] coords = getGridCoords(entity);

        for (int i = coords[0]; i <= coords[1]; i++) {
            for (int j = coords[2]; j <= coords[3]; j++) {
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
    public ArrayList<Object> getEntities(Entity entity, float newX, float newY) {
        ArrayList<Object> possibleEntities = new ArrayList<>();
        int topLeftX = ((int) (newX / cellSize));
        int topLeftY = ((int) (newY / cellSize));

        int bottomRightX = ((int) ((newX + entity.getWidth()) / cellSize));
        int bottomRightY = ((int) ((newY + entity.getHeight()) / cellSize));

        for (int i = topLeftY; i <= bottomRightY; i++) {
            for (int j = topLeftX; j <= bottomRightX; j++) {
                for (Object possibleEntity : mapGrid[i][j]) {
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
                    Circle circle = ((Player) entity).getCircleBounds();
                    shapeRenderer.circle(circle.x, circle.y, circle.radius);
                } else if (entity instanceof Enemy) {
                    Enemy enemy = (Enemy) entity;
                    shapeRenderer.circle(enemy.visionRange.x, enemy.visionRange.y,
                                         enemy.visionRange.radius);
                    shapeRenderer.circle(enemy.hearingRange.x, enemy.hearingRange.y,
                                         enemy.hearingRange.radius);
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
                float x2 = x1 + player.getBoundingRectangle().getWidth();
                float y1 = player.getBoundingRectangle().getY();
                float y2 = y1 + player.getBoundingRectangle().getHeight();

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

    public void drawShadows() {
        for (PolygonMapObject shadow : shadows) {
            shapeRenderer.polygon(shadow.getPolygon().getTransformedVertices());
        }
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

    public void drawBounds() {
        for (RectangleMapObject rectangleMapObject : getObstacles()) {
            Rectangle rect = rectangleMapObject.getRectangle();
            shapeRenderer.rect(rect.x, rect.y, rect.getWidth(), rect.getHeight());
        }
        shapeRenderer.circle(roomLight.x, roomLight.y, roomLight.radius);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseMenu(game, this));
        }

        //Drawing
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        batch.begin();
        // Draws the bounding boxes if the spacebar is pressed.
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 0, 1);
            drawBounds();
            drawShadows();
            drawEntities(true, batch, shapeRenderer);
            shapeRenderer.end();
        } else {
            drawEntities(false, batch, shapeRenderer);
        }
        batch.end();
    }

    public Player getPlayer() {
        return player;
    }

    public void lose() {
        game.setScreen(new GameOverScreen(game, this));
    }

    public String getLevelName() {
        return levelName;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        game.musicController.startGameMusic();
    }

    @Override
    public void hide() {
        game.musicController.pauseGameMusic();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
