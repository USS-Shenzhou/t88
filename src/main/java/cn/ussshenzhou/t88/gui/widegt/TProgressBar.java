package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.util.ColorManager;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TProgressBar extends TLabel {
    protected String prefix = "";
    protected double maxValue = 1;
    protected double value = 0;
    protected TextMode textMode = TextMode.NONE;
    protected int progressBarColor0 = ColorManager.get().themeColor();
    protected int progressBarColor1 = ColorManager.get().themeColor();

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
        this.progressBarColor0 = progressBarColor;
        this.progressBarColor1 = progressBarColor;
    }

    public void setProgressBarColorGradient(int color0, int color1) {
        this.progressBarColor0 = color0;
        this.progressBarColor1 = color1;
    }

    @Override
    public void tickT() {
        this.setText(Component.literal(this.textMode.generator.get(this.prefix, this.maxValue, this.value)));
        super.tickT();
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.fill(x,
                y,
                x + width,
                y + height,
                background);
        int dx = (int) (width * value / maxValue);
        var scroll = getParentScroll();
        graphics.scissorStack.push(new ScreenRectangle(
                (int) (scroll.x + x),
                (int) (y - scroll.y),
                dx,
                height));
        fillGradientHorizontal(graphics,
                x,
                y,
                x + width,
                y + height,
                progressBarColor0, progressBarColor1);
        graphics.scissorStack.pop();
    }

    public static class TextMode {
        public static final TextMode NONE = new TextMode((pre, max, v) -> "");
        public static final TextMode PREFIX_ONLY = new TextMode((pre, max, v) -> pre);
        public static final TextMode VALUE_FLOAT = new TextMode((pre, max, v) -> pre + String.format("%.2f", v));
        public static final TextMode VALUE_FLOAT_SLASH_MAX = new TextMode((pre, max, v) -> pre + String.format("%.2f/%.2f", v, max));
        public static final TextMode VALUE_INT = new TextMode((pre, max, v) -> pre + String.format("%d", (int) v));
        public static final TextMode VALUE_INT_SLASH_MAX = new TextMode((pre, max, v) -> pre + String.format("%d/%d", (int) v, (int) max));
        public static final TextMode PERCENTAGE = new TextMode((pre, max, v) -> {
            if (Math.abs(v - 1) <= 0.000001) {
                return pre + String.format("%.1f", 100 * v) + '%';
            } else {
                return pre + String.format("%.1f", 100 * v / max) + '%';
            }
        });

        private final TextGenerator generator;

        public TextMode(TextGenerator generator) {
            this.generator = generator;
        }
    }

    @FunctionalInterface
    public interface TextGenerator {

        String get(String prefix, double maxValue, double value);
    }
}
