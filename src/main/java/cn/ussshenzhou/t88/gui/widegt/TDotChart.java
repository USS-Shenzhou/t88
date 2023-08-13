package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.util.ColorManager;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

/**
 * @author USS_Shenzhou
 */
public class TDotChart<T extends Number & Comparable<T>> extends TPanel {
    protected T[] numbers;
    double yMin, yMax, yUpper, yLower;
    int xMax;
    //Chart area
    int minX, minY, maxX, maxY;
    int c0 = ColorManager.get().defaultForeground();
    Font font = Minecraft.getInstance().font;
    int labelGap = 1;
    int spikeHeight = 2;

    protected TLabel xAxisLabel, yAxisLabel;

    public TDotChart(T[] numbers, Component xAxisLabel, Component yAxisLabel) {
        super();
        this.numbers = numbers;
        this.xAxisLabel = new TLabel(xAxisLabel);
        this.add(this.xAxisLabel);
        this.yAxisLabel = new TLabel(yAxisLabel);
        this.yAxisLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        this.add(this.yAxisLabel);
        initChart();
    }

    private void initChart() {
        Arrays.stream(numbers).min(Comparable::compareTo).ifPresent(t -> yMin = t.doubleValue());
        Arrays.stream(numbers).max(Comparable::compareTo).ifPresent(t -> yMax = t.doubleValue());
        yUpper = yMax + (yMax - yMin) * 0.1;
        yLower = yMin - (yMax - yMin) * 0.1;
        xMax = (int) (numbers.length * 1.1);
    }

    @Override
    public void layout() {
        minX = (int) (getXT() + width * 0.1);
        minY = (int) (getYT() + height * 0.1);
        maxX = (int) (minX + width * 0.8);
        maxY = (int) (minY + height * 0.8);
        var y0 = yAxisLabel.getPreferredSize().y;
        this.yAxisLabel.setAbsBounds(0, minY - y0, minX - 1 - labelGap, y0);
        this.xAxisLabel.setAbsBounds(maxX, maxY + 1 + labelGap, (int) (width * 0.1), xAxisLabel.getPreferredSize().y);
        super.layout();
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderAxis(graphics);
        renderDots(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderAxis(GuiGraphics graphics) {
        //Y axis
        graphics.fill(minX - 1, minY, minX, maxY + 1, c0);
        //X axis
        graphics.fill(minX, maxY, maxX, maxY + 1, c0);
        //x=0
        graphics.drawString(font, Component.literal(String.valueOf(0)), minX - 1, maxY + 1 + labelGap, c0);
        //x=max
        var text = Component.literal(String.valueOf(xMax));
        var x0 = (int) (minX + (maxX - minX) * 0.9);
        graphics.fill(x0, maxY - spikeHeight, x0 + 1, maxY, c0);
        graphics.drawString(font, text, x0 - font.width(text) / 2, xAxisLabel.getYT(), c0);
        //y=lowerBounds
        int fontSizeOffsetY = 4;
        var x1 = minX - 1 - labelGap;
        drawStringSingleLine(graphics, font, Component.literal(String.format("%.2f", yLower)), HorizontalAlignment.RIGHT, 0, x1, maxY - fontSizeOffsetY, c0);
        //y=yMin
        var y0 = (int) (minY + (maxY - minY) * 0.9);
        graphics.fill(minX, y0, minX + spikeHeight, y0 + 1, c0);
        drawStringSingleLine(graphics, font, Component.literal(String.valueOf(yMin)), HorizontalAlignment.RIGHT, 0, x1, y0 - fontSizeOffsetY, c0);
        //y=yMax
        var y1 = (int) (minY + (maxY - minY) * 0.1);
        graphics.fill(minX, y1, minX + spikeHeight, y1 + 1, c0);
        drawStringSingleLine(graphics, font, Component.literal(String.valueOf(yMax)), HorizontalAlignment.RIGHT, 0, x1, y1 - fontSizeOffsetY, c0);
    }

    protected void renderDots(GuiGraphics graphics) {
        int i = 0;
        float stepX = (maxX - minX) * 0.9f / numbers.length;
        float bottomY = (maxY - minY) * 0.1f;
        float spaceY = (maxY - minY) * 0.8f;
        double rangeY = yMax - yMin;
        int c1 = ColorManager.get().themeColor();
        for (T dot : numbers) {
            double v = dot.doubleValue();
            int x0 = (int) (minX + stepX * i);
            int y0 = (int) (maxY - bottomY - (v - yMin) / rangeY * spaceY);
            graphics.fill(x0, y0, x0 + 1, y0 + 1, c1);

            i++;
        }
    }

}
