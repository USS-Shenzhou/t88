package cn.ussshenzhou.t88.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public class RawQuad {
    /*
        ----------> U
        |
        |    p0  p3   Y
        |    |   |    |
        |    p1->p2   |
        V             |
                      |
        X/Z <----------
     */

    private final Point[] points;
    private final Direction direction;
    private final BakedNormals bakedNormals;
    private final BakedColors bakedColors;
    private final BakedQuad.MaterialInfo materialInfo;

    static VertexFormat format = DefaultVertexFormat.BLOCK;
    static List<VertexFormatElement> elements = format.getElements();

    public RawQuad(Direction direction, BakedNormals bakedNormals, BakedColors bakedColors, BakedQuad.MaterialInfo materialInfo, Point... points) {
        this.points = points;
        this.direction = direction;
        this.bakedNormals = bakedNormals;
        this.bakedColors = bakedColors;
        this.materialInfo = materialInfo;
    }

    public RawQuad(BakedQuad bakedQuad) {
        this(bakedQuad.direction(), bakedQuad.bakedNormals(), bakedQuad.bakedColors(), bakedQuad.materialInfo(),
                new Point(bakedQuad.position0(), bakedQuad.packedUV0()),
                new Point(bakedQuad.position1(), bakedQuad.packedUV1()),
                new Point(bakedQuad.position2(), bakedQuad.packedUV2()),
                new Point(bakedQuad.position3(), bakedQuad.packedUV3()));
    }

    public Point[] getPoints() {
        return points;
    }

    public RawQuad shrink16(float fromUp, float fromDown, float fromLeft, float fromRight) {
        return shrink(fromUp / 16, fromDown / 16, fromLeft / 16, fromRight / 16);
    }

    public RawQuad shrink(float fromUp, float fromDown, float fromLeft, float fromRight) {
        int flip;
        float uRange = URange();
        float vRange = VRange();
        if (direction == Direction.UP || direction == Direction.DOWN) {
            flip = direction == Direction.UP ? 1 : -1;
            points[0].offset(fromLeft, 0, fromUp * flip, fromLeft * uRange, fromUp * vRange);
            points[1].offset(fromLeft, 0, -fromDown * flip, fromLeft * uRange, -fromDown * vRange);
            points[2].offset(-fromRight, 0, -fromDown * flip, -fromRight * uRange, -fromDown * vRange);
            points[3].offset(-fromRight, 0, fromUp * flip, -fromRight * uRange, fromUp * vRange);
        } else if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            flip = direction == Direction.NORTH ? 1 : -1;
            points[0].offset(-fromLeft * flip, -fromUp, 0, fromLeft * uRange, fromUp * vRange);
            points[1].offset(-fromLeft * flip, fromDown, 0, fromLeft * uRange, -fromDown * vRange);
            points[2].offset(fromRight * flip, fromDown, 0, -fromRight * uRange, -fromDown * vRange);
            points[3].offset(fromRight * flip, -fromUp, 0, -fromRight * uRange, fromUp * vRange);
        } else {
            flip = direction == Direction.WEST ? 1 : -1;
            points[0].offset(0, -fromUp, fromLeft * flip, fromLeft * uRange, fromUp * vRange);
            points[1].offset(0, fromDown, fromLeft * flip, fromLeft * uRange, -fromDown * vRange);
            points[2].offset(0, fromDown, -fromRight * flip, -fromRight * uRange, -fromDown * vRange);
            points[3].offset(0, -fromUp, -fromRight * flip, -fromRight * uRange, fromUp * vRange);
        }
        return this;
    }

    @Deprecated
    public RawQuad move(float x, float y, float z) {
        return moveAbs(x, y, z);
    }

    @Deprecated
    public RawQuad move16(float x, float y, float z) {
        return moveAbs16(x, y, z);
    }

    public RawQuad moveAbs(float x, float y, float z) {
        for (Point p : points) {
            p.move(x, y, z);
        }
        return this;
    }

    public RawQuad moveAbs16(float x, float y, float z) {
        return moveAbs(x / 16, y / 16, z / 16);
    }

    /**
     * Observe from opposite outside.
     */
    public RawQuad moveRel(float left, float up, float front) {
        return moveRelWithDirection(left, up, front, direction);
    }

    /**
     * Observe from opposite outside.
     */
    public RawQuad moveRel16(float left, float up, float front) {
        return moveRel(left / 16, up / 16, front / 16);
    }

    /**
     * Observe from opposite outside.
     */
    public RawQuad moveRelWithDirection(float left, float up, float front, Direction dir) {
        return switch (dir) {
            case NORTH -> moveAbs(left, up, front);
            case SOUTH -> moveAbs(-left, up, -front);
            case WEST -> moveAbs(front, up, -left);
            case EAST -> moveAbs(-front, up, left);
            case UP -> moveAbs(left, -front, up);
            case DOWN -> moveAbs(-left, front, -up);
        };
    }

    /**
     * Observe from opposite outside.
     */
    public RawQuad moveRelWithDirection16(float left, float up, float front, Direction dir) {
        return moveRelWithDirection(left / 16, up / 16, front / 16, dir);
    }

    public BakedQuad bake() {
        return new BakedQuad(
                points[0].pos,
                points[1].pos,
                points[2].pos,
                points[3].pos,
                points[0].getUV(),
                points[1].getUV(),
                points[2].getUV(),
                points[3].getUV(),
                direction, materialInfo, bakedNormals, bakedColors
        );
    }

    public Point maxUV() {
        return points[2];
    }

    public Point mimUV() {
        return points[0];
    }

    public float URange() {
        return maxUV().uv.x - mimUV().uv.x;
    }

    public float VRange() {
        return maxUV().uv.y - mimUV().uv.y;
    }

    public Direction getDirection() {
        return direction;
    }

    public RawQuad copy() {
        return new RawQuad(direction, bakedNormals, bakedColors, materialInfo, points);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Point {
        Vector3f pos;
        Vector2f uv;

        public Point(Vector3fc pos, long uv) {
            this.pos = new Vector3f(pos);
            this.uv = new Vector2f(UVPair.unpackU(uv), UVPair.unpackV(uv));
        }

        public Point(Vector3f pos, Vector2f uv) {
            this.pos = pos;
            this.uv = uv;
        }

        public Point offset(float ax, float ay, float az, float au0, float av0) {
            pos.add(ax, ay, az);
            uv.add(au0, av0);
            return this;
        }

        public Point move(float x, float y, float z) {
            pos.add(x, y, z);
            return this;
        }

        public long getUV() {
            return UVPair.pack(uv.x, uv.y);
        }
    }
}
