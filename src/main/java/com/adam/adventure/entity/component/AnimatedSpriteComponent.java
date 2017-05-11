package com.adam.adventure.entity.component;

import com.adam.adventure.entity.SpriteEntity;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class AnimatedSpriteComponent implements EntityComponent<SpriteEntity> {

    private final long millisPerFrame;
    private final boolean isLooping;
    private final List<Rectangle> animationFrames;
    private boolean isPaused;
    private long currentTime;
    private int currentFrame;

    private AnimatedSpriteComponent(final Builder builder) {
        this.millisPerFrame = builder.millisPerFrame;
        this.isLooping = builder.isLooping;
        this.animationFrames = builder.animationFrames;
        this.currentTime = 0;
        this.currentFrame = 0;
        this.isPaused = true;
    }

    //TODO all these variables are specific to the entity - how do we handle this if we want to use the same component?
    @Override
    public void update(final SpriteEntity target, final float deltaTime, final ComponentContainer componentContainer) {
        if (!this.isPaused) {
            this.currentTime += deltaTime;
            if (this.currentTime >= this.millisPerFrame) {
                currentTime = 0;
                if (this.currentFrame + 1 < this.animationFrames.size()) {
                    this.currentFrame++;
                } else {
                    this.currentFrame = 0;

                    if (!this.isLooping) {
                        this.isPaused = true;
                    }
                }

                setFrame(target, this.currentFrame);
            }
        }
    }

    @Override
    public void onComponentEvent(final ComponentEvent componentEvent) {
        if (componentEvent == ComponentEvent.START_ANIMATION) {
            this.isPaused = false;
        } else if (componentEvent == ComponentEvent.STOP_ANIMATION) {
            this.isPaused = true;
        }
    }

    private void setFrame(final SpriteEntity target, final int frameIndex) {
        final Rectangle currentFrame = this.animationFrames.get(frameIndex);
        target.getSprite().setTextureOffset(currentFrame);
    }


    public static class Builder {
        private final long millisPerFrame;
        private final boolean isLooping;
        private final List<Rectangle> animationFrames;

        public Builder(final long millisPerFrame, final boolean isLooping) {
            this.millisPerFrame = millisPerFrame;
            this.isLooping = isLooping;
            this.animationFrames = new ArrayList<>();
        }

        public Builder addAnimationFrame(final Rectangle frame) {
            animationFrames.add(frame);
            return this;
        }

        public AnimatedSpriteComponent build() {
            return new AnimatedSpriteComponent(this);
        }
    }
}
