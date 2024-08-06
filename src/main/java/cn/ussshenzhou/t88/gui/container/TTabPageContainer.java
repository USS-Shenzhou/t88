package cn.ussshenzhou.t88.gui.container;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import com.google.common.collect.ImmutableList;
import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author USS_Shenzhou
 */
public class TTabPageContainer extends TPanel {
    protected TabContainerVertical container = new TabContainerVertical();
    private Tab selectedTab = null;

    public TTabPageContainer() {
        super();
        this.add(container);
    }

    @Override
    public void tickT() {
        if (getSelectedTab() == null && !container.tabs.isEmpty()) {
            selectTab(0);
        }
        super.tickT();
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
        tab.setSelect(true);
        selectedTab = tab;
    }

    public void selectTab(int index) {
        selectTab(container.tabs.get(index));
    }

    public void removeTab(Tab tab) {
        var next = container.tabs.indexOf(tab);
        this.remove(tab.content);
        container.remove(tab);
        container.tabs.remove(tab);
        if (selectedTab == tab) {
            selectedTab = null;
        }
        this.layout();
        if (container.tabs.isEmpty()) {
            return;
        }
        if (next == container.tabs.size()) {
            next--;
        }
        this.selectTab(next);
    }

    public @Nullable Tab getSelectedTab() {
        return selectedTab;
    }

    public ImmutableList<Tab> getTabs() {
        return ImmutableList.copyOf(container.tabs);
    }

    public class TabContainerVertical extends TVerticalScrollContainer {
        protected LinkedList<Tab> tabs = new LinkedList<>();
        int row = 0;

        public TabContainerVertical() {
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

        protected void rearrangeTabs() {
            ArrayList<Tab> tmp = new ArrayList<>();
            Iterator<Tab> iterator = tabs.iterator();
            while (iterator.hasNext()) {
                Tab tab = iterator.next();
                if (tab.keepFinal) {
                    iterator.remove();
                    tmp.add(tab);
                }
            }
            tabs.addAll(tmp);
        }

        @Override
        public void layout() {
            rearrangeTabs();
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

        protected boolean closeable = true;
        protected boolean keepFinal = false;

        public Tab(Component c, TWidget content) {
            super(c);
            this.button.setOnPress(pButton -> {
                selectTab(this);
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

        public Tab setCloseable(boolean closeable) {
            this.closeable = closeable;
            close.setVisibleT(closeable);
            close.getButton().setVisibleT(closeable);
            return this;
        }

        public Tab setKeepFinal(boolean keepFinal) {
            this.keepFinal = keepFinal;
            getParentInstanceOf(TTabPageContainer.class).layout();
            return this;
        }

        public TLabelButton getCloseButton() {
            return close;
        }

        public TWidget getContent() {
            return content;
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
