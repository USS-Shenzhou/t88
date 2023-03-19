package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.util.ImageFit;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * @author USS_Shenzhou
 */
public class TImage extends TPanel {
    protected ResourceLocation imageLocation;
    protected ImageFit imageFit = ImageFit.FILL;
    protected int imageWidth;
    protected int imageHeight;
    protected float scale = 1;
    protected float alpha = 1;

    public TImage(ResourceLocation imageLocation) {
        this.imageLocation = imageLocation;
        loadImageWH();
    }

    public TImage(String imageLocation) {
        this(new ResourceLocation(imageLocation));
    }

    public TImage(String namespace, String imageLocation) {
        this(new ResourceLocation(namespace, imageLocation));
    }

    public ImageFit getImageFit() {
        return imageFit;
    }

    public void setImageFit(ImageFit imageFit) {
        this.imageFit = imageFit;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public ResourceLocation getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(ResourceLocation imageLocation) {
        this.imageLocation = imageLocation;
        loadImageWH();
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    protected void loadImageWH() {
        try {
            NativeImage n = NativeImage.read(Minecraft.getInstance().getResourceManager().getResource(imageLocation).get().open());
            imageWidth = n.getWidth();
            imageHeight = n.getHeight();
        } catch (IOException | NoSuchElementException e) {
            LogUtils.getLogger().error("Failed to get width and height of {}.", imageLocation.getPath());
            LogUtils.getLogger().error(e.getMessage());
            this.imageLocation = null;
        }
    }

    @SuppressWarnings("AlibabaSwitchStatement")
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (imageLocation != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, imageLocation);
            RenderSystem.setShaderColor(1, 1, 1, alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            switch (imageFit) {
                case FILL -> {
                    float panelWHRatio = width / (float) height;
                    float imageWHRatio = imageWidth / (float) imageHeight;
                    if (panelWHRatio > imageWHRatio) {
                        blit(pPoseStack, this.x, this.y, width, height,
                                0, (imageHeight - imageWidth / panelWHRatio) / 2,
                                imageWidth, (int) (imageWidth / panelWHRatio),
                                (int) (imageWidth * scale), (int) (imageHeight * scale));
                    } else {
                        blit(pPoseStack, this.x, this.y, width, height,
                                (imageWidth - imageHeight * panelWHRatio) / 2, 0,
                                (int) (imageHeight * panelWHRatio), imageHeight,
                                (int) (imageWidth * scale), (int) (imageHeight * scale));
                    }
                }
                case FIT -> {
                    float panelWHRatio = width / (float) height;
                    float imageWHRatio = imageWidth / (float) imageHeight;
                    if (panelWHRatio > imageWHRatio) {
                        blit(pPoseStack, (int) (this.x + (width - height * imageWHRatio) / 2), this.y,
                                (int) (height * imageWHRatio), height,
                                0, 0,
                                imageWidth, imageHeight,
                                (int) (imageWidth * scale), (int) (imageHeight * scale));
                    } else {
                        blit(pPoseStack, this.x, (int) (this.y + (height - width / imageWHRatio) / 2),
                                width, (int) (width / imageWHRatio),
                                0, 0,
                                imageWidth, imageHeight,
                                (int) (imageWidth * scale), (int) (imageHeight * scale));
                    }
                }
                case STRETCH ->
                        blit(pPoseStack, this.x, this.y, width, height, 0, 0, imageWidth, imageHeight, (int) (imageWidth * scale), (int) (imageHeight * scale));
                case TILE ->
                        blit(pPoseStack, this.x, this.y, width, height, 0, 0, width, height, (int) (imageWidth * scale), (int) (imageHeight * scale));
            }
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }
}
