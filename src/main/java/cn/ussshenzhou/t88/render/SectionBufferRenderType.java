package cn.ussshenzhou.t88.render;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author USS_Shenzhou
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SectionBufferRenderType {

    At value() default At.AFTER_CUTOUT_BLOCKS;

    int priority() default 100;

    public enum At {
        AFTER_SKY(RenderLevelStageEvent.Stage.AFTER_SKY),
        AFTER_SOLID_BLOCKS(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS),
        AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS(RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS),
        AFTER_CUTOUT_BLOCKS(RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS),

        AFTER_ENTITIES(RenderLevelStageEvent.Stage.AFTER_ENTITIES),
        AFTER_BLOCK_ENTITIES(RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES),

        AFTER_TRANSLUCENT_BLOCKS(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS),
        AFTER_TRIPWIRE_BLOCKS(RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS),

        AFTER_PARTICLES(RenderLevelStageEvent.Stage.AFTER_PARTICLES),
        AFTER_WEATHER(RenderLevelStageEvent.Stage.AFTER_WEATHER),
        AFTER_LEVEL(RenderLevelStageEvent.Stage.AFTER_LEVEL);

        private final RenderLevelStageEvent.Stage stage;

        At(RenderLevelStageEvent.Stage stage) {
            this.stage = stage;
        }

        public RenderLevelStageEvent.Stage get() {
            return stage;
        }
    }
}
