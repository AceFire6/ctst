package za.co.jethromuller.ctst.entities;


import za.co.jethromuller.ctst.Level;

public class Treasure extends Entity {

    public Treasure(Level level, float x, float y) {
        super(level, x, y, "entities/diamond.png");
    }

    public int collect() {
        dispose();
        return 100;
    }

    private void dispose() {
        currentLevel.killEntity(this);
        this.getTexture().dispose();
    }
}
