package za.co.jethromuller.ctst;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TextureController {

    //ALLLLL the enemy textures
    private Texture enemy_up = new Texture("entities/enemy/enemy_up.png");
    private Texture enemy_down = new Texture("entities/enemy/enemy_down.png");
    private Texture enemy_left = new Texture("entities/enemy/enemy_left.png");
    private Texture enemy_right = new Texture("entities/enemy/enemy_right.png");

    private Texture enemy_upLeft = new Texture("entities/enemy/enemy_up_left.png");
    private Texture enemy_upRight = new Texture("entities/enemy/enemy_up_right.png");

    private Texture enemy_downLeft = new Texture("entities/enemy/enemy_down_left.png");
    private Texture enemy_downRight = new Texture("entities/enemy/enemy_down_right.png");

    //ALLLLL the player textures
    private Texture player_up = new Texture("entities/player/player_up.png");
    private Texture player_down = new Texture("entities/player/player_down.png");
    private Texture player_left = new Texture("entities/player/player_left.png");
    private Texture player_right = new Texture("entities/player/player_right.png");

    private Texture player_upLeft = new Texture("entities/player/player_up_left.png");
    private Texture player_upRight = new Texture("entities/player/player_up_right.png");

    private Texture player_downLeft = new Texture("entities/player/player_down_left.png");
    private Texture player_downRight = new Texture("entities/player/player_down_right.png");

    public TextureController() {

    }

    public Texture getEnemy_down() {
        return enemy_down;
    }

    public Texture getEnemy_left() {
        return enemy_left;
    }

    public Texture getEnemy_right() {
        return enemy_right;
    }

    public Texture getEnemy_upLeft() {
        return enemy_upLeft;
    }

    public Texture getEnemy_upRight() {
        return enemy_upRight;
    }

    public Texture getEnemy_downLeft() {
        return enemy_downLeft;
    }

    public Texture getEnemy_downRight() {
        return enemy_downRight;
    }

    public Texture getEnemy_up() {
        return enemy_up;
    }


    public Texture getPlayer_up() {
        return player_up;
    }

    public Texture getPlayer_down() {
        return player_down;
    }

    public Texture getPlayer_left() {
        return player_left;
    }

    public Texture getPlayer_right() {
        return player_right;
    }

    public Texture getPlayer_upLeft() {
        return player_upLeft;
    }

    public Texture getPlayer_upRight() {
        return player_upRight;
    }

    public Texture getPlayer_downLeft() {
        return player_downLeft;
    }

    public Texture getPlayer_downRight() {
        return player_downRight;
    }

    public Animation getAnimation(String path, int steps, float timing) {
        Array<TextureRegion> animTextures = new Array<>(steps);
        for (int i = 0; i < steps; i++) {
            String framePath = (path + "*.png").replace("*", String.valueOf(i));
            animTextures.add(new TextureRegion(new Texture(framePath)));
        }
        return new Animation(timing, animTextures);
    }
}
