package cn.ussshenzhou.t88.gui.util;

import cn.ussshenzhou.t88.gui.widegt.TWidget;
import org.joml.Vector2i;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public class LayoutHelper {


    public static void BRightOfA(TWidget b, int gap, TWidget a, int width, int height) {
        b.setAbsBounds(a.getXT() + a.getSize().x + gap, a.getYT(), width, height);
    }

    public static void BRightOfA(TWidget b, int gap, TWidget a, Vector2i size) {
        b.setAbsBounds(a.getXT() + a.getSize().x + gap, a.getYT(), size);
    }

    public static void BRightOfA(TWidget b, int gap, TWidget a) {
        b.setAbsBounds(a.getXT() + a.getSize().x + gap, a.getYT(), a.getSize());
    }

    public static void BLeftOfA(TWidget b, int gap, TWidget a, int width, int height) {
        b.setAbsBounds(a.getXT() - gap - width, a.getYT(), width, height);
    }

    public static void BLeftOfA(TWidget b, int gap, TWidget a, Vector2i size) {
        b.setAbsBounds(a.getXT() - gap - size.x, a.getYT(), size);
    }

    public static void BLeftOfA(TWidget b, int gap, TWidget a) {
        b.setAbsBounds(a.getXT() - gap - a.getSize().x, a.getYT(), a.getSize());
    }

    public static void BTopOfA(TWidget b, int gap, TWidget a, int width, int height) {
        b.setAbsBounds(a.getXT(), a.getYT() - height - gap, width, height);
    }

    public static void BTopOfA(TWidget b, int gap, TWidget a, Vector2i size) {
        b.setAbsBounds(a.getXT(), a.getYT() - size.x - gap, size);
    }

    public static void BTopOfA(TWidget b, int gap, TWidget a) {
        b.setAbsBounds(a.getXT(), a.getYT() - a.getSize().y - gap, a.getSize());
    }

    public static void BBottomOfA(TWidget b, int gap, TWidget a, int width, int height) {
        b.setAbsBounds(a.getXT(), a.getYT() + a.getSize().y + gap, width, height);
    }

    public static void BBottomOfA(TWidget b, int gap, TWidget a, Vector2i size) {
        b.setAbsBounds(a.getXT(), a.getYT() + a.getSize().y + gap, size);
    }

    public static void BBottomOfA(TWidget b, int gap, TWidget a) {
        b.setAbsBounds(a.getXT(), a.getYT() + a.getSize().y + gap, a.getSize());
    }

    public static void BSameAsA(TWidget b, TWidget a) {
        b.setAbsBounds(a.getXT(), a.getYT(), a.getSize());
    }

    public static TWidget moveUp(TWidget widget, int distance) {
        widget.setAbsBounds(widget.getXT(), widget.getYT() - distance, widget.getSize());
        return widget;
    }

    public static TWidget moveDown(TWidget widget, int distance) {
        widget.setAbsBounds(widget.getXT(), widget.getYT() + distance, widget.getSize());
        return widget;
    }

    public static TWidget moveLeft(TWidget widget, int distance) {
        widget.setAbsBounds(widget.getXT() - distance, widget.getYT(), widget.getSize());
        return widget;
    }

    public static TWidget moveRight(TWidget widget, int distance) {
        widget.setAbsBounds(widget.getXT() + distance, widget.getYT(), widget.getSize());
        return widget;
    }
}
