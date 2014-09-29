package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.graphics.Texture;

public class EnemyTextureController {

    //ALLLLL the textures
    private Texture up = new Texture("entities/enemy/enemy_up.png");
    private Texture down = new Texture("entities/enemy/enemy_down.png");
    private Texture left = new Texture("entities/enemy/enemy_left.png");
    private Texture right = new Texture("entities/enemy/enemy_right.png");

    private Texture upLeft = new Texture("entities/enemy/enemy_up_left.png");
    private Texture upRight = new Texture("entities/enemy/enemy_up_right.png");

    private Texture downLeft = new Texture("entities/enemy/enemy_down_left.png");
    private Texture downRight = new Texture("entities/enemy/enemy_down_right.png");

    public EnemyTextureController() {

    }

    public Texture getDown() {
        return down;
    }

    public Texture getLeft() {
        return left;
    }

    public Texture getRight() {
        return right;
    }

    public Texture getUpLeft() {
        return upLeft;
    }

    public Texture getUpRight() {
        return upRight;
    }

    public Texture getDownLeft() {
        return downLeft;
    }

    public Texture getDownRight() {
        return downRight;
    }

    public Texture getUp() {
        return up;
    }
}
