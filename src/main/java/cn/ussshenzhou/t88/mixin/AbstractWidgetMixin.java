package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author USS_Shenzhou
 */
@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @Shadow
    public abstract int getY();

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;getY()I"))
    private int t88TScrollPanelCompatability(AbstractWidget instance) {
        if (instance instanceof TWidget tWidget) {
            return (int) (getY() - tWidget.getParentScrollAmountIfExist());
        } else {
            return getY();
        }
    }

    @Redirect(method = "updateTooltip",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/InputType;isKeyboard()Z"))
    private boolean t88IgnoreTooltipRequireLastInput(InputType instance){
        if (this instanceof TWidget){
            return true;
        } else {
            return Minecraft.getInstance().getLastInputType().isKeyboard();
        }
    }
}
