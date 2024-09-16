package cn.ussshenzhou.t88.render.fixedblockentity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author USS_Shenzhou
 */
public class SimpleMultiBufferSource implements MultiBufferSource {
    private final Map<RenderType, VertexConsumer> buffers = new HashMap<>();

    public static SimpleMultiBufferSource of(RenderType type, VertexConsumer consumer) {
        var instance = new SimpleMultiBufferSource();
        instance.buffers.put(type, consumer);
        return instance;
    }

    public SimpleMultiBufferSource put(RenderType type, VertexConsumer consumer) {
        buffers.put(type, consumer);
        return this;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {
        var r = buffers.get(renderType);
        if (r != null) {
            return r;
        } else {
            throw new IllegalStateException(String.format("Specified RenderType %s does not exist. " +
                    "You need to add it manually, and you may need to register it with @ChunkBufferRenderType if you are using IFixedModelBlockEntity.", renderType));
        }
    }
}
