package cn.ussshenzhou.t88.mixin;


import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    //needtest
    /*@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Inject(method = "slices", at = @At(value = "HEAD"), cancellable = true)
    private static void t88FixDividedByZeroWhenWidgetHasZeroWH(int a, int b, CallbackInfoReturnable<IntIterator> cir) {
        if (b == 0) {
            cir.setReturnValue(new Divisor(a, 0));
        }
    }*/
}
