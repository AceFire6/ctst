package za.co.jethromuller.ctst;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import za.co.jethromuller.ctst.entities.*;
import za.co.jethromuller.ctst.menus.GameOverScreen;
import za.co.jethromuller.ctst.menus.PauseMenu;
import za.co.jethromuller.ctst.menus.ScoreScreen;

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
    private Array<VfxEntity> animations;

    private Player player;

    private String levelPath = "levels/*/*.tmx";
    private String levelName;
    private static int cellSize = 40;
    private boolean lightsOff;

    private CtstGame game;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;

    private long startTime;
    private long endTime;

    private int timesSeen = 0;
    private double finalScore;

    private Rectangle staircase;


    public Level(CtstGame game, String level) {
        super();
        this.game = game;
        this.batch = game.getBatch();

        levelName = level;
        entities = new Array<>();
        animations = new Array<>();
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

        System.out.println(obstacles.size);
        addMapObjects(obstacles);

        Ellipse ellipse = ((EllipseMapObject) gameMap.getLayers().get("obstacles").getObjects().get
                ("fire")).getEllipse();
        int radius = ((int) (ellipse.circumference() / 6));
        roomLight = new Circle(ellipse.x + radius, ellipse.y + radius, radius);
        addAnimation(new VfxEntity(this, ellipse.x + 2, ellipse.y + 2, "vfx/fire/", 6, 0.5F,
                                   false));

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
                if (mapObject instanceof RectangleMapObject && ((RectangleMapObject) mapObject)
                        .getName().equals("staircase")) {
                    staircase = ((RectangleMapObject) mapObject).getRectangle();
                } else {
                    addMapObject(mapObject);
                }
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
            } else if (entity.getName().equals("Enemy")) {
                addMapObject(new Enemy(this, entityRect.getX(), entityRect.getY()));
            } else {
                addMapObject(new Treasure(this, entityRect.getX(), entityRect.getY()));
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

    public void addScore(int score) {
        finalScore += score;
    }

    public Rectangle getStaircase() {
        return staircase;
    }

    public void seePlayer() {
        timesSeen += 1;
    }

    public String getStealthy() {
        if (timesSeen == 0) {
            return "Godly Sneakmeister Deluxe";
        } else if (timesSeen < 5) {
            return "Corvo would be proud";
        } else if (getScore() == 0) {
            return "You have failed this city";
        } else {
            return "No, it hurts to see you try";
        }
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

    /**
     * Removes the given entity from any future collisions.
     */
    public void killEntity(Entity entity) {
        if (entity instanceof VfxEntity) {
            animations.removeValue(((VfxEntity) entity), false);
        } else {
            removeEntity(entity);
            entities.removeValue(entity, false);
        }
    }

    public void removeEntity(Entity entity) {
        for (Array<Object>[] aMapGrid : mapGrid) {
            for (int j = 0; j < mapGrid[0].length; j++) {
                aMapGrid[j].removeValue(entity, false);
            }
        }
    }

    /**
     * Recalculates the entities position in the mapGrid.
     * @param entity
     */
    public void updatePositionInGrid(Entity entity) {
        removeEntity(entity);
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
     */
    public void drawEntities() {
        for (Entity entity : entities) {
            entity.update();
            entity.draw(batch);
        }
    }

    public void drawAnimations() {
        for (VfxEntity animation : animations) {
            animation.update();
            TextureRegion animRegion = animation.getKeyframe();
            if (animRegion != null) {
                float x = animation.getX() - (animation.getWidth() / 2) + (player.getWidth() / 2);
                float y = animation.getY() - (animation.getHeight() / 2) + (player.getHeight() / 2);
                batch.draw(animRegion, x, y);
            }
        }
    }

    public void drawEntityBounds() {
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                Circle circle = ((Player) entity).getCircleBounds();
                Circle circleNoise = ((Player) entity).getNoiseMarker();
                shapeRenderer.circle(circle.x, circle.y, circle.radius);
                shapeRenderer.circle(circleNoise.x, circleNoise.y, circleNoise.radius);
            } else if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                shapeRenderer.circle(enemy.visionRange.x, enemy.visionRange.y, enemy.visionRange.radius);
                shapeRenderer.circle(enemy.hearingRange.x, enemy.hearingRange.y, enemy.hearingRange.radius);
                shapeRenderer.circle(enemy.smellRange.x, enemy.smellRange.y, enemy.smellRange.radius);
            } else {
                shapeRenderer.rect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
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

    public Circle getLightSource() {
        return roomLight;
    }

    public void drawMapObstaclesBounds() {
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

        if (player.isMoving() && !player.isSneaking() && player.isNoiseReady()) {
            addAnimation(new VfxEntity(this, player.getX(), player.getY(), "vfx/sound_ripple/",
                                       5, 0.15F, true));
            game.musicController.playWalkSound(0.8F, 1F, 0F);
            player.resetNoiseCounter();
        } else if (player.isMoving() && player.isSneaking() && player.isNoiseReady()) {
            game.musicController.playWalkSound(0.3F, 1F, 0F);
            player.resetNoiseCounter();
        }

        //Drawing
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        batch.begin();
        drawAnimations();
        drawEntities();
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shapeRenderer.setColor(1, 1, 0, 1);
            drawMapObstaclesBounds();
            drawShadows();
            drawEntityBounds();
        }
        shapeRenderer.end();
    }

    public void addAnimation(VfxEntity vfxEntity) {
        animations.add(vfxEntity);
    }

    public Player getPlayer() {
        return player;
    }

    public void lose() {
        game.setScreen(new GameOverScreen(game, this));
    }

    public void win() {
        endTime = System.currentTimeMillis();
        calculateScore();
        game.setScreen(new ScoreScreen(game, this));
    }

    private void calculateScore() {
        int baseScore = 1000;
        double score = (baseScore - (25 * timesSeen) - (getTime() * 10));
        finalScore += ((score >= 0 ? score : 0));
    }

    public double getScore() {
        return finalScore;
    }

    public double getTime() {
        return (endTime - startTime) / 1000;
    }

    public String getLevelName() {
        return levelName;
    }

    public CtstGame getGame() {
        return game;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        game.musicController.startGameMusic();
        startTime = System.currentTimeMillis();
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
