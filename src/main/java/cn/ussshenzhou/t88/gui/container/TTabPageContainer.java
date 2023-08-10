package cn.ussshenzhou.t88.gui.container;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TButton;
import cn.ussshenzhou.t88.gui.widegt.TComponent;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;

/**
 * @author USS_Shenzhou
 */
public class TTabPageContainer extends TPanel {
    protected TabContainer container = new TabContainer();

    public TTabPageContainer() {
        super();
        this.add(container);
    }

    @Override
    public void layout() {
        container.setBounds(0, 0, width, Math.min(height / 2, container.getPreferredSize().y));
        container.tabs.forEach(tab -> LayoutHelper.BBottomOfA(tab.content, 1, container, width, height - 1 - container.getHeight()));
        super.layout();
    }

    public Tab newTab(Component c, TWidget content) {
        var tab = container.newTab(c, content);
        content.setVisibleT(false);
        this.add(content);
        this.layout();
        return tab;
    }

    public void selectTab(Tab tab) {
        container.tabs.forEach(t -> {
            t.content.setVisibleT(false);
            t.setSelect(false);
        });
        tab.content.setVisibleT(true);
    }

    public void removeTab(Tab tab) {
        this.remove(tab.content);
        container.remove(tab);
        container.tabs.remove(tab);
        this.layout();
    }

    public class TabContainer extends TScrollContainer {
        protected LinkedList<Tab> tabs = new LinkedList<>();
        int row = 0;

        public TabContainer() {
            super();
            this.bottomMargin = 0;
            this.setBorder(new Border(0x7f_ffffff, 1));
        }

        private Tab newTab(Component c, TWidget content) {
            var tab = new Tab(c, content);
            this.add(tab);
            tabs.add(tab);
            return tab;
        }

        @Override
        public void layout() {
            int prevRow = row;
            row = 0;
            boolean newRow = false;
            for (int i = 0; i < tabs.size(); i++) {
                var tab = tabs.get(i);
                var size = tab.getPreferredSize();
                if (i == 0) {
                    tab.setBounds(0, 0, Math.min(size.x, getUsableWidth()), size.y);
                    continue;
                }
                if (newRow) {
                    tab.setBounds(0, row * size.y, Math.min(size.x, getUsableWidth()), size.y);
                    newRow = false;
                    continue;
                } else {
                    LayoutHelper.BRightOfA(tab, 0, tabs.get(i - 1), size);
                    if (tab.getXT() + tab.getWidth() > this.getXT() + this.getUsableWidth()) {
                        newRow = true;
                        row++;
                        i--;
                    }
                }
            }
            if (row != prevRow && this.getParent() != null) {
                this.getParent().layout();
            }
            super.layout();
        }

        @Override
        public Vector2i getPreferredSize() {
            return new Vector2i(0, (row + 1) * Tab.HEIGHT);
        }
    }

    public class Tab extends TLabelButton {
        protected final TLabelButton close;
        protected TWidget content;
        protected static final int HEIGHT = 14;

        public Tab(Component c, TWidget content) {
            super(c);
            this.button.setOnPress(pButton -> {
                selectTab(this);
                setSelect(true);
            });
            close = new TLabelButton(Component.literal("×"), pButton -> {
                removeTab(this);
            });
            close.setBorder(null);
            close.setForeground(0xff_bbbbbb);
            this.add(close);
            this.content = content;
        }

        public void setSelect(boolean select) {
            this.setBorder(new Border(select ? 0xff_3c91ff : 0xff_ffffff, -1));
        }

        @Override
        public void tickT() {
            if (close.getButton().isHoveredOrFocused()) {
                close.setBackground(close.getHoverBackGround());
                this.setBackground(normalBackGround);
            } else {
                close.setBackground(close.getNormalBackGround());
                if (this.button.isHoveredOrFocused()) {
                    this.setBackground(hoverBackGround);
                } else {
                    this.setBackground(normalBackGround);
                }
            }
            tickChildren();
        }

        @Override
        public void layout() {
            close.setBounds(width - 12, 0, 12, HEIGHT);
            super.layout();
        }

        @Override
        public Vector2i getPreferredSize() {
            return new Vector2i(super.getPreferredSize().x + 2 * HEIGHT + 2, HEIGHT);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return close.mouseClicked(pMouseX, pMouseY, pButton) || super.mouseClicked(pMouseX, pMouseY, pButton);
        }
    }
}
