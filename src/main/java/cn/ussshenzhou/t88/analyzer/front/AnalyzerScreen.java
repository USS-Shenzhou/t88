package cn.ussshenzhou.t88.analyzer.front;

import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class AnalyzerScreen extends TScreen {
    protected TTabPageContainer container = new TTabPageContainer();

    public AnalyzerScreen() {
        super(Component.literal("T88 Analyzer"));
        this.add(container);
        container.newTab(Component.literal("TEST1"),new TLabel(Component.literal("""
                QWRGRGQETHBWRT arh  trh wrstghge
                defgethg
                bbrnbverg""")));
        container.newTab(Component.literal("TEST     2"),new TLabel(Component.literal("""
                sdagverht 654511
                894fer1g
                ga4rth5ndf fds""")));
        container.newTab(Component.literal("TEST     3 dvbhhfvbiwrefvbhnfbwnigbnwgbnjgnbjwnigbnjignbnigjbnjigbn 3"),new TLabel(Component.literal("""
                ;l,vr.tlngb.eplw,oo54
                g425bhrtgb
                350t0t,..lq.34ferfg fwqgtbwerytnerybnrtr""")));
    }

    @Override
    public void layout() {
        container.setBounds(0, 0, width, height);
        super.layout();
    }
}
