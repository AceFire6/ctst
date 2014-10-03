package za.co.jethromuller.ctst.menus;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;

public class Menu implements Screen {
    protected SpriteBatch batch;
    protected ShapeRenderer shapeRenderer;

    protected CtstGame game;
    protected Texture menuTexture;

    protected int option = 0;
    protected int[] yCoords;
    protected Level currentLevel;

    protected int xCoord;

    public Menu(CtstGame game, Level level, String filePath) {
        super();
        this.game = game;
        currentLevel = level;
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();

        menuTexture = new Texture(Gdx.files.internal("menus/" + filePath));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Override this
        handleControls();

        batch.begin();
        batch.draw(menuTexture, 0, 0);
        additionalBatchRender();
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        additionalShapeRender();
        shapeRenderer.setColor(1F, 0.23F, 0.23F, 1);
        shapeRenderer.circle(xCoord, yCoords[option], 10);
        shapeRenderer.end();
    }

    private void handleControls() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            option = (option > 0 ? option - 1: option);
            game.musicController.playSelectSound();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            option = (option < (yCoords.length - 1) ? option + 1: option);
            game.musicController.playSelectSound();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleMenuOptions();
        }
    }

    protected void additionalBatchRender() {

    }

    protected void additionalShapeRender() {

    }

    protected void handleMenuOptions() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        game.musicController.startMenuMusic();
    }

    @Override
    public void hide() {

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
