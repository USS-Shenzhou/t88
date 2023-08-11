package cn.ussshenzhou.t88.gui.widegt;

import net.minecraft.client.gui.GuiGraphics;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 */
public class TDotChart<T extends Number & Comparable<T>> extends TPanel {
    protected T[] numbers;
    double min;
    double max;
    double upperBound;
    double lowerBound;

    public TDotChart(T[] numbers) {
        super();
        this.numbers = numbers;
        initChart();
    }

    private void initChart() {
        Arrays.stream(numbers).min(Comparable::compareTo).ifPresent(t -> min = t.doubleValue());
        Arrays.stream(numbers).max(Comparable::compareTo).ifPresent(t -> max = t.doubleValue());
        upperBound = max + (max - min) * 0.1;
        lowerBound = min - (max - min) * 0.1;
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
