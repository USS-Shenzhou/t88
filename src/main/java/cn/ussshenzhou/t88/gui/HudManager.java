package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.event.GameRendererRenderedEvent;
import cn.ussshenzhou.t88.gui.event.ResizeHudEvent;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.widegt.TComponent;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HudManager {
    private static final LinkedHashSet<TComponent> CHILDREN = new LinkedHashSet<>();
    private static final LinkedList<TComponent> needAdd = new LinkedList<>();
    private static final LinkedList<TComponent> needRemove = new LinkedList<>();

    public static void add(TComponent... tComponents) {
        needAdd.addAll(Arrays.stream(tComponents).toList());
    }

    public static void remove(TComponent... tComponents) {
        needRemove.addAll(Arrays.stream(tComponents).toList());
    }

    public static LinkedHashSet<TComponent> getChildren() {
        return CHILDREN;
    }

    public static void addIfSameClassNotExist(TComponent... tComponents) {
        Arrays.stream(tComponents).forEach(component -> {
            var o = CHILDREN.stream()
                    .filter(t -> t.getClass() == component.getClass())
                    .findFirst();
            if (o.isEmpty()) {
                add(component);
            }
        });
    }

    public static void addOrReplaceIfSameClassExist(TComponent... tComponents) {
        Arrays.stream(tComponents).forEach(component -> {
            var o = CHILDREN.stream()
                    .filter(t -> t.getClass() == component.getClass())
                    .findFirst();
            if (o.isPresent()) {
                remove(o.get());
                add(component);
            }
        });
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event) {
        var graphics = event.getGuiGraphics();
        int mouseX = (int) MouseHelper.getMouseX();
        int mouseY = (int) MouseHelper.getMouseY();
        float partialTick = event.getPartialTick();
        CHILDREN.forEach((tComponent) -> {
            if (tComponent.isVisibleT()) {
                graphics.pose().translate(0, 0, 0.1);
                tComponent.render(graphics, mouseX, mouseY, partialTick);
            }
        });
    }

    @SubscribeEvent
    public static void renderHudAfterScreen(GameRendererRenderedEvent event) {
        var graphics = event.getGraphics();
        int mouseX = (int) MouseHelper.getMouseX();
        int mouseY = (int) MouseHelper.getMouseY();
        float partialTick = event.getPartialTick();
        graphics.pose().translate(0, 0, 1000);
        CHILDREN.forEach((tComponent) -> {
            if (tComponent.isVisibleT()) {
                graphics.pose().translate(0, 0, 0.1);
                tComponent.renderTop(graphics, mouseX, mouseY, partialTick);
            }
        });
        graphics.pose().translate(0, 0, -1000);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            needAdd.forEach(tComponent -> {
                CHILDREN.add(tComponent);
                tComponent.resizeAsHud(w, h);
            });
            needAdd.clear();
            needRemove.forEach(CHILDREN::remove);
            needRemove.clear();
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

    @SubscribeEvent
    public static void onPlayerOut(ClientPlayerNetworkEvent.LoggingOut event) {
        synchronized (CHILDREN) {
            List<TComponent> l = CHILDREN.stream().filter(TComponent::isShowHudEvenLoggedOut).toList();
            CHILDREN.clear();
            CHILDREN.addAll(l);
        }
    }
}
