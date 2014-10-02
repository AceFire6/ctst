package za.co.jethromuller.ctst.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import za.co.jethromuller.ctst.Level;

public class VfxEntity extends Entity {

    private float stateTime;
    private Animation animation;

    public VfxEntity(Level level, float x, float y, String filePath, int steps, float timing,
                     boolean destroyOnCompletion) {
        super(level, x, y, filePath + "0.png");
        animation = currentLevel.getGame().textureController.getAnimation("vfx/sound_ripple/",
                                                                        steps, timing);
        stateTime = 0f;
        setCollidable(false);
    }

    @Override
    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();
    }

    public TextureRegion getKeyframe() {
        if (animation.isAnimationFinished(stateTime)) {
            this.dispose();
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