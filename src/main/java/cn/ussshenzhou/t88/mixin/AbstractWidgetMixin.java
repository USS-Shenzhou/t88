package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.AbstractWidget;
import org.objectweb.asm.Opcodes;
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
    public abstract int getX();

    @Shadow
    public abstract int getY();

    @Shadow
    protected boolean isHovered;

    @Shadow
    protected int width;

    @Shadow
    protected int height;

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/AbstractWidget;isHovered:Z", opcode = Opcodes.PUTFIELD))
    private void t88TScrollPanelCompatability(AbstractWidget instance, boolean value, @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y) {
        if (instance instanceof TWidget tWidget) {
            var scroll = tWidget.getParentScroll();
            this.isHovered = tWidget.isInRange(x + scroll.x, y + scroll.y);
            return;
        }
        this.isHovered = value;
    }

    /*@Redirect(method = "updateTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/InputType;isKeyboard()Z"))
    private boolean t88IgnoreTooltipRequireLastInput(InputType instance) {
        if (this instanceof TWidget) {
            return true;
        } else {
            return Minecraft.getInstance().getLastInputType().isKeyboard();
        }
    }*/
}
