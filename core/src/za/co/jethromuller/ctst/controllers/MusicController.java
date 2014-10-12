package za.co.jethromuller.ctst.controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;

public class MusicController {
    private Music[] songs;
    private Music menuMusic;
    private Music winningMusic;
    private Sound selectSound;
    private Sound collectSound;
    private Sound walkSound;
    private Sound deathSound;

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

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath.replace("*", "Breaking_In")));
        menuMusic.setVolume(menuVolume * muteMusic * musicVolume);
        menuMusic.setLooping(true);

        winningMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath.replace("*", "Moduless")));

        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/main_menu_select.ogg"));
        collectSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/collect_treasure.ogg"));
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/walk.wav"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/player_death.ogg"));
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

    public void playWinningMusic() {
        stopMenuMusic();
        winningMusic.play();
        winningMusic.setVolume(songVolume * muteMusic * musicVolume);
        winningMusic.setLooping(true);
    }

    public void stopWinningMusic() {
        winningMusic.stop();
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

    public void playDeathSound(float volume, float pitch, float pan) {
        if (muteSound == 1) {
            deathSound.play(volume * soundVolume, pitch, pan);
        }
    }

    public void playSelectSound(float volume, float pitch, float pan) {
        if (muteSound == 1) {
            selectSound.play(volume * soundVolume, pitch, pan);
        }
    }

    public void unMuteMusic() {
        muteMusic = 1;
        updateMusicVolumes();
    }

    public void muteMusic() {
        muteMusic = 0;
        updateMusicVolumes();
    }

    public boolean isMusicMuted() {
        return (muteMusic == 0);
    }

    public boolean isSoundMuted() {
        return (muteSound == 0);
    }

    public void unMuteSound() {
        muteSound = 1;
    }

    public void muteSound() {
        muteSound = 0;
    }

    public void setMusicVolume(float newVolume) {
        musicVolume = newVolume;
        updateMusicVolumes();
    }

    private void updateMusicVolumes() {
        songs[currentSelection].setVolume(musicVolume * musicVolume * muteMusic);
        menuMusic.setVolume(menuVolume * musicVolume * muteMusic);
    }

    public void setSoundVolume(float newVolume) {
        soundVolume = newVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }
}
