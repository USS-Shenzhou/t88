package cn.ussshenzhou.t88.modnetwork;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcher;
import cn.ussshenzhou.t88.networkanalyzer.SenderInfo;
import cn.ussshenzhou.t88.networkanalyzer.SizeAndTimes;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

/**
 * @author USS_Shenzhou
 */
@MethodsReturnNonnullByDefault
public record ServerNetworkDataPacket(HashMap<SenderInfo, SizeAndTimes> sent,
                                      HashMap<SenderInfo, SizeAndTimes> received) implements CustomPacketPayload {

    public static final Type<ServerNetworkDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(T88.MOD_ID, "server_network_data"));

    public static final StreamCodec<ByteBuf, ServerNetworkDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, SenderInfo.STREAM_CODEC, SizeAndTimes.STREAM_CODEC),
            ServerNetworkDataPacket::sent,
            ByteBufCodecs.map(HashMap::new, SenderInfo.STREAM_CODEC, SizeAndTimes.STREAM_CODEC),
            ServerNetworkDataPacket::received,
            ServerNetworkDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void clientHandler(IPayloadContext context){
        NetworkWatcher.SERVER_SENT.clear();
        NetworkWatcher.SERVER_SENT.putAll(sent);
        NetworkWatcher.SERVER_RECEIVED.clear();
        NetworkWatcher.SERVER_RECEIVED.putAll(received);
    }
}
