package cn.ussshenzhou.t88.networkanalyzer.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcher;
import cn.ussshenzhou.t88.networkanalyzer.SenderInfo;
import cn.ussshenzhou.t88.networkanalyzer.SizeAndTimes;
import cn.ussshenzhou.t88.util.T88Config;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author USS_Shenzhou
 */
public abstract class NetworkPanel extends TPanel {

    private final TLabel general = new TLabel();
    private final TTabPageContainer charts = new TTabPageContainer();
    private final TLabelButton pause = new TLabelButton(Component.translatable("gui.t88.net.pause"), this::switchPause);

    private boolean working = true;

    public NetworkPanel(Component tooltip) {
        this.add(general);
        general.setTooltip(Tooltip.create(tooltip));
        this.add(charts);
        charts.newTab(Component.literal("Send"), new ChartPanel(NetworkWatcher.TR.T)).setCloseable(false);
        charts.newTab(Component.literal("Receive"), new ChartPanel(NetworkWatcher.TR.R)).setCloseable(false);
        this.add(pause);
        pause.setBorder(new Border(0xffffffff, 1));
    }

    private void switchPause(Button button) {
        var p = Component.translatable("gui.t88.net.pause");
        if (pause.getText().getString().equals(p.getString())) {
            setWorking(false);
            pause.setText(Component.translatable("gui.t88.net.resume"));
        } else {
            setWorking(true);
            pause.setText(p);
        }
    }

    private void setWorking(boolean working) {
        this.working = working;
    }

    public boolean isWorking() {
        return working;
    }

    @Override
    public void layout() {
        general.setBounds(10, 6, general.getPreferredSize().x, 20);
        charts.setBounds((int) (width * 0.05), general.getYT() + 8, (int) (width * 0.9), (int) (height * 0.85));
        LayoutHelper.BBottomOfA(pause, 1, charts, 100, 20);
        super.layout();
    }

    int t = 0;

    @Override
    public void tickT() {
        if (working) {
            if (t % 20 == 0) {
                general.setText(getGeneralUpAndDown());
                layout();
                t = 0;
            }
            t++;
        }
        super.tickT();
    }

    @Nonnull
    public abstract ConcurrentHashMap<SenderInfo, SizeAndTimes> sent();

    @Nonnull
    public abstract ConcurrentHashMap<SenderInfo, SizeAndTimes> received();

    private Component getGeneralUpAndDown() {
        StringBuilder s = new StringBuilder();
        int t = sent().values().stream().mapToInt(SizeAndTimes::getSize).sum();
        int r = received().values().stream().mapToInt(SizeAndTimes::getSize).sum();
        s.append("↑ ");
        getReadableSize(s, t);
        s.append(" ");
        s.append(sent().values().stream().mapToInt(SizeAndTimes::getTimes).sum());
        s.append(" §7packets§r");
        s.append("  ↓ ");
        getReadableSize(s, r);
        s.append(" ");
        s.append(received().values().stream().mapToInt(SizeAndTimes::getTimes).sum());
        s.append(" §7packets§r");
        return Component.literal(s.toString());
    }

    private void getReadableSize(StringBuilder s, int bytes) {
        if (ConfigHelper.getConfigRead(T88Config.class).networkUnit == T88Config.NetworkUnit.BIT) {
            bytes *= 8;
            if (bytes < 1000) {
                s.append(bytes).append(" §7bps§r");
            } else if (bytes < 1000 * 1000) {
                s.append(String.format("%.1f §7Kbps§r", bytes / 1024f));
            } else {
                s.append(String.format("%.2f §7Mbps§r", bytes / (1024 * 1024f)));
            }
        } else {
            if (bytes < 1000) {
                s.append(bytes).append(" §7Bytes/S§r");
            } else if (bytes < 1000 * 1000) {
                s.append(String.format("%.1f §7KiB/S§r", bytes / 1024f));
            } else {
                s.append(String.format("%.2f §7MiB/S§r", bytes / (1024 * 1024f)));
            }
        }
    }
}
