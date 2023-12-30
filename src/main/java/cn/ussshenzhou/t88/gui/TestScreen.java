package cn.ussshenzhou.t88.gui;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.advanced.TSuggestedEditBox;
import cn.ussshenzhou.t88.gui.container.TScrollContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

/**
 * @author USS_Shenzhou
 */
public class TestScreen extends TScreen {
    private TLabel title = new TLabel(Component.literal("T88 Test Screen"));

    private TLabel linesTest = new TLabel(Component.literal("§6Test1 \nTest223456\n...")) {
        @Override
        public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
            //super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public void renderTop(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
            super.renderTop(guigraphics, pMouseX, pMouseY, pPartialTick);
        }
    };
    private TTimer tTimer = TTimer.newTimerCountDown(30);
    private TSlider slider = new TSlider("Test", 2, 5);
    private TSelectList<String> selectList = new TSelectList<>();
    private TEditBox editBox = new TEditBox();
    private TSuggestedEditBox editBox2 = new TSuggestedEditBox(d -> {
        d.register(Commands.literal("ABCD"));
        d.register(Commands.literal("1df"));
        d.register(Commands.literal("2d"));
        d.register(Commands.literal("345"));
        d.register(Commands.literal("6gefrthy"));
        d.register(Commands.literal("7v"));
        d.register(Commands.literal("41gtg"));
    });
    private TScrollContainer scrollPanel = new TScrollContainer() {
        @Override
        public void layout() {
            editBox.setBounds(10, 10, 50, 20);
            selectList.setBounds(10, 230, 50, 100);
            editBox2.setBounds(10, 350, 50, 20);
            super.layout();
        }
    };
    private TPanel cover = new TPanel();
    private TLabelButton labelButton = new TLabelButton(Component.literal("Label Button"), pButton -> {
    });
    private TItem item = new TItem(new ItemStack(Items.GRASS_BLOCK), null, 32);

    public TestScreen() {
        super(Component.empty());
        this.add(title);
        title.setFontSize(20);
        title.setAutoScroll(false);
        linesTest.setBorder(new Border(0xff00ff00, -4));
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
        selectList.addElement("3456789012");
        selectList.addElement("4");
        selectList.addElement("5");

        //this.add(editBox);
        //this.add(editBox2);
        this.add(scrollPanel);
        scrollPanel.add(editBox);
        scrollPanel.add(editBox2);
        scrollPanel.add(selectList);
        //this.add(cover);
        cover.setBorder(new Border(0xffff0000, 1));
        cover.setBackground(0x88aaaaaa);
        //editBox2.setTooltip(Tooltip.create(Component.literal("ABCDEFG")));
        this.add(labelButton);
        this.add(item);
        item.setBorder(new Border(0xffffffff, 1));
    }

    @Override
    public void layout() {
        title.setBounds(0, 0, title.getPreferredSize().x, title.getPreferredSize().y);
        linesTest.setBounds(50, 50, 80, 40);
        cover.setBounds(25, 25, 260, 180);
        tTimer.setBounds(50, 100, 80, 20);
        LayoutHelper.BBottomOfA(slider, 10, tTimer);
        scrollPanel.setBounds(210, 20, 200, 100);
        labelButton.setBounds(50, 210, 100, 30);
        LayoutHelper.BRightOfA(item, 4, tTimer, item.getPreferredSize());
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

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        //graphics.drawString(Minecraft.getInstance().font, "ABCDE", 30, 30, 0xff000000);
        Minecraft.getInstance().font.drawInBatch("ABCDE", 30, 30, 0xffff0000, false, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
