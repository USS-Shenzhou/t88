package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.event.ResizeHudEvent;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.widegt.TComponent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(value = Dist.CLIENT)
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
            o.ifPresent(HudManager::remove);
            add(component);
        });
    }

    public static void removeInstanceOf(Class<? extends TComponent> clazz) {
        needRemove.addAll(CHILDREN.stream().filter(clazz::isInstance).toList());
    }

    public static void renderHud(GuiGraphics graphics, int mouseX, int mouseY, DeltaTracker partialTick) {
        if (Minecraft.getInstance().options.hideGui) {
            return;
        }
        CHILDREN.forEach((tComponent) -> {
            if (tComponent.isVisibleT()) {
                tComponent.render(graphics, mouseX, mouseY, partialTick.getRealtimeDeltaTicks());
            }
        });
        CHILDREN.forEach((tComponent) -> {
            if (tComponent.isVisibleT()) {
                tComponent.renderTop(graphics, mouseX, mouseY, partialTick.getRealtimeDeltaTicks());
            }
        });
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
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
