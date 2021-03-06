package za.co.jethromuller.ctst;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import za.co.jethromuller.ctst.controllers.MusicController;
import za.co.jethromuller.ctst.controllers.TextureController;
import za.co.jethromuller.ctst.menus.MainMenu;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Main class that handles all the rendering and map and entities.
 */
public class CtstGame extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    public MusicController musicController;
    public TextureController textureController;
    public ArrayList<String> levelNames;
    public Preferences preferences;

    @Override
    public void create () {
        camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(400, 400, camera);
        viewport.apply(true);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        musicController = new MusicController();
        textureController = new TextureController();

        levelNames = new ArrayList<>();
        preferences = Gdx.app.getPreferences("CTST");

        try {
            System.out.println(Gdx.files.internal("levelNames.fn").exists());
            Collections.addAll(levelNames, Gdx.files.internal("levelNames.fn").readString().split("\n"));
            System.out.println(levelNames);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Level name list (levelNames.fn) not found.",
                                          "ERROR", JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }

        if (levelNames.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Level name list (levelNames.fn) empty.", "ERROR",
                                          JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        }

        this.setScreen(new MainMenu(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
