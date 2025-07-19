package cn.ussshenzhou.t88.gui.notification;

import cn.ussshenzhou.t88.gui.HudManager;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class TSimpleNotification extends TLabel {
    private int life;

    public TSimpleNotification(Component text, int lifeSeconds, Severity severity) {
        super(text);
        this.life = lifeSeconds * 20;
        this.setHorizontalAlignment(HorizontalAlignment.CENTER);
        this.setBackground(severity.background);
        this.setBorder(new Border(severity.foreGround, 1));
    }

    public static TSimpleNotification fire(Component text, int lifeSeconds, Severity severity){
        var notify = new TSimpleNotification(text, lifeSeconds, severity);
        HudManager.addOrReplaceIfSameClassExist(notify);
        return notify;
    }

    @Override
    public void resizeAsHud(int screenWidth, int screenHeight) {
        var size = this.getPreferredSize().add(16, 6);
        this.setAbsBounds((screenWidth - size.x) / 2, (int) (screenHeight * 0.62), size);
        super.resizeAsHud(screenWidth, screenHeight);
    }

    @Override
    public void tickT() {
        if (life <= 0) {
            HudManager.remove(this);
        }
        life--;
        super.tickT();
    }

    public enum Severity {
        ERROR(0xffeb1010, 0x80800909),
        WARN(0xffe59400, 0x80805300),
        TIP(0xff42eb35, 0x8024801d),
        INFO(0xff11d5eb, 0x80097480);

        public final int foreGround, background;

        Severity(int foreGround, int background) {
            this.foreGround = foreGround;
            this.background = background;
        }
    }
}
