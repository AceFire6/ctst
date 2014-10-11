package za.co.jethromuller.ctst.menus;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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

    private Preferences options;
    private boolean musicMuted;
    private boolean soundMuted;


    public OptionsMenu(CtstGame game) {
        super(game, null, "options_menu.png");
        options = Gdx.app.getPreferences("CTST");

        yCoords = new int[] {304, 233, 162, 122, 82, 28};
        xCoord = 90;

        muteSound = new Rectangle(313, 154, 18, 19);
        muteMusic = new Rectangle(313, 112, 18, 19);

        float musicOffset = (game.musicController.getMusicVolume() * sliderDistance);
        float soundOffset = (game.musicController.getSoundVolume() * sliderDistance);
        sliderBarMusic = new Rectangle(barX + musicOffset, musicSliderY, 15, 30);
        sliderBarSound = new Rectangle(barX + soundOffset, soundSliderY, 15, 30);

        musicMuted = game.musicController.isMusicMuted();
        soundMuted = game.musicController.isSoundMuted();
    }

    @Override
    protected void handleMenuOptions() {
        switch (option) {
            case 0:
            case 1:
                break;
            case 2:
                game.musicController.muteSound();
                soundMuted = !soundMuted;
                break;
            case 3:
                game.musicController.muteMusic();
                musicMuted = !musicMuted;
                break;
            case 4:
                game.musicController.playSelectSound(1F, 0.25F, 0F);
                options.clear();
                musicMuted = false;
                soundMuted = false;
                game.musicController.unMuteMusic();
                game.musicController.unMuteSound();
                game.musicController.setMusicVolume(1F);
                game.musicController.setSoundVolume(1F);
                break;
            case 5:
                String optionCsv = game.musicController.getMusicVolume() + "," +
                                   game.musicController.getSoundVolume() + "," +
                                   String.valueOf(soundMuted) + "," +
                                   String.valueOf(musicMuted);
                options.putString("options", optionCsv);
                options.flush();
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
            float musicVol = game.musicController.getMusicVolume();
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (musicVol > 0.2) {
                    game.musicController.setMusicVolume(musicVol - 0.2F);
                } else {
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (musicVol < 1) {
                    game.musicController.setMusicVolume(musicVol + 0.2F);
                } else {
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                }
            }
        } else if (option == 1) {
            float soundVol = game.musicController.getSoundVolume();
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (soundVol > 0.2) {
                    game.musicController.setSoundVolume(soundVol - 0.2F);
                } else {
                    game.musicController.playSelectSound(1F, 0.25F, 0F);
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (soundVol < 1) {
                    game.musicController.setSoundVolume(soundVol + 0.2F);
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

        if (soundMuted) {
            shapeRenderer.rect(muteSound.getX(), muteSound.getY(), muteSound.getWidth(), muteSound.getHeight());

        }
        if (musicMuted) {
            shapeRenderer.rect(muteMusic.getX(), muteMusic.getY(), muteMusic.getWidth(), muteMusic.getHeight());

        }
    }

    public static void setOptions(CtstGame game) {
        Preferences options = Gdx.app.getPreferences("CTST");
        if (options.contains("options")) {
            String[] optionValues = options.getString("options").split(",");
            game.musicController.setMusicVolume(Float.parseFloat(optionValues[0]));
            game.musicController.setSoundVolume(Float.parseFloat(optionValues[1]));
            if (Boolean.parseBoolean(optionValues[2])) {
                game.musicController.muteSound();
            } else {
                game.musicController.unMuteSound();
            }
            if (Boolean.parseBoolean(optionValues[3])) {
                game.musicController.muteMusic();
            } else {
                game.musicController.unMuteMusic();
            }
        }
    }
}
