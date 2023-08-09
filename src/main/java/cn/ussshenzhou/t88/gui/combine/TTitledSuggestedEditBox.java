package cn.ussshenzhou.t88.gui.combine;

import cn.ussshenzhou.t88.gui.advanced.TSuggestedEditBox;
import org.joml.Vector2i;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TTitledSuggestedEditBox extends TTitledComponent<TSuggestedEditBox> {
    public TTitledSuggestedEditBox(Component titleText, TSuggestedEditBox component) {
        super(titleText, component);
    }

    public TTitledSuggestedEditBox(Component titleText, Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        super(titleText, new TSuggestedEditBox(consumer));
    }

    public TTitledSuggestedEditBox(Component titleText, CommandDispatcher<CommandSourceStack> dispatcher) {
        super(titleText, new TSuggestedEditBox(dispatcher));
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(
                widget.getPreferredSize().x,
                title.getHeight() + widget.getPreferredSize().y
        );
    }
}
