package za.co.jethromuller.ctst.menus;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import za.co.jethromuller.ctst.CtstGame;

public class SaveSelect implements Screen {

    private Music backgroundMusic;
    private CtstGame game;

    public SaveSelect(CtstGame game, Music backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
        this.game = game;
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        backgroundMusic.play();
    }

    @Override
    public void hide() {
        backgroundMusic.pause();
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
