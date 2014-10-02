package za.co.jethromuller.ctst.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;


public class PauseMenu extends Menu {

    public PauseMenu(CtstGame game, Level currentLevel) {
        super(game, currentLevel, "pause_menu.png");
        yCoords = new int[] {275, 235};
        xCoord = 128;
    }

    @Override
    protected void handleMenuOptions() {
        switch (option) {
            case 0:
                game.setScreen(currentLevel);
                break;
            case 1:
                game.setScreen(new MainMenu(game));
                break;
        }
    }

    @Override
    protected void additionalRender() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(currentLevel);
        }
    }

    @Override
    public void show() {

    }
}
