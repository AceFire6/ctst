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

public class ScoreScreen implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private CtstGame game;
    private Texture scoreScreen;

    private int option = 0;
    private int[] yCoords = {84, 50};
    private Level currentLevel;

    public ScoreScreen(CtstGame game, Level currentLevel) {
        super();
        this.game = game;
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();
        scoreScreen = new Texture(Gdx.files.internal ("score_screen.png"));
        this.currentLevel = currentLevel;
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
                    game.setScreen(currentLevel);
                    break;
                case 1:
                    game.setScreen(new MainMenu(game));
                    break;
            }
        }

        batch.begin();
        batch.draw(scoreScreen, 0, 0);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1F, 0.23F, 0.23F, 1);
        shapeRenderer.circle(115, yCoords[option], 10);
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
