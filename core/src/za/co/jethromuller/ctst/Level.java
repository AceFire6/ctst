package za.co.jethromuller.ctst;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import za.co.jethromuller.ctst.entities.*;
import za.co.jethromuller.ctst.menus.GameOverScreen;
import za.co.jethromuller.ctst.menus.PauseMenu;
import za.co.jethromuller.ctst.menus.ScoreScreen;
import za.co.jethromuller.ctst.pathfinding.PathFinder;
import za.co.jethromuller.ctst.pathfinding.Waypoint;

import java.util.ArrayList;

/**
 * Each level is generated based on the given tmx file.
 *
 * The required layer are called:
 *      - "obstacles"
 *      - "shadows"
 *      - "entities"
 *
 *  In the obstacles layer, there are two required objects.
 *  A light source labelled "fire" and a staircase labelled "staircase".
 *
 *  In the entities layer, there are 3 types of possible entities that are handled.
 *      - "Enemy"
 *      - "Player"
 *      - "Treasure"
 *
 *  The appropriate entity class with the given coordinate will be spawned at the locations given
 *  by the map.
 *
 *  The map must be in the levels folder, in a folder with the same name as the map.
 *  Eg. `/levels/level1/level1.tmx`
 */
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

    private ArrayList<Entity> entities;
    private Array<VfxEntity> animations;
    private VfxEntity fireVfx;

    private Player player;
    private ArrayList<Enemy> enemies;

    private String levelPath = "levels/*/*.tmx";
    private String levelName;
    private int levelIndex;
    private int cellSize;
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

    private int gridRows;
    private int gridCols;

    public PathFinder pathFinder;


    /**
     * Creates a level based on the map with the name given by level.
     * @param game     The game object that holds information the level object will need.
     * @param level    A String that holds the name of th level.
     */
    public Level(CtstGame game, String level, int levelIndex) {
        super();
        this.game = game;
        this.batch = game.getBatch();

        this.levelIndex = levelIndex;
        levelName = level;
        entities = new ArrayList<>();
        animations = new Array<>();
        cellSize = ((int) (game.getCamera().viewportHeight / 10));
        gridRows = (int) game.getCamera().viewportHeight / cellSize;
        gridCols = (int) game.getCamera().viewportWidth / cellSize;

        mapGrid = new Array[gridRows + 2][gridCols + 2];
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[0].length; j++) {
                mapGrid[i][j] = new Array<>();
            }
        }

        gameMap = new TmxMapLoader().load(levelPath.replace("*", level));
        enemies = new ArrayList<>();
        placeEntities();
        obstacles = gameMap.getLayers().get("obstacles").getObjects().getByType(RectangleMapObject
                                                                                                .class);

        addMapObjects(obstacles);

        addFire();

        shadows = gameMap.getLayers().get("shadows").getObjects().getByType(PolygonMapObject.class);

        lightsOff = false;
        levelPath = levelPath.replace("*", level);

        mapRenderer = new OrthogonalTiledMapRenderer(gameMap);
        mapRenderer.setView(game.getCamera());

        shapeRenderer = game.getShapeRenderer();

        pathFinder = new PathFinder(this);
    }

    public void addFire() {
        Ellipse ellipse = ((EllipseMapObject) gameMap.getLayers().get("obstacles").getObjects().get
                ("fire")).getEllipse();
        int radius = ((int) (ellipse.circumference() / 5));
        roomLight = new Circle(ellipse.x + (ellipse.width / 2), ellipse.y + (ellipse.height / 2),
                               radius);
        fireVfx = new VfxEntity(this, ellipse.x, ellipse.y, "vfx/fire/", 6, 0.4F, false);
        addAnimation(fireVfx);
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public int getRows() {
        return gridRows;
    }

    public int getCols() {
        return gridCols;
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
                Enemy enemy = new Enemy(this, entityRect.getX(), entityRect.getY());
                enemies.add(enemy);
                addMapObject(enemy);
            } else if (entity.getName().equals("Treasure")) {
                addMapObject(new Treasure(this, entityRect.getX(), entityRect.getY()));
            }
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void lightsOff() {
        gameMap.getLayers().get("gameMapLight").setVisible(false);
        animations.removeValue(fireVfx, false);
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
        } else if (timesSeen < 10) {
            return "You're no Sam Fisher";
        } else if (timesSeen < 15) {
            return "Maybe try Call of Duty instead?";
        } else if (getScore() < 100) {
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
            entities.remove(entity);
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
     * @return  ArrayList of entities that could be collided with.
     */
    public ArrayList<Object> getEntities(float width, float height, float newX, float newY) {
        ArrayList<Object> possibleEntities = new ArrayList<>();
        int topLeftX = ((int) (newX / cellSize));
        int topLeftY = ((int) (newY / cellSize));

        int bottomRightX = ((int) ((newX + width) / cellSize));
        int bottomRightY = ((int) ((newY + height) / cellSize));

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
        ArrayList<Entity> notTreasures = new ArrayList<>();
        for (Entity entity : entities) {
            if (!(entity instanceof Treasure)) {
                notTreasures.add(entity);
            } else {
                entity.update();
                entity.draw(batch);
            }
        }

        for (Entity notTreasure : notTreasures) {
            notTreasure.update();
            notTreasure.draw(batch);
            if (notTreasure instanceof Player) {
                Texture projectile;
                if (((Player) notTreasure).isThrowRock()) {
                    projectile = new Texture("entities/rock.png");
                } else {
                    projectile = new Texture("entities/water-bomb.png");
                }
                batch.draw(projectile, 5, 5, 20, 20);
            }
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

                if (enemy.visionRay != null) {
                    shapeRenderer.line(enemy.visionRay.origin, enemy.visionRay.getEndPoint(new
                                                                                                   Vector3(), new Vector2(enemy.getOriginX(), enemy.getOriginY()).dst(player.getX(), player.getY())));
                }

                if (enemy.getWaypoints() != null) {
                    for (Waypoint waypoint : enemy.getWaypoints()) {
                        shapeRenderer.circle(waypoint.getX(), waypoint.getY(), 5);
                    }
                }
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
            shapeRenderer.setColor(0.16F, 1, 0.45F, 1);
            drawSenses();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SLASH)) {
            shapeRenderer.setColor(0.16F, 1, 0.45F, 1);
            drawMapObstaclesBounds();
            drawShadows();
            drawEntityBounds();
        }
        shapeRenderer.end();
    }

    private void drawSenses() {
        for (Entity entity : entities) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                shapeRenderer.circle(enemy.visionRange.x, enemy.visionRange.y, enemy.visionRange.radius);
                shapeRenderer.circle(enemy.hearingRange.x, enemy.hearingRange.y, enemy.hearingRange.radius);

                if (enemy.visionRay != null) {
                    shapeRenderer.line(enemy.visionRay.origin, enemy.visionRay.getEndPoint(new
                                                                                                   Vector3(), new Vector2(enemy.getOriginX(), enemy.getOriginY()).dst(player.getX(), player.getY())));
                }
            } else if (entity instanceof Player) {
                Circle circleNoise = ((Player) entity).getNoiseMarker();
                shapeRenderer.circle(circleNoise.x, circleNoise.y, circleNoise.radius);
            }
        }
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
