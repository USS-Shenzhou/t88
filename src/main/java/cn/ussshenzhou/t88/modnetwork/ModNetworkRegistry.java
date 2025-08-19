package cn.ussshenzhou.t88.modnetwork;

import cn.ussshenzhou.t88.T88;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber
public class ModNetworkRegistry {

    @SubscribeEvent
    public static void networkPacketRegistry(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(T88.MOD_ID);

        registrar.commonBidirectional(ServerNetworkDataPacket.TYPE, ServerNetworkDataPacket.STREAM_CODEC,
                (payload, context) -> {
                },
                ServerNetworkDataPacket::clientHandler
        );

        registrar.commonBidirectional(ClientRequestServerNetworkDataPacket.TYPE, ClientRequestServerNetworkDataPacket.STREAM_CODEC,
                ClientRequestServerNetworkDataPacket::serverHandler,
                (payload, context) -> {
                }
        );
    }
}
