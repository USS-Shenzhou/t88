package cn.ussshenzhou.t88.networkanalyzer.gui;

import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.modnetwork.ClientRequestServerNetworkDataPacket;
import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcher;
import cn.ussshenzhou.t88.networkanalyzer.SenderInfo;
import cn.ussshenzhou.t88.networkanalyzer.SizeAndTimes;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcherScreen extends TScreen {
    private final TTabPageContainer container = new TTabPageContainer();

    public NetworkWatcherScreen() {
        super(Component.literal("T88 Network Watcher"));
        this.add(container);
        container.newTab(Component.literal("Client"), new NetworkPanel(Component.translatable("gui.t88.net.options.client.tooltip")) {
            @Override
            public @NotNull ConcurrentHashMap<SenderInfo, SizeAndTimes> sent() {
                return NetworkWatcher.SENT.get();
            }

            @Override
            public @NotNull ConcurrentHashMap<SenderInfo, SizeAndTimes> received() {
                return NetworkWatcher.RECEIVED.get();
            }
        }).setCloseable(false);
        container.newTab(Component.literal("Server"), new NetworkPanel(Component.translatable("gui.t88.net.options.server.tooltip")) {
            @Override
            public void tickT() {
                super.tickT();
                if (this.isVisibleT() && this.isWorking()) {
                    PacketDistributor.sendToServer(new ClientRequestServerNetworkDataPacket());
                }
            }

            @Override
            public @NotNull ConcurrentHashMap<SenderInfo, SizeAndTimes> sent() {
                return NetworkWatcher.SERVER_SENT;
            }

            @Override
            public @NotNull ConcurrentHashMap<SenderInfo, SizeAndTimes> received() {
                return NetworkWatcher.SERVER_RECEIVED;
            }
        }).setCloseable(false);
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
}
