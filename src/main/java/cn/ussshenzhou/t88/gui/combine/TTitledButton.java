package cn.ussshenzhou.t88.gui.combine;

import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TTitledButton extends TTitledComponent<TButton> {
    public TTitledButton(Component titleText, Component buttonText) {
        super(titleText, new TButton(buttonText));
    }

    public TTitledButton(Component titleText, Component buttonText, Button.OnPress onPress) {
        super(titleText, new TButton(buttonText, onPress));
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(
                TButton.RECOMMEND_SIZE.x,
                title.getHeight() + TButton.RECOMMEND_SIZE.y
        );
    }
}
