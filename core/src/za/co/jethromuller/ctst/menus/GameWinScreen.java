package za.co.jethromuller.ctst.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import za.co.jethromuller.ctst.CtstGame;

public class GameWinScreen extends Menu {

    public GameWinScreen(CtstGame game) {
        super(game, null, "game_win.png");
        game.musicController.playWinningMusic();
    }

    @Override
    protected void additionalBatchRender() {
        game.musicController.stopMenuMusic();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.musicController.stopWinningMusic();
            game.setScreen(new MainMenu(game));
        }
    }
}
