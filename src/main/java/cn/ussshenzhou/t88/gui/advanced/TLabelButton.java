package cn.ussshenzhou.t88.gui.advanced;

import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.widegt.TComponent;
import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TButton;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TLabelButton extends TLabel {
    protected TButton button;
    protected int normalBackGround = 0x00000000;
    protected int hoverBackGround = 0x7f_ffffff;

    public TLabelButton(Component s) {
        this(s, pButton -> {
        });
    }

    public TLabelButton(Component s, Button.OnPress onPress) {
        super(s);
        button = new TButton(Component.empty(), onPress);
        button.setSkipRenderAsBackend(true);
        this.add(button);
        this.setBorder(new Border(0xffffffff, -1));
        this.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setAutoScroll(false);
    }

    public TLabelButton(Component s, Button.OnPress onPress, int normalBackGround, int hoverBackGround) {
        this(s, onPress);
        this.normalBackGround = normalBackGround;
        this.hoverBackGround = hoverBackGround;
    }

    public void setOnPress(Button.OnPress onPress) {
        button.setOnPress(onPress);
    }

    public void onPress() {
        button.onPress();
    }

    @Override
    public void layout() {
        button.setBounds(0, 0, width, height);
        super.layout();
    }

    @Override
    public void tickT() {
        if (button.isHoveredOrFocused()) {
            this.setBackground(hoverBackGround);
        } else {
            this.setBackground(normalBackGround);
        }
        super.tickT();
    }

    public int getNormalBackGround() {
        return normalBackGround;
    }

    public TLabelButton setNormalBackGround(int normalBackGround) {
        this.normalBackGround = normalBackGround;
        return this;
    }

    public int getHoverBackGround() {
        return hoverBackGround;
    }

    public TLabelButton setHoverBackGround(int hoverBackGround) {
        this.hoverBackGround = hoverBackGround;
        return this;
    }

    public TButton getButton() {
        return button;
    }

    @Override
    @Deprecated
    public TComponent setBackground(int background) {
        return super.setBackground(background);
    }

    @Override
    @Deprecated
    public int getBackground() {
        return super.getBackground();
    }
}
