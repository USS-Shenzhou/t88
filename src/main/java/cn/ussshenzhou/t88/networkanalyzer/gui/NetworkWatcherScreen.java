package cn.ussshenzhou.t88.networkanalyzer.gui;

import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcherScreen extends TScreen {
    private final TTabPageContainer container = new TTabPageContainer();

    public NetworkWatcherScreen() {
        super(Component.literal("T88 Network Watcher"));
        this.add(container);
        container.newTab(Component.literal("Client"), new NetworkClientPanel()).setCloseable(false);
        container.newTab(Component.translatable("gui.t88.net.options"), new OptionsPanel()).setCloseable(false);
    }

    @Override
    public void layout() {
        container.setBounds(0, 0, width, height);
        super.layout();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public OptionsPanel getOptionsPanel() {
        return (OptionsPanel) container.getTabs()
                .stream()
                .filter(tab -> tab.getContent() instanceof OptionsPanel)
                .findFirst()
                .orElseThrow()
                .getContent();
    }
}
