package cn.ussshenzhou.t88.mixin;

import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(AbstractContainerWidget.class)
public abstract class AbstractContainerWidgetMixin extends AbstractScrollArea implements ContainerEventHandler {

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public AbstractContainerWidgetMixin(int p_386878_, int p_387233_, int p_388234_, int p_386759_, Component p_388945_) {
        super(p_386878_, p_387233_, p_388234_, p_386759_, p_388945_);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    public void fixedMouseReleased(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        var res = super.mouseReleased(pMouseX, pMouseY, pButton);
        cir.setReturnValue(ContainerEventHandler.super.mouseReleased(pMouseX, pMouseY, pButton) || res);
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    public void fixedMouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY, CallbackInfoReturnable<Boolean> cir) {
        var res = super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        cir.setReturnValue(ContainerEventHandler.super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) || res);
    }
}
