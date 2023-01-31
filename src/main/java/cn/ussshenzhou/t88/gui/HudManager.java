package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.event.ResizeHudEvent;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.widegt.TComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashSet;

/**
 * @author USS_Shenzhou
 * TODO move to T88
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HudManager {
    private static final LinkedHashSet<TComponent> CHILDREN = new LinkedHashSet<>();

    public static void add(TComponent tComponent) {
        CHILDREN.add(tComponent);
    }

    public static void remove(TComponent tComponent) {
        CHILDREN.remove(tComponent);
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGameOverlayEvent event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        PoseStack poseStack = event.getMatrixStack();
        int mouseX = (int) MouseHelper.getMouseX();
        int mouseY = (int) MouseHelper.getMouseY();
        float partialTick = event.getPartialTicks();
        CHILDREN.forEach((tComponent) -> {
            if (tComponent.isVisibleT()) {
                tComponent.render(poseStack, mouseX, mouseY, partialTick);
            }
        });
    }

    @SubscribeEvent
    public static void renderHudAfterScreen(ScreenEvent.DrawScreenEvent.Post event) {
        PoseStack poseStack = event.getPoseStack();
        int mouseX = (int) MouseHelper.getMouseX();
        int mouseY = (int) MouseHelper.getMouseY();
        float partialTick = event.getPartialTicks();
        CHILDREN.forEach((tComponent) -> {
            if (tComponent.isVisibleT()) {
                tComponent.renderTop(poseStack, mouseX, mouseY, partialTick);
            }
        });
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CHILDREN.forEach((tComponent) -> {
                if (tComponent.isVisibleT()) {
                    tComponent.tickT();
                }
            });
        }
    }

    @SubscribeEvent
    public static void onResize(ResizeHudEvent event) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        CHILDREN.forEach(tComponent -> tComponent.resizeAsHud(screenWidth, screenHeight));
    }
}
