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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
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
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);
        this.add(count);
        count.setFontSize(itemSize / DEFAULT_SIZE * TLabel.STD_FONT_SIZE);
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
        renderItem(graphics, pMouseX, pMouseY, pPartialTick);
        float scale = itemSize / DEFAULT_SIZE;
        if (count.isVisibleT()) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 1 * scale);
            count.render(graphics, pMouseX, pMouseY, pPartialTick);
            graphics.pose().popPose();
        }
        if (showTooltip && isInRange(pMouseX, pMouseY) && this.getTopParentScreen() != null) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 500 * scale);
            RenderSystem.disableScissor();
            graphics.renderTooltip(Minecraft.getInstance().font, item.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.NORMAL), item.getTooltipImage(), item, pMouseX, pMouseY);
            var rectangle = graphics.scissorStack.stack.peek();
            if (rectangle != null) {
                Window window = Minecraft.getInstance().getWindow();
                int i = window.getHeight();
                double d0 = window.getGuiScale();
                double d1 = (double) rectangle.left() * d0;
                double d2 = (double) i - (double) rectangle.bottom() * d0;
                double d3 = (double) rectangle.width() * d0;
                double d4 = (double) rectangle.height() * d0;
                RenderSystem.enableScissor((int) d1, (int) d2, Math.max(0, (int) d3), Math.max(0, (int) d4));
            }
            graphics.pose().popPose();
        }
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderItem(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (item.isEmpty()) {
            return;
        }
        var mc = Minecraft.getInstance();
        BakedModel bakedmodel = mc.getItemRenderer().getModel(item, mc.level, host, 42);
        float scale = itemSize / DEFAULT_SIZE;
        graphics.pose().pushPose();
        graphics.pose().translate(x + itemSize / 2, y + itemSize / 2, 0.01 * scale);
        try {
            graphics.pose().mulPoseMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
            graphics.pose().scale(16.0F, 16.0F, 16.0F);
            boolean flag = !bakedmodel.usesBlockLight();
            if (flag) {
                Lighting.setupForFlatItems();
            }
            graphics.pose().scale(scale, scale, scale);
            mc.getItemRenderer()
                    .render(item, ItemDisplayContext.GUI, false, graphics.pose(), graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
            graphics.flush();
            if (flag) {
                Lighting.setupFor3DItems();
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type / Registry Name", () -> String.valueOf(item.getItem()));
            crashreportcategory.setDetail("Item Damage", () -> String.valueOf(item.getDamageValue()));
            crashreportcategory.setDetail("Item NBT", () -> String.valueOf(item.getTag()));
            crashreportcategory.setDetail("Item Foil", () -> String.valueOf(item.hasFoil()));
            throw new ReportedException(crashreport);
        }

        graphics.pose().popPose();
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
