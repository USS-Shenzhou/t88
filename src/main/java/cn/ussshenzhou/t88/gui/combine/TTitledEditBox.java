package cn.ussshenzhou.t88.gui.combine;

import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TEditBox;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TTitledEditBox extends TTitledComponent<TEditBox> {
    public TTitledEditBox(Component titleText, TEditBox component) {
        super(titleText, component);
    }

    public TTitledEditBox(Component titleText, Component editBoxText) {
        this(titleText, new TEditBox(editBoxText));
    }

    public TTitledEditBox(Component titleText) {
        this(titleText, new TEditBox());
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(
                widget.getPreferredSize().x,
                title.getHeight() + widget.getPreferredSize().y
        );
    }
}
