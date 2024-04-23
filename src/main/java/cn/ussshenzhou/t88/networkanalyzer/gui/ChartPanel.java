package cn.ussshenzhou.t88.networkanalyzer.gui;

import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcher;
import cn.ussshenzhou.t88.networkanalyzer.SenderInfo;
import cn.ussshenzhou.t88.networkanalyzer.SizeAndTimes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author USS_Shenzhou
 */
public class ChartPanel extends TPanel {
    private double maxSize;
    private final LinkedList<Group> groups = new LinkedList<>();
    private static final int COLUMN = 30;
    private final NetworkWatcher.TR dir;

    public ChartPanel(NetworkWatcher.TR dir) {
        this.dir = dir;
    }

    int t = 0;

    @Override
    public void tickT() {
        getParentInstanceOfOptional(NetworkClientPanel.class).ifPresent(networkClientPanel -> {
            if (networkClientPanel.isWorking()) {
                if (t % 20 == 0) {
                    update();
                    layout();
                    t = 0;
                }
                t++;
            }
        });
        super.tickT();
    }

    @Override
    public void layout() {
        int i = COLUMN - groups.size(), groupHeight = height / COLUMN;
        for (Group group : groups) {
            group.setBounds(0, groupHeight * i, width, groupHeight);
            i++;
        }
        super.layout();
    }

    private void update() {
        if (groups.size() == COLUMN) {
            this.remove(groups.poll());
        }
        var map = dir == NetworkWatcher.TR.T ? NetworkWatcher.SENT : NetworkWatcher.RECEIVED;
        var newGroup = new Group(map);
        groups.offer(newGroup);
        this.add(newGroup);
        maxSize = groups.stream().max(Comparator.comparingDouble(group -> group.groupSize)).orElseGet(() -> new Group(new HashMap<>())).groupSize;
    }

    private void getReadableSize(StringBuilder s, int bytes) {
        if (getTopParentScreenAsOptional(NetworkWatcherScreen.class).orElseThrow().getOptionsPanel().getUnit().getSelectedOptional().orElseThrow().getContent().contains("bit")) {
            bytes *= 8;
            if (bytes < 1000) {
                s.append(bytes).append(" §7bp§r");
            } else if (bytes < 1000 * 1000) {
                s.append(String.format("%.1f §7Kbp§r", bytes / 1024f));
            } else {
                s.append(String.format("%.1f §7Mbp§r", bytes / 1024 * 1024f));
            }
        } else {
            if (bytes < 1000) {
                s.append(bytes).append(" §7Bytes§r");
            } else if (bytes < 1000 * 1000) {
                s.append(String.format("%.1f §7KiB§r", bytes / 1024f));
            } else {
                s.append(String.format("%.1f §7MiB§r", bytes / 1024 * 1024f));
            }
        }
    }

    public class Group extends TPanel {
        private HashMap<String, ModPacketInfoPanel> mods = new HashMap<>();
        float groupSize;

        public Group(Map<SenderInfo, SizeAndTimes> all) {
            all.forEach((senderInfo, sizeAndTimes) -> {
                mods.compute(senderInfo.modId(), (id, modPacketInfoPanel) -> {
                    if (modPacketInfoPanel == null) {
                        modPacketInfoPanel = new ModPacketInfoPanel(id);
                        Group.this.add(modPacketInfoPanel);
                    }
                    modPacketInfoPanel.addPacket(senderInfo.clazz(), sizeAndTimes);
                    return modPacketInfoPanel;
                });
            });
            groupSize = (float) mods.values().stream().mapToDouble(panel -> {
                var l = Math.log(panel.totalSize + 1);
                return l * l * l;
            }).sum();
        }

        @Override
        public void layout() {
            int i = 0;
            for (Map.Entry<String, ModPacketInfoPanel> entry : mods.entrySet()) {
                var panel = entry.getValue();
                var l = Math.log(panel.totalSize + 1);
                int w = (int) (width * l * l * l / maxSize);
                entry.getValue().setBounds(i, 0, w, height);
                i += w;
            }
            super.layout();
        }
    }

    public class ModPacketInfoPanel extends TPanel {
        private final String modId;
        private final Map<String, SizeAndTimes> packets = new HashMap<>();
        private int totalSize;
        private int totalPackets;

        public ModPacketInfoPanel(String modId) {
            this.modId = modId;
            this.setBackground(modId.hashCode() | 0xff000000);
        }

        public void addPacket(String packet, SizeAndTimes sizeAndTimes) {
            packets.put(packet, sizeAndTimes);
            totalSize += sizeAndTimes.getSize();
            totalPackets += sizeAndTimes.getTimes();
        }

        @Nullable
        @Override
        public Tooltip getTooltip() {
            if (super.getTooltip() == null) {
                setTooltip(generateTooltip());
            }
            return super.getTooltip();
        }

        private Tooltip generateTooltip() {
            var s = new StringBuilder(modId);
            s.append("\n");
            getReadableSize(s, totalSize);
            s.append(" ");
            s.append(totalPackets);
            s.append(" §7packets§r");
            packets.entrySet().stream().sorted(Comparator.comparingInt(e -> -e.getValue().getSize())).forEach(e -> {
                s.append("\n");
                s.append(getColorBySize(e.getValue().getSize()));
                s.append(e.getKey());
                s.append("§r ");
                getReadableSize(s, e.getValue().getSize());
                s.append(" ");
                s.append(e.getValue().getTimes());
                s.append(" §7packets§r");
            });
            var c = Component.literal(s.toString());
            return new Tooltip(c, c) {
                @Override
                public @NotNull List<FormattedCharSequence> toCharSequence(@NotNull Minecraft pMinecraft) {
                    if (this.cachedTooltip == null) {
                        this.cachedTooltip = splitTooltip(pMinecraft, this.message);
                    }

                    return this.cachedTooltip;
                }

                public static @NotNull List<FormattedCharSequence> splitTooltip(Minecraft pMinecraft, @NotNull Component pMessage) {
                    //needtest compatibility with modern ui
                    return pMinecraft.font.split(pMessage, Math.max((int) (pMinecraft.getWindow().getGuiScaledWidth() * 0.4), 340));
                }
            };
        }

        private String getColorBySize(int size) {
            float f = (float) size / totalSize;
            if (f < 0.2f) {
                return "§b";
            } else if (f < 0.4f) {
                return "§a";
            } else if (f < 0.6f) {
                return "§6";
            } else if (f < 0.8f) {
                return "§c";
            } else {
                return "§4";
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ModPacketInfoPanel modPacketInfoPanel) {
                return modId.equals(modPacketInfoPanel.modId);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return modId.hashCode();
        }
    }
}
