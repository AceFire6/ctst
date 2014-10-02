package za.co.jethromuller.ctst.menus;

import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;

public class GameOverScreen extends Menu {

    public GameOverScreen(CtstGame game, Level level) {
        super(game, level, "game_over.png");
        yCoords = new int[] {285, 240};
        xCoord = 150;
    }

    @Override
    protected void handleMenuOptions() {
        switch (option) {
            case 0:
                game.setScreen(new Level(game, currentLevel.getLevelName()));
                break;
            case 1:
                game.setScreen(new MainMenu(game));
                break;
        }
    }
}
