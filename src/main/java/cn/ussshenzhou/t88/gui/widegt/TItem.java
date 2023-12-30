package cn.ussshenzhou.t88.gui.widegt;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.ForgeRegistries;
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

    public TItem(ItemStack item, LivingEntity host, float itemSize) {
        super();
        this.host = host;
        this.item = item;
        this.itemSize = itemSize;
    }

    public TItem(ItemStack item) {
        this(item, null, DEFAULT_SIZE);
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        renderItem(graphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderItem(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (item.isEmpty()) {
            return;
        }
        var mc = Minecraft.getInstance();
        BakedModel bakedmodel = mc.getItemRenderer().getModel(item, mc.level, host, 42);
        graphics.pose().pushPose();
        graphics.pose().translate(x + itemSize / 2, y + itemSize / 2, 150);
        try {
            graphics.pose().mulPoseMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
            graphics.pose().scale(16.0F, 16.0F, 16.0F);

            float scale = itemSize / DEFAULT_SIZE;
            graphics.pose().scale(scale, scale, scale);

            boolean flag = !bakedmodel.usesBlockLight();
            if (flag) {
                Lighting.setupForFlatItems();
            }
            mc.getItemRenderer().render(item, ItemDisplayContext.GUI, false, graphics.pose(), graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
            graphics.flush();
            if (flag) {
                Lighting.setupFor3DItems();
            }
        } catch (Throwable var12) {
            CrashReport crashreport = CrashReport.forThrowable(var12, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type", () -> String.valueOf(item.getItem()));
            crashreportcategory.setDetail("Registry Name", () -> String.valueOf(ForgeRegistries.ITEMS.getKey(item.getItem())));
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

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public float getItemSize() {
        return itemSize;
    }

    public void getItemSize(float size) {
        this.itemSize = size;
    }
}
