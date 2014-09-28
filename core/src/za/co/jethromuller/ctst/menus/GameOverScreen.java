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

public class GameOverScreen implements Screen {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private CtstGame game;
    private Texture gameOver;

    private int option = 0;
    private int[] yCoords = {285, 240};
    private Level currentLevel;

    public GameOverScreen(CtstGame game, Level level) {
        super();
        this.game = game;
        currentLevel = level;
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();

        gameOver = new Texture(Gdx.files.internal("game_over.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            option = (option > 0 ? option - 1: option);
            game.musicController.playSelectSound();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            option = (option < 1 ? option + 1: option);
            game.musicController.playSelectSound();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (option) {
                case 0:
                    game.setScreen(new Level(game, currentLevel.getLevelName()));
                    break;
                case 1:
                    game.setScreen(new MainMenu(game));
                    break;
            }
        }

        batch.begin();
        batch.draw(gameOver, 0, 0);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1F, 0.23F, 0.23F, 1);
        shapeRenderer.circle(150, yCoords[option], 10);
        shapeRenderer.end();
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
