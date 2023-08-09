package cn.ussshenzhou.t88.gui.combine;

import cn.ussshenzhou.t88.gui.advanced.TSimpleConstrainedEditBox;
import org.joml.Vector2i;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TTitledSimpleConstrainedEditBox extends TTitledComponent<TSimpleConstrainedEditBox> {
    public TTitledSimpleConstrainedEditBox(Component titleText, TSimpleConstrainedEditBox component) {
        super(titleText, component);
    }

    public TTitledSimpleConstrainedEditBox(Component titleText, ArgumentType<?> argument) {
        this(titleText, new TSimpleConstrainedEditBox(argument));
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(
                widget.getPreferredSize().x,
                title.getHeight() + widget.getPreferredSize().y
        );
    }
}
