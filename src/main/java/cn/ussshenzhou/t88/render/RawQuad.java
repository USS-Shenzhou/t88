package cn.ussshenzhou.t88.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@OnlyIn(Dist.CLIENT)
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

    Point[] points = new Point[4];
    private final int tintIndex;
    private Direction direction;
    private final TextureAtlasSprite sprite;
    private boolean shade;
    static VertexFormat format = DefaultVertexFormat.BLOCK;
    static ImmutableList<VertexFormatElement> elements = format.getElements();

    public RawQuad(int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade, Point... points) {
        this.points = points;
        this.tintIndex = tintIndex;
        this.direction = direction;
        this.sprite = sprite;
        this.shade = shade;
    }

    public RawQuad(BakedQuad bakedQuad) {
        for (int v = 0; v < 4; v++) {
            float[] buffer = new float[0];
            for (int e = 0; e < elements.size(); e++) {
                float[] f = new float[elements.get(e).getElementCount()];
                unpack(bakedQuad.getVertices(), f, format, v, e);
                buffer = ArrayUtils.addAll(buffer, f);
            }
            points[v] = new Point(buffer);
        }
        this.tintIndex = bakedQuad.getTintIndex();
        this.direction = bakedQuad.getDirection();
        this.sprite = bakedQuad.getSprite();
        this.shade = bakedQuad.isShade();

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

    public RawQuad move(float x, float y, float z) {
        for (Point p : points) {
            p.move(x, y, z);
        }
        return this;
    }

    public RawQuad move16(float x, float y, float z) {
        x /= 16;
        y /= 16;
        z /= 16;
        for (Point p : points) {
            p.move(x, y, z);
        }
        return this;
    }

    public BakedQuad bake() {
        int[] packed = new int[format.getIntegerSize() * 4];
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < elements.size(); e++) {
                //LightUtil.pack(unpackedData[v][e], packed, DefaultVertexFormat.BLOCK, v, e);
                pack(points[v].get(e), packed, format, v, e);
            }
        }
        return new BakedQuad(packed, tintIndex, direction, sprite, shade);
    }

    /*private boolean isXOY() {
        return points[0].z == points[1].z && points[0].z == points[2].z && points[0].z == points[3].z;
    }

    private boolean isXOZ() {
        return points[0].y == points[1].y && points[0].y == points[2].y && points[0].y == points[3].y;
    }*/

    public Point maxUV() {
        return points[2];
    }

    public float[] minXYZ() {
        return new float[]{
                Math.min(points[0].get(0)[0], points[2].get(0)[0]),
                Math.min(points[0].get(0)[1], points[2].get(0)[1]),
                Math.min(points[0].get(0)[2], points[2].get(0)[2]),
        };
    }

    public Point mimUV() {
        return points[0];
    }

    public float[] maxXYZ() {
        return new float[]{
                Math.max(points[0].get(0)[0], points[2].get(0)[0]),
                Math.max(points[0].get(0)[1], points[2].get(0)[1]),
                Math.max(points[0].get(0)[2], points[2].get(0)[2]),
        };
    }

    public float URange() {
        return maxUV().u0 - mimUV().u0;
    }

    public float VRange() {
        return maxUV().v0 - mimUV().v0;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setShade(boolean shade) {
        this.shade = shade;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isShade() {
        return shade;
    }

    public RawQuad copy() {
        return new RawQuad(tintIndex, direction, sprite, shade, points);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Point {
        float x, y, z;
        float r, g, b, a;
        float u0, v0;
        float u2, v2;
        float normalX, normalY, normalZ;
        float padding;

        public Point(float x, float y, float z, float r, float g, float b, float a, float u0, float v0, float u2, float v2, float normalX, float normalY, float normalZ, float padding) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.u0 = u0;
            this.v0 = v0;
            this.u2 = u2;
            this.v2 = v2;
            this.normalX = normalX;
            this.normalY = normalY;
            this.normalZ = normalZ;
            this.padding = padding;
        }

        public Point(float[] f) {
            this(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], f[9], f[10], f[11], f[12], f[13], f[14]);
        }

        public Point copy() {
            return new Point(x, y, z, r, g, b, a, u0, v0, u2, v2, normalX, normalY, normalZ, padding);
        }

        public Point offset(float ax, float ay, float az, float au0, float av0) {
            this.x += ax;
            this.y += ay;
            this.z += az;
            this.u0 += au0;
            this.v0 += av0;
            return this;
        }

        public Point move(float x, float y, float z) {
            this.x += x;
            this.y += y;
            this.z += z;
            return this;
        }

        public Point setNormal(float x, float y, float z) {
            this.normalX = x;
            this.normalY = y;
            this.normalZ = z;
            return this;
        }

        public float[] get(int index) {
            return switch (index) {
                case 0 -> new float[]{x, y, z};
                case 1 -> new float[]{r, g, b, a};
                case 2 -> new float[]{u0, v0};
                case 3 -> new float[]{u2, v2};
                case 4 -> new float[]{normalX, normalY, normalZ};
                default -> new float[]{padding};
            };
        }
    }

    /**
     * Copied from net.neoforged.neoforge.client.model.pipeline.LightUtil during updating from 1.18.2 to 1.19.4 under LGPL-2.1
     */
    public static void unpack(int[] from, float[] to, VertexFormat formatFrom, int v, int e) {
        int length = 4 < to.length ? 4 : to.length;
        VertexFormatElement element = formatFrom.getElements().get(e);
        int vertexStart = v * formatFrom.getVertexSize() + formatFrom.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.Type type = element.getType();
        VertexFormatElement.Usage usage = element.getUsage();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < length; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = from[index];
                bits = bits >>> (offset * 8);
                if ((pos + size - 1) / 4 != index) {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= mask;
                if (type == VertexFormatElement.Type.FLOAT) {
                    to[i] = Float.intBitsToFloat(bits);
                } else if (type == VertexFormatElement.Type.UBYTE || type == VertexFormatElement.Type.USHORT) {
                    to[i] = (float) bits / mask;
                } else if (type == VertexFormatElement.Type.UINT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / 0xFFFFFFFFL);
                } else if (type == VertexFormatElement.Type.BYTE) {
                    to[i] = ((float) (byte) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.Type.SHORT) {
                    to[i] = ((float) (short) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.Type.INT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / (0xFFFFFFFFL >> 1));
                }
            } else {
                to[i] = (i == 3 && usage == VertexFormatElement.Usage.POSITION) ? 1 : 0;
            }
        }
    }

    /**
     * Copied from net.neoforged.neoforge.client.model.pipeline.LightUtil during updating from 1.18.2 to 1.19.4 under LGPL-2.1
     */
    public static void pack(float[] from, int[] to, VertexFormat formatTo, int v, int e) {
        VertexFormatElement element = formatTo.getElements().get(e);
        int vertexStart = v * formatTo.getVertexSize() + formatTo.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.Type type = element.getType();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < 4; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = 0;
                float f = i < from.length ? from[i] : 0;
                if (type == VertexFormatElement.Type.FLOAT) {
                    bits = Float.floatToRawIntBits(f);
                } else if (
                        type == VertexFormatElement.Type.UBYTE ||
                                type == VertexFormatElement.Type.USHORT ||
                                type == VertexFormatElement.Type.UINT
                ) {
                    bits = Math.round(f * mask);
                } else {
                    bits = Math.round(f * (mask >> 1));
                }
                to[index] &= ~(mask << (offset * 8));
                to[index] |= (((bits & mask) << (offset * 8)));
                // ForgeTODO handle overflow into to[index + 1]
            }
        }
    }
}
