package cn.ussshenzhou.t88.gui.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * @author USS_Shenzhou
 */
public class Border {
    private int color;
    private int thickness;

    public Border(int argb, int thickness) {
        this.color = argb;
        this.thickness = thickness;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int argb) {
        this.color = argb;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public static void renderBorder(GuiGraphicsExtractor guiGraphics, int argb, int thickness, int x, int y, int width, int height) {
        guiGraphics.fill(x - thickness, y - thickness, x + width + thickness, y, argb);
        guiGraphics.fill(x - thickness, y + height, x + width + thickness, y + height + thickness, argb);
        guiGraphics.fill(x - thickness, y, x, y + height, argb);
        guiGraphics.fill(x + width, y, x + width + thickness, y + height, argb);
    }
}
