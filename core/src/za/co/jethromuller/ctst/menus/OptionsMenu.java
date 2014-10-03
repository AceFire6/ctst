package za.co.jethromuller.ctst.menus;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import za.co.jethromuller.ctst.CtstGame;

public class OptionsMenu extends Menu{

    private Rectangle sliderBarMusic;
    private Rectangle sliderBarSound;
    private Rectangle muteSound;
    private Rectangle muteMusic;

    private int barX = 104;

    private int musicSliderY = 255;
    private int soundSliderY = 184;

    private int sliderDistance = 200;


    public OptionsMenu(CtstGame game) {
        super(game, null, "options_menu.png");

        yCoords = new int[] {304, 233, 162, 122, 82, 28};
        xCoord = 90;

        sliderBarMusic = new Rectangle(barX + sliderDistance, musicSliderY, 15, 30);
        sliderBarSound = new Rectangle(barX + sliderDistance, soundSliderY, 15, 30);
        muteSound = new Rectangle(-313, -154, 18, 19);
        muteMusic = new Rectangle(-313, -112, 18, 19);
    }

    @Override
    protected void handleMenuOptions() {
        switch (option) {
            case 0:
            case 1:
                break;
            case 2:
                game.musicController.muteSound();
                muteSound.setPosition(-muteSound.x, -muteSound.y);
                break;
            case 3:
                game.musicController.muteMusic();
                muteMusic.setPosition(-muteMusic.x, -muteMusic.y);
                break;
            case 4:
                game.musicController.playSelectSound(1F, 0.25F, 0F);
                break;
            case 5:
                game.setScreen(new MainMenu(game));
                break;
        }
    }

    @Override
    protected void additionalShapeRender() {
        if (option == 5) {
            xCoord = 153;
        } else {
            xCoord = 90;
        }
        if (option == 0) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (game.musicController.getMusicVolume() > 0.2) {
                    game.musicController.setMusicVolume(-0.2F);
                } else {
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (game.musicController.getMusicVolume() < 1) {
                    game.musicController.setMusicVolume(0.2F);
                } else {
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                }
            }
        } else if (option == 1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (game.musicController.getSoundVolume() > 0.2) {
                    game.musicController.setSoundVolume(-0.2F);
                } else {
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (game.musicController.getSoundVolume() < 1) {
                    game.musicController.setSoundVolume(0.2F);
                } else {
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                }
            }
        }

        float musicOffset = (game.musicController.getMusicVolume() * sliderDistance);
        float soundOffset = (game.musicController.getSoundVolume() * sliderDistance);
        sliderBarMusic.setPosition(barX + musicOffset, musicSliderY);
        sliderBarSound.setPosition(barX + soundOffset, soundSliderY);

        shapeRenderer.setColor(Color.BLACK);

        shapeRenderer.rect(barX + musicOffset , musicSliderY, sliderBarMusic.getWidth(),
                           sliderBarMusic.getHeight());

        shapeRenderer.rect(barX + soundOffset, soundSliderY, sliderBarSound.getWidth(),
                           sliderBarSound.getHeight());

        shapeRenderer.rect(muteSound.getX(), muteSound.getY(), muteSound.getWidth(),
                           muteSound.getHeight());

        shapeRenderer.rect(muteMusic.getX(), muteMusic.getY(), muteMusic.getWidth(),
                           muteMusic.getHeight());
    }
}
