package za.co.jethromuller.ctst;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Main class that handles all the rendering and map and entities.
 */
public class CtstMain extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;
    private Level currentLevel;

    @Override
    public void create () {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(400, 400, camera);
        viewport.apply(true);

        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        Level level1 = new Level("level1", camera);
        currentLevel = level1;
        Player player = new Player(currentLevel, 200, 200, "entities/player_down.png");

        currentLevel.addEntity(player);
        currentLevel.addEntity(new Entity(currentLevel, 50, 150, "entities/enemy.png"));
        currentLevel.addEntity(new Entity(currentLevel, 180, 50, "entities/enemy.png"));
    }

    @Override
    public void render () {
        //Drawing
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer = new OrthogonalTiledMapRenderer(currentLevel.getGameMap());
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        // Draws the bounding boxes if the spacebar is pressed.

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 0, 1);
            currentLevel.drawBounds(shapeRenderer);
            currentLevel.drawEntities(true, batch, shapeRenderer);
            shapeRenderer.end();
        } else {
            currentLevel.drawEntities(false, batch, shapeRenderer);
        }
        batch.end();
    }
}
