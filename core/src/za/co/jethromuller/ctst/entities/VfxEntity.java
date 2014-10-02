package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import za.co.jethromuller.ctst.Level;

public class VfxEntity extends Entity {

    private float stateTime;
    private Animation animation;
    private boolean destroyOnComplete;

    public VfxEntity(Level level, float x, float y, String filePath, int steps, float timing,
                     boolean destroyOnCompletion) {
        super(level, x, y, filePath + "0.png");
        animation = currentLevel.getGame().textureController.getAnimation(filePath, steps, timing);
        stateTime = 0f;
        destroyOnComplete = destroyOnCompletion;
        setCollidable(false);
    }

    @Override
    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();
    }

    public TextureRegion getKeyframe() {
        if (animation.isAnimationFinished(stateTime)) {
            if (destroyOnComplete) {
                this.dispose();
            } else {
                stateTime = Gdx.graphics.getDeltaTime();
                return animation.getKeyFrame(stateTime, true);
            }
        } else {
            stateTime += Gdx.graphics.getDeltaTime();
            return animation.getKeyFrame(stateTime, true);
        }
        return null;
    }

    public void dispose() {
        currentLevel.killEntity(this);
        this.getTexture().dispose();
    }
}
