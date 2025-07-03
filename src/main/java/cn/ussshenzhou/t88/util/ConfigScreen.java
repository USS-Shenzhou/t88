package cn.ussshenzhou.t88.util;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.TOptionsPanel;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class ConfigScreen extends TScreen {
    private final TLabel title = new TLabel(Component.literal("T88 Config"));
    private final TOptionsPanel config = new TOptionsPanel();

    public ConfigScreen() {
        super(Component.empty());
        this.add(title);
        title.setFontSize(TLabel.STD_FONT_SIZE * 2);
        this.add(config);
        config.addOptionSplitter(Component.translatable("gui.t88.net.options.general"));
        config.addOptionCycleButtonInit(Component.translatable("gui.t88.net.options.style"), List.of(Boolean.TRUE, Boolean.FALSE),
                boo -> b -> ConfigHelper.getConfigWrite(T88Config.class, c -> {
                    b.getSelectedOptional().ifPresent(e -> c.replaceStyle = e.getContent());
                }), e -> e.getContent() == ConfigHelper.getConfigRead(T88Config.class).replaceStyle);
        config.addOptionSplitter(Component.translatable("gui.t88.net.options.net"));
        config.addOptionCycleButtonInit(Component.translatable("gui.t88.net.options.unit"), List.of(T88Config.NetworkUnit.values()),
                s -> b -> ConfigHelper.getConfigWrite(T88Config.class, c -> {
                    c.networkUnit = b.getSelectedIndex() == 0 ? T88Config.NetworkUnit.BYTE : T88Config.NetworkUnit.BIT;
                }), e -> e.getContent() == ConfigHelper.getConfigRead(T88Config.class).networkUnit);
    }

    @Override
    public void layout() {
        title.setBounds((int) (0.01 * width), (int) (0.02 * height), title.getPreferredSize());
        LayoutHelper.BBottomOfA(config, 6, title, (int) (0.98 * width), (int) (0.98 * height - title.getYT() - title.getHeight() - 6));
        super.layout();
    }
}
