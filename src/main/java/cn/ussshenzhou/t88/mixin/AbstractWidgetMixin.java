package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.container.TScrollContainer;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import org.checkerframework.checker.units.qual.A;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
    private void t88TScrollPanelCompatability(AbstractWidget instance, boolean value) {
        if (instance instanceof TWidget tWidget) {
            var mouseX = MouseHelper.getMouseX();
            var mouseY = MouseHelper.getMouseY();

            var scrollContainer = tWidget.getParentInstanceOf(TScrollContainer.class);
            if (scrollContainer != null) {
                if (scrollContainer.isInRange(mouseX, mouseY)) {
                    var scroll = tWidget.getParentScrollAmountIfExist();
                    this.isHovered = mouseX >= this.getX()
                            && mouseY >= this.getY() - scroll
                            && mouseX < this.getX() + this.width
                            && mouseY < this.getY() + this.height - scroll;
                } else {
                    this.isHovered = false;
                }
                return;
            }
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
