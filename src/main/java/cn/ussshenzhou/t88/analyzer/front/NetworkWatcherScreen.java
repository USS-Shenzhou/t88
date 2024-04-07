package cn.ussshenzhou.t88.analyzer.front;

import cn.ussshenzhou.t88.analyzer.back.NetworkWatcher;
import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcherScreen extends TScreen {

    private TLabelButton client = new TLabelButton(Component.literal(""), pButton -> {
        //TODO
    });
    /*private TLabelButton server = new TLabelButton(Component.literal(""), pButton -> {
        //TODO
    });*/

    public NetworkWatcherScreen() {
        super(Component.literal("T88 Network Watcher"));
        this.add(client);
        //this.add(server);
        updateClient();
        //updateServer();
    }

    private void updateClient() {
        StringBuilder s = new StringBuilder("Client: ");
        int t = NetworkWatcher.SENT.values().stream().mapToInt(i -> i + 63).sum();
        int r = NetworkWatcher.RECEIVED.values().stream().mapToInt(i -> i + 63).sum();
        s.append("↑ ");
        getReadableSize(s, t);
        s.append("  ↓ ");
        getReadableSize(s, r);
        client.setText(Component.literal(s.toString()));
    }

    /*private void updateServer() {
        StringBuilder s = new StringBuilder("Server: ");
        if (Minecraft.getInstance().player.hasPermissions(2)) {
            if (!NetworkWatcher.fromServerUpdated) {
                int t = NetworkWatcher.FROM_SERVER_SENT.values().stream().mapToInt(i -> i + 63).sum();
                int r = NetworkWatcher.FROM_SERVER_RECEIVED.values().stream().mapToInt(i -> i + 63).sum();
                s.append("↑ ");
                getReadableSize(s, t);
                s.append("  ↓ ");
                getReadableSize(s, r);
                NetworkWatcher.fromServerUpdated = true;
            }
        } else {
            s.append("Permission Denied");
        }
        client.setText(Component.literal(s.toString()));
    }*/

    private void getReadableSize(StringBuilder s, int bytes) {
        if (bytes < 1000) {
            s.append(bytes).append(" Bytes/S");
        } else if (bytes < 1000 * 1000) {
            s.append(String.format("%.1f KiB/S", bytes / 1024f));
        } else {
            s.append(String.format("%.1f MiB/S", bytes / 1024 * 1024f));
        }
    }

    int t = 0;

    @Override
    public void tick() {
        super.tick();
        if (t % 20 == 0) {
            updateClient();
            //updateServer();
            layout();
        }
        t++;
    }

    @Override
    public void layout() {
        super.layout();
        client.setBounds((int) (width * 0.05), (int) (height * 0.05), client.getPreferredSize().x + 10, 20);
        //var w = server.getPreferredSize().x + 10;
        //server.setBounds((int) (width * 0.95 - w), (int) (height * 0.05), w, 20);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
