package cn.ussshenzhou.t88.gui.advanced;

import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author USS_Shenzhou
 */
public class THoverSensitiveImageButton extends TPanel {
    protected TImage backgroundImage;
    protected TImage backgroundImageHovered;
    protected TButton button;
    protected TLabel text;

    protected int padding = 0;
    private boolean inTransition = false;
    private boolean transited = false;
    private int transitionTimeMinus1 = 2;
    private float transitionTick = 0;

    public THoverSensitiveImageButton(Component text1, Button.OnPress onPress, @Nullable ResourceLocation backgroundImageLocation, @Nullable ResourceLocation backgroundImageLocationHovered) {
        super();
        if (backgroundImageLocation == null) {
            backgroundImageLocation = PLACEHOLDER_IMAGE;
        }
        if (backgroundImageLocationHovered == null) {
            backgroundImageLocationHovered = PLACEHOLDER_IMAGE;
        }
        this.text = new TLabel(text1);
        this.text.setAutoScroll(false)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        this.button = new TButton(Component.literal(""), onPress) {
            @Override
            public void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                return;
            }
        };
        this.backgroundImageHovered = new TImage(backgroundImageLocationHovered);
        this.backgroundImageHovered.setVisibleT(false);
        this.add(this.backgroundImageHovered);
        this.backgroundImage = new TImage(backgroundImageLocation);
        this.add(this.backgroundImage);
        this.add(this.button);
        this.add(this.text);
    }

    @Override
    public void layout() {
        backgroundImage.setBounds(padding, padding, this.width - padding * 2, this.height - padding * 2);
        backgroundImageHovered.setBounds(0, 0, this.getSize());
        LayoutHelper.BSameAsA(text, backgroundImage);
        button.setBounds(padding / 2, padding / 2, this.width - padding, this.height - padding);

        super.layout();
    }

    @Override
    public void tickT() {
        if (button.isHoveredOrFocused()) {
            backgroundImage.setVisibleT(false);
            backgroundImageHovered.setVisibleT(true);
        } else {
            backgroundImage.setVisibleT(true);
            backgroundImageHovered.setVisibleT(false);
        }

        if (backgroundImageHovered.isVisibleT() && !transited) {
            if (!inTransition) {
                //begin transit
                inTransition = true;
            }
            if (transitionTick >= transitionTimeMinus1) {
                //complete transit
                inTransition = false;
                transited = true;
                transitionTick = 0;
            }
        }
        if (!backgroundImageHovered.isVisibleT()) {
            inTransition = false;
            transited = false;
            transitionTick = 0;
        }

        super.tickT();
    }

    @Override
    protected void renderChildren(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (TWidget tWidget : children) {
            if (tWidget.isVisibleT()) {
                graphics.pose().translate(0, 0);
                if (tWidget == text && backgroundImageHovered.isVisibleT()) {
                    renderText(graphics, pMouseX, pMouseY, pPartialTick);
                    continue;
                }
                if (tWidget == backgroundImageHovered) {
                    renderBgImageHovered(graphics, pMouseX, pMouseY, pPartialTick);
                    continue;
                }
                tWidget.render(graphics, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    /**
     * backgroundImage will be stretched to the size of backgroundImage, then magnified to original.
     */
    protected void renderBgImageHovered(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (padding != 0 && inTransition && transitionTick < transitionTimeMinus1) {
            float minScaleX = (float) backgroundImage.getWidth() / backgroundImageHovered.getWidth();
            float minScaleY = (float) backgroundImage.getHeight() / backgroundImageHovered.getHeight();
            float scaleX = minScaleX + transitionTick / transitionTimeMinus1 * (1 - minScaleX);
            float scaleY = minScaleY + transitionTick / transitionTimeMinus1 * (1 - minScaleY);
            float compensationRelativeX = (1 - scaleX) / (1 - minScaleX) * padding;
            float compensationRelativeY = (1 - scaleY) / (1 - minScaleY) * padding;
            graphics.pose().pushMatrix();
            //scale compensation = absolute + relative
            graphics.pose().translate(
                    (1 - scaleX) * backgroundImageHovered.getXT() + compensationRelativeX,
                    (1 - scaleY) * backgroundImageHovered.getYT() + compensationRelativeY);
            graphics.pose().scale(scaleX, scaleY);
            backgroundImageHovered.render(graphics, pMouseX, pMouseY, pPartialTick);
            graphics.pose().popMatrix();
            transitionTick += pPartialTick;
        } else {
            backgroundImageHovered.render(graphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    /**
     * Text will be magnified when focused. To keep text's original shape, scaling will not calculate separately.
     */
    protected void renderText(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        float maxScaleX = (float) backgroundImageHovered.getWidth() / backgroundImage.getWidth();
        float maxScaleY = (float) backgroundImageHovered.getHeight() / backgroundImage.getHeight();
        float maxScale = Math.min(maxScaleX, maxScaleY);
        if (padding != 0 && inTransition && transitionTick < transitionTimeMinus1) {
            float scale = 1 + transitionTick / transitionTimeMinus1 * (maxScale - 1);
            renderTextInternal(graphics, pMouseX, pMouseY, pPartialTick, scale, maxScale);
        } else {
            renderTextInternal(graphics, pMouseX, pMouseY, pPartialTick, maxScale, maxScale);
        }
    }

    public void renderTextInternal(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, float scale, float maxScale) {
        float compensationRelative = padding == 0 ? 0 : (1 - scale) / (maxScale - 1) * padding;
        graphics.pose().pushMatrix();
        graphics.pose().translate(
                (1 - scale) * text.getXT() + compensationRelative,
                (1 - scale) * text.getYT() + compensationRelative);
        graphics.pose().scale(scale, scale);
        text.render(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.pose().popMatrix();
    }

    public TImage getBackgroundImage() {
        return backgroundImage;
    }

    public TImage getBackgroundImageHovered() {
        return backgroundImageHovered;
    }

    public TButton getButton() {
        return button;
    }

    public TLabel getText() {
        return text;
    }

    public int getPadding() {
        return padding;
    }

    public THoverSensitiveImageButton setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    public int getTransitionTimeMinus1() {
        return transitionTimeMinus1;
    }

    public THoverSensitiveImageButton setTransitionTimeMinus1(int transitionTimeMinus1) {
        this.transitionTimeMinus1 = transitionTimeMinus1;
        return this;
    }
}
