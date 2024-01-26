package cn.ussshenzhou.t88.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jline.utils.Log;

import java.util.Locale;

/**
 * @author USS_Shenzhou
 */
public class NetworkHelper {

    public static String classNameToResLocName(Class<?> clazz) {
        return classNameToResLocName(clazz.getSimpleName());
    }

    protected static String classNameToResLocName(String s) {
        return s.toLowerCase(Locale.ENGLISH).replaceAll("\\$", "_");
    }

    public static <MSG> void sendToServer(MSG packet) {
        try {
            PacketDistributor.SERVER.noArg().send((CustomPacketPayload) packet);
        } catch (ClassCastException e) {
            LogUtils.getLogger().error(e.getMessage());
        }
    }

    public static <MSG> void sendToPlayer(ServerPlayer target, MSG packet) {
        try {
            PacketDistributor.PLAYER.with(target).send((CustomPacketPayload) packet);
        } catch (ClassCastException e) {
            LogUtils.getLogger().error(e.getMessage());
        }
    }

    public static <MSG> void sendTo(PacketDistributor.PacketTarget target, MSG packet) {
        try {
            target.send((CustomPacketPayload) packet);
        } catch (ClassCastException e) {
            LogUtils.getLogger().error(e.getMessage());
        }
    }
}
