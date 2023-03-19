package cn.ussshenzhou.t88.mixin;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author USS_Shenzhou
 */
@Mixin(OptionInstance.OptionInstanceSliderButton.class)
public interface SliderButtonAccessor {

    @Mutable
    @Accessor("instance")
    void setOption(OptionInstance<?> option);

    @Mutable
    @Accessor
    void setTooltipSupplier(OptionInstance.TooltipSupplier<?> tooltipSupplier);
}
