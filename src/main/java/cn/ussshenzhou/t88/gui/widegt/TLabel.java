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
    protected int maxLineWidth = 0;

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
        for (Component line : textLines) {
            //FIXME wrong calculate
            maxLineWidth = Mth.ceil(Math.max(font.width(line) * fontSize / 7, width));
        }
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

    @Override
    public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
        renderText(guigraphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderText(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        guigraphics.pose().pushPose();
        float scaleFactor = fontSize / 7f;
        guigraphics.pose().scale(scaleFactor, scaleFactor, 1);
        int y0 = Mth.ceil((y + (height - (fontSize + lineSpacing) * textLines.size()) / 2) / scaleFactor);
        for (Component line : textLines) {
            int x0 = (int) (getAlignedX(line) / scaleFactor);
            guigraphics.drawString(font, line, x0, y0, foreground);
            y0 += (fontSize + lineSpacing) / scaleFactor;
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
        return new Vector2i(maxLineWidth, (int) ((fontSize + lineSpacing) * textLines.size()) + 1);
    }

}
