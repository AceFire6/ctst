package za.co.jethromuller.ctst.menus;

import com.badlogic.gdx.Gdx;
import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;

public class MainMenu extends Menu {

    public MainMenu(CtstGame game) {
        super(game, null, "main_menu.png");
        yCoords = new int[] {224, 180, 140};
        xCoord = 104;

        OptionsMenu.setOptions(game);
        game.musicController.startMenuMusic();
    }

    @Override
    protected void handleMenuOptions() {
        switch (option) {
            case 0:
                Level level1 = new Level(game, "level1");
                game.setScreen(level1);
                break;
            case 1:
                game.setScreen(new OptionsMenu(game));
                break;
            case 2:
                Gdx.app.exit();
                break;
        }
    }
}
