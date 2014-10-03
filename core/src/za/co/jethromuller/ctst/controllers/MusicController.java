package za.co.jethromuller.ctst.controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;

public class MusicController {
    private Music[] songs;
    private Music menuMusic;
    private Sound selectSound;
    private Sound collectSound;
    private Sound walkSound;
    private final String[] songNames = {"Enthalpy", "Fuckaboing", "OHC3", "Sea_Battles_in_Space",
                                        "Submerged"};
    private int currentSelection = 0;

    private int muteMusic = 1;
    private int muteSound = 1;

    private float musicVolume = 1F;
    private float soundVolume = 1F;

    private float songVolume = 0.3F;
    private float menuVolume = 0.2F;


    public MusicController() {
        songs = new Music[songNames.length];
        String musicPath = "music/*.ogg";
        for (int i = 0; i < songs.length; i++) {
            songs[i] = Gdx.audio.newMusic(Gdx.files.internal(musicPath.replace("*", songNames[i])));
            OnCompletionListener onCompletionListener = new OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    currentSelection = ((currentSelection + 1) % songNames.length);
                    songs[currentSelection].play();
                    songs[currentSelection].setVolume(songVolume * muteMusic * musicVolume);
                }
            };
            songs[i].setOnCompletionListener(onCompletionListener);
        }

        String menuMusicName = "Breaking_In";
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath.replace("*", menuMusicName)));
        menuMusic.setVolume(menuVolume * muteMusic * musicVolume);
        menuMusic.setLooping(true);

        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/main_menu_select.ogg"));
        collectSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/collect_treasure.ogg"));
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/walk.wav"));
    }

    public void startGameMusic() {
        stopMenuMusic();
        songs[currentSelection].play();
        songs[currentSelection].setVolume(songVolume * muteMusic * musicVolume);
    }

    public void pauseGameMusic() {
        songs[currentSelection].pause();
    }

    public void startMenuMusic() {
        pauseGameMusic();
        menuMusic.play();
        menuMusic.setVolume(menuVolume * muteMusic * musicVolume);
    }

    public void stopMenuMusic() {
        menuMusic.stop();
    }

    public void playSelectSound() {
        if (muteSound == 1) {
            selectSound.play(1F * soundVolume);
        }
    }

    public void playCollectSound() {
        if (muteSound == 1) {
            collectSound.play(1F * soundVolume);
        }
    }

    public void playWalkSound(float volume, float pitch, float pan) {
        if (muteSound == 1) {
            walkSound.play(volume * soundVolume, pitch, pan);
        }
    }

    public void playSelectSound(float volume, float pitch, float pan) {
        if (muteSound == 1) {
            selectSound.play(volume * soundVolume, pitch, pan);
        }
    }

    public void muteMusic() {
        muteMusic = ((muteMusic == 1) ? 0 : 1);
        updateMusicVolumes();
    }

    public void muteSound() {
        muteSound = ((muteSound == 1) ? 0 : 1);
    }

    public void setMusicVolume(float volOffset) {
        musicVolume += volOffset;
        updateMusicVolumes();
    }

    private void updateMusicVolumes() {
        songs[currentSelection].setVolume(musicVolume * musicVolume * muteMusic);
        menuMusic.setVolume(menuVolume * musicVolume * muteMusic);
    }

    public void setSoundVolume(float volOffset) {
        soundVolume += volOffset;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }
}
