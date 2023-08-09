package cn.ussshenzhou.t88.gui.combine;

import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public abstract class TTitledComponent<T extends TWidget> extends TPanel {
    protected final TLabel title = new TLabel();
    protected final T widget;
    int gap = 0;
    int labelHeight = 12;

    public TTitledComponent(Component titleText, T component) {
        widget = component;
        title.setText(titleText);
        title.setHorizontalAlignment(HorizontalAlignment.LEFT);
        title.setBounds(0,0,0,labelHeight);
        title.setForeground(0xff9e9e9e);
        this.add(title);
        this.add(widget);
    }

    @Override
    public void layout() {
        defaultLayout();
        super.layout();
    }

    public void defaultLayout() {
        title.setBounds(0, 0, width, labelHeight);
        LayoutHelper.BBottomOfA(widget, gap, title, width, height - title.getHeight() - gap);
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(Math.max(title.getWidth(), widget.getSize().x),
                title.getHeight() + widget.getSize().y
        );
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public TLabel getTitle() {
        return title;
    }

    public T getComponent() {
        return widget;
    }

    public int getLabelHeight() {
        return labelHeight;
    }

    public void setLabelHeight(int labelHeight) {
        this.labelHeight = labelHeight;
    }
}
