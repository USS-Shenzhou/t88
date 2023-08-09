package cn.ussshenzhou.t88.gui.container;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TTabPageContainer extends TPanel {


    public static class TTab extends TLabelButton {
        protected final TLabelButton x;

        public TTab(Component c) {
            super(c, pButton -> {

            });
            x = new TLabelButton(Component.literal("×"), pButton -> {
                //TODO
            });
            x.setBorder(null);
            this.add(x);
        }

        @Override
        public void layout() {
            x.setBounds(width - 12, 0, 12, height);
            super.layout();
        }

        @Override
        protected void renderText(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
            guigraphics.pose().pushPose();
            guigraphics.pose().translate(-12, 0, 0);
            super.renderText(guigraphics, pMouseX, pMouseY, pPartialTick);
            guigraphics.pose().popPose();
        }

        @Override
        public Vector2i getPreferredSize() {
            return super.getPreferredSize().add(12, 0);
        }
    }
}
