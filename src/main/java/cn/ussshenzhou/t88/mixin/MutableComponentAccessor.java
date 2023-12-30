package cn.ussshenzhou.t88.mixin;

import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;


/**
 * @author USS_Shenzhou
 */
@Mixin(MutableComponent.class)
public interface MutableComponentAccessor {
    @Mutable
    @Accessor
    void setContents(ComponentContents contents);
}
