package cn.ussshenzhou.t88.mixin;


import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.gui.GuiComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(GuiComponent.class)
public class GuiComponentMixin {

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Inject(method = "slices", at = @At(value = "HEAD"), cancellable = true)
    private static void t88FixDividedByZeroWhenWidgetHasZeroWH(int a, int b, CallbackInfoReturnable<IntIterator> cir) {
        if (b == 0) {
            cir.setReturnValue(new Divisor(a, 0));
        }
    }
}
