package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Matrix4f;
import org.joml.Vector2i;

/**
 * @author USS_Shenzhou
 */
public class TItem extends TPanel {
    public static int DEFAULT_SIZE = 16;
    private LivingEntity host;
    private ItemStack item;
    private float itemSize;
    private final TLabel count;
    private boolean showTooltip = false;

    public TItem(ItemStack item, LivingEntity host, float itemSize) {
        super();
        this.host = host;
        this.item = item;
        this.itemSize = itemSize;
        count = new TLabel(Component.literal(String.valueOf(item.getCount())))
                .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setFontSize(itemSize / DEFAULT_SIZE * TLabel.STD_FONT_SIZE)
                .setAutoScroll(false);
        this.add(count);
        if (item.getCount() <= 1) {
            count.setVisibleT(false);
        }
        count.setAutoScroll(false);
    }

    public TItem(ItemStack item) {
        this(item, null, DEFAULT_SIZE);
    }

    public TItem(Item item) {
        this(new ItemStack(item));
    }

    @Override
    public void layout() {
        count.setBounds(0, (int) (height - itemSize / DEFAULT_SIZE * TLabel.STD_FONT_SIZE), width, (int) (itemSize / DEFAULT_SIZE * TLabel.STD_FONT_SIZE));
        super.layout();
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.pose().pushMatrix();
        renderItem(graphics, pMouseX, pMouseY, pPartialTick);
        float scale = itemSize / DEFAULT_SIZE;
        if (count.isVisibleT()) {
            count.render(graphics, pMouseX, pMouseY, pPartialTick);
        }
        if (showTooltip && isInRange(pMouseX, pMouseY) && this.getTopParentScreen() != null) {
            graphics.disableScissor();
            graphics.setTooltipForNextFrame(
                    Minecraft.getInstance().font,
                    Screen.getTooltipFromItem(Minecraft.getInstance(), item),
                    item.getTooltipImage(),
                    item,
                    x,
                    y,
                    item.get(DataComponents.TOOLTIP_STYLE)
            );
            var rectangle = graphics.scissorStack.stack.peek();
            if (rectangle != null) {
                Window window = Minecraft.getInstance().getWindow();
                int i = window.getHeight();
                double d0 = window.getGuiScale();
                double d1 = (double) rectangle.left() * d0;
                double d2 = (double) i - (double) rectangle.bottom() * d0;
                double d3 = (double) rectangle.width() * d0;
                double d4 = (double) rectangle.height() * d0;
                graphics.enableScissor((int) d1, (int) d2, Math.max(0, (int) d3), Math.max(0, (int) d4));
            }
        }
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.pose().popMatrix();
    }

    protected void renderItem(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (item.isEmpty()) {
            return;
        }
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(itemSize / DEFAULT_SIZE);
        graphics.renderItem(item, 0, 0);
        graphics.pose().popMatrix();
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i((int) (DEFAULT_SIZE * itemSize / 16), (int) (DEFAULT_SIZE * itemSize / 16));
    }

    public LivingEntity getHost() {
        return host;
    }

    public void setHost(LivingEntity host) {
        this.host = host;
    }

    public ItemStack getItem() {
        return item;
    }

    public TItem setItem(ItemStack item) {
        this.item = item;
        this.count.setText(Component.literal(String.valueOf(item.getCount())));
        if (item.getCount() <= 1) {
            count.setVisibleT(false);
        }
        return this;
    }

    public float getItemSize() {
        return itemSize;
    }

    public TItem setItemSize(float size) {
        this.itemSize = size;
        count.setFontSize(itemSize / DEFAULT_SIZE * TLabel.STD_FONT_SIZE);
        layout();
        return this;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public TItem setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
        return this;
    }
}
