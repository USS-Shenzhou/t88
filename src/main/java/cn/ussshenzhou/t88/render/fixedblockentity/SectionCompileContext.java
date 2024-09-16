package cn.ussshenzhou.t88.render.fixedblockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public interface SectionCompileContext {

    Consumer<PoseStack> resetToBlock000();

    Consumer<PoseStack> rotateByState();

    SectionCompileContext withRenderType(RenderType renderType);

    RenderType getBakedModelRenderType();

    SectionCompileContext withBakedModel(BakedModel bakedModel);

    @Nullable
    BakedModel getBakedModel();

    SectionCompileContext withBlockState(BlockState bakedModelBlockState);

    @Nullable
    BlockState getBakedModelBlockState();

    SectionCompileContext withAdditionalRender();

    boolean hasAdditionalRender();

    SectionCompileContext withPrepareBakedModelRender(Consumer<PoseStack> preparer);

    Consumer<PoseStack> getPreparer();

    VertexConsumer getVertexConsumer(RenderType renderType);

    default SimpleMultiBufferSource getSimpleMultiBufferSource(RenderType... types) {
        var r = SimpleMultiBufferSource.of(types[0], this.getVertexConsumer(types[0]));
        for (int i = 1; i < types.length; i++) {
            r.put(types[i], this.getVertexConsumer(types[1]));
        }
        return r;
    }
}
