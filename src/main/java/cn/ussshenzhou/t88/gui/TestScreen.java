package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author USS_Shenzhou
 */
public class TestScreen extends TScreen {
    private TLabel title = new TLabel(Component.literal("T88 Test Screen"));

    private TLabel linesTest = new TLabel(Component.literal("§6Test1 \nTest223456\n...")) {
        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        }

        @Override
        public void renderTop(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            super.renderTop(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    };
    private TTimer tTimer = TTimer.newTimerCountDown(30);
    private TSlider slider = new TSlider("Test", 2, 5);
    private TSelectList<String> selectList = new TSelectList<>();
    private TEditBox editBox = new TEditBox();
    private TEditBox editBox2 = new TEditBox();
    private TScrollPanel scrollPanel = new TScrollPanel() {
        @Override
        public void layout() {
            editBox.setBounds(10, 10, 50, 20);
            editBox2.setBounds(10, 200, 50, 20);
            selectList.setBounds(10, 230, 50, 100);
            super.layout();
        }
    };

    public TestScreen() {
        super(Component.empty());
        this.add(title);
        title.setFontSize(20);

        linesTest.setBorder(new Border(0xff00ff00, 1));
        linesTest.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //this.add(linesTest);
        HudManager.add(linesTest);

        tTimer.setPrefix("ABC: ");
        tTimer.setBackground(0x80000000);
        tTimer.setForeground(0xff00ffff);
        tTimer.setShowMillis(true);
        tTimer.setShowFullFormat(true);
        //tTimer.setKeepDigitsLength(false);
        tTimer.start();
        this.add(tTimer);
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
            }
            tTimer.stop();
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
            }
            tTimer.start();
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
            }
            tTimer.pause();
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
            }
            tTimer.resume();
        });
        this.add(slider);
        selectList.addElement("ABC");
        selectList.addElement("DEF");
        selectList.addElement("1");
        selectList.addElement("2");
        selectList.addElement("3");
        selectList.addElement("4");
        selectList.addElement("5");

        //this.add(editBox);
        //this.add(editBox2);
        this.add(scrollPanel);
        scrollPanel.add(editBox);
        scrollPanel.add(editBox2);
        scrollPanel.add(selectList);
    }

    @Override
    public void layout() {
        title.setBounds(0, 0, title.getPreferredSize().x, title.getPreferredSize().y);
        linesTest.setBounds(50, 50, 80, 40);
        tTimer.setBounds(50, 100, 80, 20);
        LayoutHelper.BBottomOfA(slider, 10, tTimer);
        scrollPanel.setBounds(210,20,200,100);
        super.layout();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose(boolean isFinal) {
        super.onClose(isFinal);
        HudManager.remove(linesTest);
    }
}
