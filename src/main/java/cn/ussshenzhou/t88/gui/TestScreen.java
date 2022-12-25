package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.ImageFit;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.network.chat.TextComponent;

/**
 * @author USS_Shenzhou
 */
public class TestScreen extends TScreen {
    private TLabel title = new TLabel(new TextComponent("T88 Test Screen"));

    private TImage imageTest = new TImage("minecraft", "textures/block/grass_block_side.png");

    public TestScreen() {
        super(new TextComponent(""));
        this.add(title);
        title.setFontSize(20);
        this.add(imageTest);
        imageTest.setBorder(new Border(0xff00ff00, 1));
        imageTest.setImageFit(ImageFit.FIT);
    }

    @Override
    public void layout() {
        title.setBounds(0, 0, title.getPreferredSize().x, title.getPreferredSize().y);
        imageTest.setBounds(50,50,200,100);
        super.layout();
    }
}
