package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.network.chat.TextComponent;

/**
 * @author USS_Shenzhou
 */
public class TestScreen extends TScreen {
    private TLabel title = new TLabel(new TextComponent("T88 Test Screen"));

    private TLabel linesTest = new TLabel(new TextComponent("§6Test1 \nTest2"));

    public TestScreen() {
        super(new TextComponent(""));
        this.add(title);
        title.setFontSize(20);

        linesTest.setBorder(new Border(0xff00ff00,1));
        this.add(linesTest);
    }

    @Override
    public void layout() {
        title.setBounds(0, 0, title.getPreferredSize().x, title.getPreferredSize().y);
        linesTest.setBounds(50,50,50,100);
        super.layout();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /*@Override
    public void onClose(boolean isFinal) {
        super.onClose(isFinal);
        Minecraft.getInstance().setScreen(null);
    }*/
}
