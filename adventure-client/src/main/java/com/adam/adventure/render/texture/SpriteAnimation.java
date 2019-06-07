package com.adam.adventure.render.texture;

import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.util.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpriteAnimation {

    private final List<Rectangle> animationFrames;
    private final long millisPerFrame;
    private boolean isLooping;
    private boolean isPaused;
    private long currentTime;
    private int currentFrame;


    private SpriteAnimation(final Builder builder) {
        this.animationFrames = builder.animationFrames;
        this.millisPerFrame = builder.millisPerFrame;
        this.isLooping = builder.isLooping;
        this.isPaused = true;
    }

    public void update(final float elapsedTime, final Sprite sprite) {
        if (!this.isPaused) {
            this.currentTime += elapsedTime;
            if (this.currentTime >= this.millisPerFrame) {
                currentTime = 0;
                incrementCurrentFrame();
                sprite.setTextureOffset(getCurrentFrame());
            }
        }
    }

    public Rectangle getCurrentFrame() {
        return animationFrames.get(currentFrame);
    }

    private void incrementCurrentFrame() {
        if (this.currentFrame + 1 < this.animationFrames.size()) {
            this.currentFrame++;
        } else {
            this.currentFrame = 0;
            if (!this.isLooping) {
                this.isPaused = true;
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(final boolean paused) {
        isPaused = paused;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(final boolean looping) {
        isLooping = looping;
    }

    public void reset() {
        this.currentFrame = 0;
    }


    public static class Builder {
        private final List<Rectangle> animationFrames;
        private final long millisPerFrame;
        private final boolean isLooping;

        public Builder(final long millisPerFrame, final boolean isLooping) {
            this.millisPerFrame = millisPerFrame;
            this.isLooping = isLooping;
            this.animationFrames = new ArrayList<>();
        }


        public Builder(final SpriteAnimation other) {
            this(other.millisPerFrame, other.isLooping);
            addAnimationFrames(other.animationFrames);
        }

        public Builder addAnimationFrame(final Rectangle frame) {
            animationFrames.add(frame);
            return this;
        }

        public Builder addAnimationFrames(final Collection<Rectangle> frames) {
            animationFrames.addAll(frames);
            return this;
        }

        public SpriteAnimation build() {
            return new SpriteAnimation(this);
        }
    }
}
