package cn.ussshenzhou.t88.analyzer.front;

import cn.ussshenzhou.t88.analyzer.back.T88AnalyzerClient;
import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.widegt.TDotChart;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class AnalyzerScreen extends TScreen {
    protected TTabPageContainer container = new TTabPageContainer();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public AnalyzerScreen() {
        super(Component.literal("T88 Analyzer"));
        this.add(container);
        T88AnalyzerClient.RECORDERS.forEach((s, recorder) ->
                container.newTab(Component.literal(s), new TDotChart(recorder.getRecords(), Component.literal("x"), Component.literal(s)))
        );
    }

    @Override
    public void layout() {
        container.setBounds(0, 0, width, height);
        super.layout();
    }
}
