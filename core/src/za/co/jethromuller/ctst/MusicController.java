package za.co.jethromuller.ctst;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;

public class MusicController {
    private Music[] songs;
    private Music menuMusic;
    private Sound selectSound;
    private final String menuMusicName = "Breaking_In";
    private final String[] songNames = {"Enthalpy", "Fuckaboing", "OHC3", "Sea_Battles_in_Space",
                                        "Submerged"};
    private int currentSelection = 0;


    public MusicController() {
        songs = new Music[songNames.length];
        String musicPath = "music/*.ogg";
        for (int i = 0; i < songs.length; i++) {
            songs[i] = Gdx.audio.newMusic(Gdx.files.internal(musicPath.replace("*", songNames[i])));
            songs[i].setOnCompletionListener(onCompletionListener);
        }

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath.replace("*", menuMusicName)));
        menuMusic.setVolume(0.6F);
        menuMusic.setLooping(true);

        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/main_menu_select.ogg"));
    }

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(Music music) {
            currentSelection = ((currentSelection + 1) % songNames.length);
            songs[currentSelection].play();
            songs[currentSelection].setVolume(0.5f);
        }
    };

    public void startGameMusic() {
        stopMenuMusic();
        songs[currentSelection].play();
    }

    public void pauseGameMusic() {
        songs[currentSelection].pause();
    }

    public void startMenuMusic() {
        pauseGameMusic();
        menuMusic.play();
    }

    public void stopMenuMusic() {
        menuMusic.stop();
    }

    public void playSelectSound() {
        selectSound.play();
    }

    public void playSelectSound(float volume, float pitch, float pan) {
        selectSound.play(volume, pitch, pan);
    }
}
