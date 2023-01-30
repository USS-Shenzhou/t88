package cn.ussshenzhou.t88.mixin;

import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author USS_Shenzhou
 */
@Mixin(TextComponent.class)
public interface TextComponentAccessor {
    @Mutable
    @Accessor
    void setText(String text);
}
