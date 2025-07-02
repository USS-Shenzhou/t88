package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import org.joml.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("UnusedReturnValue")
public class TLabel extends TPanel {
    protected Component text;
    protected float fontSize = 7;
    public static final int STD_FONT_SIZE = 7;
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    protected ArrayList<Component> textLines = new ArrayList<>();
    protected int lineSpacing = 2;
    protected boolean autoScroll = true;

    Font font = Minecraft.getInstance().font;

    public TLabel() {
        this.text = Component.empty();
        parseTextLines();
    }

    public TLabel(Component s) {
        this.text = s;
        parseTextLines();
    }

    public TLabel(Component s, int foreground) {
        this(s);
        this.setForeground(foreground);
    }

    protected void parseTextLines() {
        String[] lines = text.getString().split("\n");
        textLines.clear();
        Stream.of(lines).forEach(line -> textLines.add(Component.literal(line)));
    }

    public Component getText() {
        return text;
    }

    public TLabel setText(Component text) {
        this.text = text;
        parseTextLines();
        return this;
    }

    public float getFontSize() {
        return fontSize;
    }

    public TLabel setFontSize(float size) {
        this.fontSize = size;
        return this;
    }

    public TLabel setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public TLabel setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
        return this;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public TLabel setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
        return this;
    }

    @Override
    public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
        renderText(guigraphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderText(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        guigraphics.pose().pushMatrix();
        float y0 = Mth.ceil((y + (height - (fontSize + lineSpacing) * textLines.size()) / 2));
        for (Component line : textLines) {
            if (autoScroll) {
                drawStringSingleLine(guigraphics, font, line, fontSize, horizontalAlignment, x, (x + width), (int) y0, (int) (y0 + fontSize + lineSpacing), foreground);
            } else {
                guigraphics.pose().pushMatrix();
                int x0 = getAlignedX(line);
                float scaleFactor = fontSize / (float) STD_FONT_SIZE;
                guigraphics.pose().scale(scaleFactor, scaleFactor);
                guigraphics.drawString(font, line, (int) (x0 / scaleFactor), (int) (y0 / scaleFactor), foreground);
                guigraphics.pose().popMatrix();
            }
            y0 += (fontSize + lineSpacing);
        }
        guigraphics.pose().popMatrix();
    }

    protected int getAlignedX(Component line) {
        int textWidth = Mth.ceil(font.width(line) * fontSize / 7);
        return switch (horizontalAlignment) {
            case CENTER -> x + (width - textWidth) / 2;
            case RIGHT -> x + width - textWidth;
            default -> x;
        };
    }

    @Override
    public Vector2i getPreferredSize() {
        int maxLineWidth = 0;
        for (Component line : textLines) {
            maxLineWidth = Mth.ceil(
                    Math.max(font.width(line) * fontSize / 7,  maxLineWidth)
            );
        }
        return new Vector2i(maxLineWidth, (int) ((fontSize + lineSpacing) * textLines.size()) + 1);
    }

}
