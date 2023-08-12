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

    public void setText(Component text) {
        this.text = text;
        parseTextLines();
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float size) {
        this.fontSize = size;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    @Override
    public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
        renderText(guigraphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderText(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        guigraphics.pose().pushPose();
        float y0 = Mth.ceil((y + (height - (fontSize + lineSpacing) * textLines.size()) / 2));
        for (Component line : textLines) {
            if (autoScroll) {
                drawStringSingleLine(guigraphics, font, line, fontSize, horizontalAlignment, x, (x + width), (int) y0, (int) (y0 + fontSize + lineSpacing), foreground);
            } else {
                int x0 = getAlignedX(line);
                float scaleFactor = fontSize / STD_FONT_SIZE;
                guigraphics.pose().scale(scaleFactor, scaleFactor, 1);
                guigraphics.drawString(font, line, (int) (x0 / scaleFactor), (int) (y0 / scaleFactor), foreground);
            }
            y0 += (fontSize + lineSpacing);
        }
        guigraphics.pose().popPose();
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
                    Math.max(font.width(line) * fontSize / 7, Math.max(width, maxLineWidth))
            );
        }
        return new Vector2i(maxLineWidth, (int) ((fontSize + lineSpacing) * textLines.size()) + 1);
    }

}
