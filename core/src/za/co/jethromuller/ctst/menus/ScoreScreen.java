package za.co.jethromuller.ctst.menus;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;

public class ScoreScreen extends Menu {
    private BitmapFont font;
    private int nextLevelIndex;


    public ScoreScreen(CtstGame game, Level currentLevel) {
        super(game, currentLevel, "score_screen.png");
        yCoords = new int[] {84, 50};
        xCoord = 115;
        font = new BitmapFont();

        nextLevelIndex = currentLevel.getLevelIndex() + 1;
        game.preferences.putString("lastLevel", currentLevel.getLevelName());
        game.preferences.flush();
    }

    @Override
    public void handleMenuOptions() {
        switch (option) {
            case 0:
                if (nextLevelIndex >= game.levelNames.size()) {
                    game.setScreen(new GameWinScreen(game));
                } else {
                    Level nextLevel = new Level(game, game.levelNames.get(nextLevelIndex), nextLevelIndex);
                    game.setScreen(nextLevel);
                }
                break;
            case 1:
                game.setScreen(new MainMenu(game));
                break;
        }

    }

    @Override
    public void additionalBatchRender() {
        font.setScale(1.2F);
        font.draw(batch, String.valueOf(((int) currentLevel.getScore())), 170, 295);
        font.draw(batch, String.valueOf(currentLevel.getTime()), 170, 254);
        font.draw(batch, currentLevel.getStealthy(), 170, 210);
    }
}
