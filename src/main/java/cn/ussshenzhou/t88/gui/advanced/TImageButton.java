package cn.ussshenzhou.t88.gui.advanced;

import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.ColorManager;
import cn.ussshenzhou.t88.gui.widegt.TButton;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * @author USS_Shenzhou
 */
public class TImageButton extends TImage {
    protected TButton button;
    protected int normalBorder = ColorManager.get().defaultBackground();
    protected int hoverBorder = ColorManager.get().themeColor();

    public TImageButton(ResourceLocation imageLocation, Button.OnPress onPress) {
        super(imageLocation);
        button = new TButton(Component.empty(), onPress);
        button.setSkipRenderAsBackend(true);
        this.add(button);
        this.setBorder(new Border(normalBorder, -1));
    }

    public TImageButton(ResourceLocation imageLocation, Button.OnPress onPress, int normalBorder, int hoverBorder) {
        this(imageLocation, onPress);
        this.normalBorder = normalBorder;
        this.hoverBorder = hoverBorder;
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
            this.setBorder(new Border(hoverBorder, -1));
        } else {
            this.setBorder(new Border(normalBorder, -1));
        }
        super.tickT();
    }

    public int getNormalBorder() {
        return normalBorder;
    }

    public void setNormalBorder(int normalBorder) {
        this.normalBorder = normalBorder;
    }

    public int getHoverBorder() {
        return hoverBorder;
    }

    public void setHoverBorder(int hoverBorder) {
        this.hoverBorder = hoverBorder;
    }

    public TButton getButton() {
        return button;
    }
}
