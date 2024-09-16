package cn.ussshenzhou.t88.render.fixedblockentity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.builder.ChunkMeshBufferBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;

/**
 * @author USS_Shenzhou
 */
@MethodsReturnNonnullByDefault
public class SodiumWrappedChunkMeshBufferBuilder implements VertexConsumer {
    private final ChunkMeshBufferBuilder chunkMeshBufferBuilder;
    private final Material material;
    private final ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();
    private int vertexIndex = 0;
    private boolean next = false;

    public SodiumWrappedChunkMeshBufferBuilder(ChunkMeshBufferBuilder chunkMeshBufferBuilder, Material material) {
        this.chunkMeshBufferBuilder = chunkMeshBufferBuilder;
        this.material = material;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        if (vertexIndex == 3) {
            endLast();
        }
        if (next) {
            vertexIndex++;
        }
        ChunkVertexEncoder.Vertex vertex = this.vertices[vertexIndex];
        vertex.x = x;
        vertex.y = y;
        vertex.z = z;
        next = true;
        return potentiallyEndVertex();
    }

    private void endLast() {
        chunkMeshBufferBuilder.push(vertices, material);
        vertexIndex = 0;
        next = false;
        for (ChunkVertexEncoder.Vertex vertex : vertices) {
            vertex.x = 0;
            vertex.y = 0;
            vertex.z = 0;
            vertex.u = 0;
            vertex.v = 0;
            vertex.ao = 0;
            vertex.color = 0;
            vertex.light = 0;
        }
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        ChunkVertexEncoder.Vertex vertex = this.vertices[vertexIndex];
        vertex.color = ColorABGR.pack(red, green, blue, alpha);
        return potentiallyEndVertex();
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        ChunkVertexEncoder.Vertex vertex = this.vertices[vertexIndex];
        vertex.u = u;
        vertex.v = v;
        return potentiallyEndVertex();
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        ChunkVertexEncoder.Vertex vertex = this.vertices[vertexIndex];
        return potentiallyEndVertex();
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        ChunkVertexEncoder.Vertex vertex = this.vertices[vertexIndex];
        vertex.light = (v & '\uffff') << 16 | u & '\uffff';
        return potentiallyEndVertex();
    }

    @Override
    public @NotNull VertexConsumer setLight(int uv) {
        ChunkVertexEncoder.Vertex vertex = this.vertices[this.vertexIndex];
        vertex.light = uv;
        return potentiallyEndVertex();
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        ChunkVertexEncoder.Vertex vertex = this.vertices[vertexIndex];
        return potentiallyEndVertex();
    }

    public VertexConsumer potentiallyEndVertex() {

        return this;
    }
}
