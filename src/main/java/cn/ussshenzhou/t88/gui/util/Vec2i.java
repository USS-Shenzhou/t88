package cn.ussshenzhou.t88.gui.util;

/**
 * @author USS_Shenzhou
 */
public class Vec2i {
    public int x, y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i add(Vec2i vec2i) {
        x += vec2i.x;
        y += vec2i.y;
        return this;
    }

    public Vec2i add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec2i copy() {
        return new Vec2i(x, y);
    }
}
