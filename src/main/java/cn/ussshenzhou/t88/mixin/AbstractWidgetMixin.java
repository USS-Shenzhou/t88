package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
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
    private void t88TScrollPanelCompatability(AbstractWidget instance, boolean value) {
        if (instance instanceof TWidget tWidget) {
            var mouseX = MouseHelper.getMouseX();
            var mouseY = MouseHelper.getMouseY();

            var scrollContainer = tWidget.getParentInstanceOf(TVerticalScrollContainer.class);
            if (scrollContainer != null) {
                if (scrollContainer.isInRange(mouseX, mouseY)) {
                    var scroll = tWidget.getParentScroll();
                    this.isHovered = mouseX >= this.getX() - scroll.x
                            && mouseY >= this.getY() - scroll.y
                            && mouseX < this.getX() + this.width - scroll.x
                            && mouseY < this.getY() + this.height - scroll.y;
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
