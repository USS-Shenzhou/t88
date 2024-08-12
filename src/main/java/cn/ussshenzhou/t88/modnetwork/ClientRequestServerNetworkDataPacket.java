package cn.ussshenzhou.t88.modnetwork;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

/**
 * @author USS_Shenzhou
 */
@MethodsReturnNonnullByDefault
public record ClientRequestServerNetworkDataPacket() implements CustomPacketPayload {
    public static final Type<ClientRequestServerNetworkDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(T88.MOD_ID, "client_request_server_network_data"));

    public static final StreamCodec<ByteBuf, ClientRequestServerNetworkDataPacket> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
    }, buffer -> new ClientRequestServerNetworkDataPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void serverHandler(IPayloadContext context) {
        var player = (ServerPlayer) context.player();
        if (player.isCreative() || player.hasPermissions(2)) {
            var s = new HashMap<>(NetworkWatcher.SENT.get());
            var r = new HashMap<>(NetworkWatcher.RECEIVED.get());
            PacketDistributor.sendToPlayer(player, new ServerNetworkDataPacket(s, r));
        }
    }
}
