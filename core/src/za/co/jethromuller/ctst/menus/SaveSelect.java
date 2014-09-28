package za.co.jethromuller.ctst.menus;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import za.co.jethromuller.ctst.CtstGame;

public class SaveSelect implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private CtstGame game;
    private MainMenu menu;
    private Texture selectScreen;

    private int option = 0;
    private int[] yCoords = {277, 202, 133, 63};

    public SaveSelect(CtstGame game, MainMenu menu) {
        super();
        this.game = game;
        shapeRenderer = game.getShapeRenderer();
        batch = game.getBatch();

        this.menu = menu;
        selectScreen = new Texture(Gdx.files.internal ("save_select.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
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
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                    break;
                case 1:
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                    break;
                case 2:
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                    break;
                case 3:
                    game.setScreen(menu);
                    break;
            }
        }

        batch.begin();
        batch.draw(selectScreen, 0, 0);
        batch.end();

        int xInt;
        if (option == 3) {
            xInt = 106;
        } else {
            xInt = 56;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1F, 0.23F, 0.23F, 1);
        shapeRenderer.circle(xInt, yCoords[option], 10);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

    }

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

    }
}
