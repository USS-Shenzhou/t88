package cn.ussshenzhou.t88.networkanalyzer.gui;

import cn.ussshenzhou.t88.gui.advanced.TOptionsPanel;
import cn.ussshenzhou.t88.gui.widegt.TCycleButton;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class OptionsPanel extends TOptionsPanel {
    private final TCycleButton<String> unit;

    public OptionsPanel() {
        addOptionSplitter(Component.translatable("gui.t88.net.options.general"));
        unit = addOptionCycleButton(Component.translatable("gui.t88.net.options.unit"),
                List.of("gui.t88.net.options.byte", "gui.t88.net.options.bit"),
                s -> b -> {
                }
        ).getB();
    }

    public TCycleButton<String> getUnit() {
        return unit;
    }
}
