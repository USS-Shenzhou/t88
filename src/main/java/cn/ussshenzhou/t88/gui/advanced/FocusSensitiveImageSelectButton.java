package cn.ussshenzhou.t88.gui.advanced;

import cn.ussshenzhou.t88.gui.widegt.TButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author USS_Shenzhou
 */
public class FocusSensitiveImageSelectButton extends HoverSensitiveImageButton{
    private boolean selected = false;

    public FocusSensitiveImageSelectButton(Component text1, Button.OnPress onPress, @Nullable ResourceLocation backgroundImageLocation,@Nullable ResourceLocation backgroundImageLocationFocused) {
        super(text1, onPress, backgroundImageLocation, backgroundImageLocationFocused);
        this.remove(button);
        this.button = new TButton(Component.literal(""), onPress) {
            @Override
            public void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                return;
            }

            @Override
            public void onPress() {
                super.onPress();
                selected = true;
            }

            @Override
            public boolean isHoveredOrFocused() {
                return super.isHoveredOrFocused() || selected;
            }
        };
        this.add(button);
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
