package za.co.jethromuller.ctst.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;

public class MainMenu extends Menu {

    private String firstLevelName;
    private boolean canContinue;

    public MainMenu(CtstGame game) {
        super(game, null, "main_menu.png");;
        xCoord = 104;

        OptionsMenu.setOptions(game);
        game.musicController.startMenuMusic();

        checkForPreviousPlay();

        if (!canContinue) {
            yCoords = new int[] {193, 149, 109};
        } else {
            yCoords = new int[] {233, 193, 149, 109};
        }
    }

    private void checkForPreviousPlay() {
        Preferences prefs = Gdx.app.getPreferences("CTST");
        if (prefs.contains("lastLevel")) {
            firstLevelName = prefs.getString("lastLevel");
            menuTexture = new Texture(Gdx.files.internal("menus/main_menu_continue.png"));
            canContinue = true;
        } else {
            firstLevelName = game.levelNames.get(0);
            canContinue = false;
        }
    }

    @Override
    protected void handleMenuOptions() {
        int tempOptions = option;
        if (!canContinue) {
            tempOptions += 1;
        }
        switch (tempOptions) {
            case 0:
                game.setScreen(new Level(game, firstLevelName, game.levelNames.indexOf(firstLevelName)));
                break;
            case 1:
                game.setScreen(new Level(game, game.levelNames.get(0),
                                         game.levelNames.indexOf(game.levelNames.get(0))));
                break;
            case 2:
                game.setScreen(new OptionsMenu(game));
                break;
            case 3:
                Gdx.app.exit();
                break;
        }
    }
}
