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

public class MainMenu implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture menu;
    private CtstGame game;
    private int option = 0;
    private int[] yCoords = {224, 180, 140, 98};

    public MainMenu(CtstGame game) {
        super();
        this.game = game;

        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();

        menu = new Texture(Gdx.files.internal("main_menu.png"));
        game.musicController.startMenuMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            option = (option > 0 ? option - 1: option);
            game.musicController.playSelectSound();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            option = (option < 3 ? option + 1: option);
            game.musicController.playSelectSound();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (option) {
                case 0:
                    Level level1 = new Level(game, "level1");
                    game.setScreen(level1);
                    break;
                case 1:
                    game.setScreen(new SaveSelect(game, this));
                    break;
                case 2:
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                    break;
                case 3:
                    Gdx.app.exit();
                    break;
            }
        }

        batch.begin();
        batch.draw(menu, 0, 0);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1F, 0.23F, 0.23F, 1);
        shapeRenderer.circle(104, yCoords[option], 10);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void show() {

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
        batch.dispose();
        shapeRenderer.dispose();
        menu.dispose();
    }
}
