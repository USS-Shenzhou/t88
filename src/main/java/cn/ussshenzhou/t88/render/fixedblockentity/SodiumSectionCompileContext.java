package cn.ussshenzhou.t88.render.fixedblockentity;

import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.BakedChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class SodiumSectionCompileContext implements SectionCompileContext {
    private final ChunkBuildContext chunkBuildContext;
    private final BlockPos pos;
    private final BlockState state;
    private final TranslucentGeometryCollector collector;

    public RenderType bakedModelRenderType = RenderType.solid();
    @Nullable
    public BakedModel bakedModel = null;
    @Nullable
    public BlockState bakedModelBlockState = null;
    public boolean needRenderAdditional = false;
    public Consumer<PoseStack> beforeBakedModel = this.resetToBlock000();

    @Override
    public Consumer<PoseStack> resetToBlock000() {
        return poseStack -> IFixedModelBlockEntity.resetToBlock000(pos, bakedModelRenderType, poseStack);
    }

    @Override
    public Consumer<PoseStack> rotateByState() {
        return poseStack -> RenderUtil.rotateAroundBlockCenter(BlockUtil.justGetFacing(bakedModelBlockState, state), poseStack);
    }

    public SodiumSectionCompileContext(ChunkBuildContext chunkBuildContext, BlockPos pos, BlockState state, TranslucentGeometryCollector collector) {
        this.chunkBuildContext = chunkBuildContext;
        this.pos = pos;
        this.state = state;
        this.collector = collector;
    }

    @Override
    public SodiumSectionCompileContext withRenderType(RenderType renderType) {
        this.bakedModelRenderType = renderType;
        return this;
    }

    @Override
    public RenderType getBakedModelRenderType() {
        return bakedModelRenderType;
    }

    @Override
    public SodiumSectionCompileContext withBakedModel(BakedModel bakedModel) {
        this.bakedModel = bakedModel;
        return this;
    }

    @Nullable
    @Override
    public BakedModel getBakedModel() {
        return bakedModel;
    }

    @Override
    public SodiumSectionCompileContext withBlockState(BlockState bakedModelBlockState) {
        this.bakedModelBlockState = bakedModelBlockState;
        return this;
    }

    @Nullable
    @Override
    public BlockState getBakedModelBlockState() {
        return bakedModelBlockState;
    }

    @Override
    public SodiumSectionCompileContext withAdditionalRender() {
        this.needRenderAdditional = true;
        return this;
    }

    @Override
    public boolean hasAdditionalRender() {
        return needRenderAdditional;
    }

    @Override
    public SodiumSectionCompileContext withPrepareBakedModelRender(Consumer<PoseStack> preparer) {
        this.beforeBakedModel = preparer;
        return this;
    }

    @Override
    public Consumer<PoseStack> getPreparer() {
        return beforeBakedModel;
    }

    @Override
    public VertexConsumer getVertexConsumer(RenderType renderType) {
        Material material;
        try {
            material = DefaultMaterials.forRenderLayer(renderType);
            return this.chunkBuildContext.buffers.get(material).asFallbackVertexConsumer(material, collector);
        } catch (IllegalArgumentException ignored1) {
            try {
                var buildersField = ChunkBuildBuffers.class.getDeclaredField("builders");
                buildersField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var builders = (Reference2ReferenceOpenHashMap<TerrainRenderPass, BakedChunkModelBuilder>) buildersField.get(chunkBuildContext.buffers);
                var renderTypeField = TerrainRenderPass.class.getDeclaredField("renderType");
                renderTypeField.setAccessible(true);
                return builders.get(
                        builders.keySet()
                                .stream()
                                .filter(terrainRenderPass -> {
                                    try {
                                        return renderTypeField.get(terrainRenderPass) == renderType;
                                    } catch (IllegalAccessException e) {
                                        return false;
                                    }
                                })
                                .findFirst()
                                .orElseThrow()
                ).asFallbackVertexConsumer(SectionBufferRenderTypeHelper.Sodium.SODIUM_MATERIALS.get(renderType), collector);
            } catch (Exception ignored) {
                material = DefaultMaterials.forBlockState(state);
                return this.chunkBuildContext.buffers.get(material).asFallbackVertexConsumer(material, collector);
            }
        }
    }

    /*@Override
    public VertexConsumer getVertexConsumer(RenderType renderType) {
        var material = getMaterial(renderType);
        var chunkMeshBufferBuilder = getChunkMeshBufferBuilder(renderType);
        return new SodiumWrappedChunkMeshBufferBuilder(chunkMeshBufferBuilder, material);
    }

    private Material getMaterial(RenderType renderType) {
        Material material;
        try {
            material = DefaultMaterials.forRenderLayer(renderType);
        } catch (IllegalArgumentException ignored) {
            material = DefaultMaterials.forBlockState(state);
        }
        return material;
    }

    private ChunkMeshBufferBuilder getChunkMeshBufferBuilder(RenderType renderType) {
        Material material;
        try {
            material = DefaultMaterials.forRenderLayer(renderType);
            return this.chunkBuildContext.buffers.get(material).getVertexBuffer(ModelQuadFacing.UNASSIGNED);
        } catch (IllegalArgumentException ignored1) {
            try {
                var buildersField = ChunkBuildBuffers.class.getDeclaredField("builders");
                buildersField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var builders = (Reference2ReferenceOpenHashMap<TerrainRenderPass, BakedChunkModelBuilder>) buildersField.get(chunkBuildContext.buffers);
                var renderTypeField = TerrainRenderPass.class.getDeclaredField("renderType");
                renderTypeField.setAccessible(true);
                return builders.get(
                        builders.keySet()
                                .stream()
                                .filter(terrainRenderPass -> {
                                    try {
                                        return renderTypeField.get(terrainRenderPass) == renderType;
                                    } catch (IllegalAccessException e) {
                                        return false;
                                    }
                                })
                                .findFirst()
                                .orElseThrow()
                ).getVertexBuffer(ModelQuadFacing.UNASSIGNED);
            } catch (Exception ignored) {
                material = DefaultMaterials.forBlockState(state);
                return this.chunkBuildContext.buffers.get(material).getVertexBuffer(ModelQuadFacing.UNASSIGNED);
            }
        }
    }*/

}
