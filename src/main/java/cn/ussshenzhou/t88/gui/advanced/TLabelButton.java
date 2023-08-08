package cn.ussshenzhou.t88.gui.advanced;

import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TLabelButton extends TLabel {

    public TLabelButton(Component s) {
        this(s, 0xffffffff);
    }

    public TLabelButton(Component s, int foreground) {
        super(s, foreground);

    }
}
