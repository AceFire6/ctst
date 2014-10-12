package za.co.jethromuller.ctst.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;

public class MainMenu extends Menu {

    private String firstLevelName;

    public MainMenu(CtstGame game) {
        super(game, null, "main_menu.png");
        yCoords = new int[] {224, 180, 140};
        xCoord = 104;

        OptionsMenu.setOptions(game);
        game.musicController.startMenuMusic();

        checkForPreviousPlay();
    }

    private void checkForPreviousPlay() {
        Preferences prefs = Gdx.app.getPreferences("CTST");
        if (prefs.contains("lastLevel")) {
            firstLevelName = prefs.getString("lastLevel");
            menuTexture = new Texture(Gdx.files.internal("menus/main_menu_continue.png"));
        } else {
            firstLevelName = game.levelNames.get(0);
        }
    }

    @Override
    protected void handleMenuOptions() {
        switch (option) {
            case 0:
                game.setScreen(new Level(game, firstLevelName, game.levelNames.indexOf(firstLevelName)));
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
