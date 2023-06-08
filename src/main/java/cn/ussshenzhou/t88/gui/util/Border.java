package cn.ussshenzhou.t88.gui.util;

import net.minecraft.client.gui.GuiGraphics;

/**
 * @author USS_Shenzhou
 */
public class Border {
    private int color;
    private int thickness;

    public Border(int color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public static void renderBorder(GuiGraphics guiGraphics, int color, int thickness, int x, int y, int width, int height) {
        guiGraphics.fill(x - thickness, y - thickness, x + width + thickness, y, color);
        guiGraphics.fill(x - thickness, y + height, x + width + thickness, y + height + thickness, color);
        guiGraphics.fill(x - thickness, y, x, y + height, color);
        guiGraphics.fill(x + width, y, x + width + thickness, y + height, color);
    }
}
