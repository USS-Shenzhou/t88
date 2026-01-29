package cn.ussshenzhou.t88.mixin;

import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.input.MouseButtonEvent;
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

    public AbstractContainerWidgetMixin(int x, int y, int width, int height, Component message, ScrollbarSettings scrollbarSettings) {
        super(x, y, width, height, message, scrollbarSettings);
    }


    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    public void fixedMouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        var res = super.mouseReleased(event);
        cir.setReturnValue(ContainerEventHandler.super.mouseReleased(event) || res);
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    public void fixedMouseDragged(MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        var res = super.mouseDragged(event, dx, dy);
        cir.setReturnValue(ContainerEventHandler.super.mouseDragged(event, dx, dy) || res);
    }
}
