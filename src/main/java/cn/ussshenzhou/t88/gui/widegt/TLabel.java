package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.Vec2i;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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

    Font font = Minecraft.getInstance().font;

    public TLabel() {
        this.text = new TextComponent("");
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
        Stream.of(lines).forEach(line -> {
            textLines.add(new TextComponent(line));
        });
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

    public void setFontSize(int size) {
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
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        pPoseStack.pushPose();
        float scaleFactor = fontSize / 7f;
        pPoseStack.scale(scaleFactor, scaleFactor, 1);
        int x0 = (int) (getAlignedX() / scaleFactor);
        int y0 = (int) ((y + (height - (fontSize + lineSpacing) * textLines.size()) / 2) / scaleFactor);
        for (Component component : textLines) {
            drawString(pPoseStack, font, component, x0, y0, foreground);
            y0 += (fontSize + lineSpacing) / scaleFactor;
        }
        pPoseStack.popPose();
    }

    protected int getAlignedX() {
        int textWidth = getPreferredSize().x;
        if (width == textWidth) {
            return x;
        }
        switch (horizontalAlignment) {
            case CENTER:
                return x + (width - textWidth) / 2;
            case RIGHT:
                return x + width - textWidth;
            default:
                return x;
        }
    }

    @Override
    public Vec2i getPreferredSize() {
        return new Vec2i(Mth.ceil(font.width(text) * fontSize / 7), Mth.ceil((fontSize + lineSpacing) * fontSize / 7f));
    }

}
