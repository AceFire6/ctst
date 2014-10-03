package za.co.jethromuller.ctst.menus;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import za.co.jethromuller.ctst.CtstGame;
import za.co.jethromuller.ctst.Level;

public class ScoreScreen extends Menu {
    private BitmapFont font;


    public ScoreScreen(CtstGame game, Level currentLevel) {
        super(game, currentLevel, "score_screen.png");
        yCoords = new int[] {84, 50};
        xCoord = 115;
        font = new BitmapFont();
    }

    @Override
    public void handleMenuOptions() {
        switch (option) {
            case 0:
                Level level2 = new Level(game, "level2");
                game.setScreen(level2);
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
