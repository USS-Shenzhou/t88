package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.util.ColorManager;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TProgressBar extends TLabel {
    protected String prefix = "";
    protected double maxValue = 1;
    protected double value = 0;
    protected TextMode textMode = TextMode.NONE;
    protected int progressBarColor = ColorManager.get().themeColor();

    public TProgressBar() {
        this("", 1);
    }

    public TProgressBar(double maxValue) {
        this("", maxValue);
    }

    public TProgressBar(String prefix, double maxValue) {
        super();
        this.prefix = prefix;
        this.maxValue = maxValue;
        this.background = ColorManager.get().defaultBackground();
        this.setHorizontalAlignment(HorizontalAlignment.CENTER);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void increaseValue(double value) {
        this.value += value;
    }

    public void setTextMode(TextMode textMode) {
        this.textMode = textMode;
    }

    public void setProgressBarColor(int progressBarColor) {
        this.progressBarColor = progressBarColor;
    }

    @Override
    public void tickT() {
        this.setText(Component.literal(this.textMode.generator.get(this.prefix, this.maxValue, this.value)));
        super.tickT();
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int dx = (int) (width * value / maxValue);
        graphics.fill(x, y, x + dx, y + height, progressBarColor);
        graphics.fill(x + dx, y, x + width, y + height, background);
    }

    public enum TextMode {
        NONE((pre, max, v) -> ""),
        PREFIX_ONLY((pre, max, v) -> pre),
        VALUE_FLOAT((pre, max, v) -> pre + String.format("%.2f", v)),
        VALUE_FLOAT_SLASH_MAX((pre, max, v) -> pre + String.format("%.2f/%.2f", v, max)),
        VALUE_INT((pre, max, v) -> pre + String.format("%d", (int) v)),
        VALUE_INT_SLASH_MAX((pre, max, v) -> pre + String.format("%d/%d", (int) v, (int) max)),
        PERCENTAGE((pre, max, v) -> {
            if (Math.abs(v - 1) <= 0.000001) {
                return pre + String.format("%.1f", 100 * v) + '%';
            } else {
                return pre + String.format("%.1f", 100 * v / max) + '%';
            }
        });

        private final TextGenerator generator;

        TextMode(TextGenerator generator) {
            this.generator = generator;
        }
    }

    @FunctionalInterface
    protected interface TextGenerator {

        String get(String prefix, double maxValue, double value);
    }
}
