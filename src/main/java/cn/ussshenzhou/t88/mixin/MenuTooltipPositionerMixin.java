package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(MenuTooltipPositioner.class)
public class MenuTooltipPositionerMixin {

    @Shadow
    @Final
    private AbstractWidget widget;

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Inject(method = "positionTooltip", at = @At("RETURN"), cancellable = true)
    private void t88TScrollPanelCompatability(int p_283490_, int p_282509_, int p_282684_, int p_281703_, int p_281348_, int p_283657_, CallbackInfoReturnable<Vector2ic> cir) {
        if (widget instanceof TWidget tWidget) {
            cir.setReturnValue(cir.getReturnValue().add(0, -(int) tWidget.getParentScrollAmountIfExist(), (Vector2i) cir.getReturnValue()));
        }
    }
}
