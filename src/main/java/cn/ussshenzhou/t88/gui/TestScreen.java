package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import cn.ussshenzhou.t88.gui.widegt.TTimer;
import net.minecraft.network.chat.TextComponent;

/**
 * @author USS_Shenzhou
 */
public class TestScreen extends TScreen {
    private TLabel title = new TLabel(new TextComponent("T88 Test Screen"));

    private TLabel linesTest = new TLabel(new TextComponent("§6Test1 \nTest223456\n..."));
    private TTimer tTimer = TTimer.newTimerCountDownAndStart(20);

    public TestScreen() {
        super(new TextComponent(""));
        this.add(title);
        title.setFontSize(20);

        linesTest.setBorder(new Border(0xff00ff00, 1));
        linesTest.setHorizontalAlignment(HorizontalAlignment.CENTER);
        this.add(linesTest);

        tTimer.setPrefix("ABC: ");
        tTimer.setBackground(0xff000000);
        tTimer.setForeground(0xff00ffff);
        //tTimer.setShowMillis(true);
        tTimer.setShowFullFormat(true);
        //tTimer.setKeepDigitsLength(false);
        tTimer.setShowUpto(TTimer.TimeCategory.MIN);
        this.add(tTimer);
    }

    @Override
    public void layout() {
        title.setBounds(0, 0, title.getPreferredSize().x, title.getPreferredSize().y);
        linesTest.setBounds(50, 50, 80, 40);
        tTimer.setBounds(50, 100, 80, 20);
        super.layout();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
