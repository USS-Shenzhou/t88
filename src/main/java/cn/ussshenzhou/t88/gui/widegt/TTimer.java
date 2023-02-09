package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.event.TimerCountdownReachEvent;
import cn.ussshenzhou.t88.mixin.TextComponentAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author USS_Shenzhou
 */
public class TTimer extends TLabel {
    private long startMs = 0;
    private long pausedMs = 0;
    private boolean countdown = false;
    private boolean updateGui = true;
    private String prefix;
    private boolean showFullFormat = false;
    private boolean keepDigitsLength = true;
    private boolean showMillis = false;
    private int countDownSec = 0;
    private TimeCategory showUpto = TimeCategory.DAY;
    String finalString = "";
    long time;

    public TTimer() {
        super();
        prefix = "";
        this.text = new TextComponent("");
    }

    public static TTimer newTimerAndStart() {
        TTimer t = new TTimer();
        t.start();
        return t;
    }

    public static TTimer newTimerCountDown(int secs) {
        TTimer t = new TTimer();
        t.countDownSec = secs;
        t.countdown = true;
        return t;
    }

    public static TTimer newTimerCountDownAndStart(int secs) {
        TTimer t = newTimerCountDown(secs);
        t.start();
        return t;
    }

    @Override
    public void tickT() {
        super.tickT();
        if (!updateGui) {
            return;
        }
        updateText();
    }

    private void updateText() {
        int ms = (int) (time % 1000);
        int s = (int) (time / 1000 % 60);
        int min = (int) (time / 1000 / 60 % 60);
        int hour = (int) (time / 1000 / 60 / 60 % 60);
        int day = (int) (time / 1000 / 60 / 60 / 24);
        switch (showUpto) {
            case SEC -> finalString = prefix
                    + getRequiredFormat(s)
                    + getRequiredFormatMs(ms);
            case MIN -> finalString = prefix
                    + getRequiredFormat1(min)
                    + getRequiredFormat(s)
                    + getRequiredFormatMs(ms);
            case HOUR -> finalString = prefix
                    + getRequiredFormat1(hour)
                    + getRequiredFormat1(min)
                    + getRequiredFormat(s)
                    + getRequiredFormatMs(ms);
            default -> finalString = prefix
                    + getRequiredFormat1(day)
                    + getRequiredFormat1(hour)
                    + getRequiredFormat1(min)
                    + getRequiredFormat(s)
                    + getRequiredFormatMs(ms);
        }
        this.parseTextLines();
        ((TextComponentAccessor) text).setText(finalString);
    }

    private String getRequiredFormat1(int i) {
        String s = getRequiredFormat(i);
        if (s.length() > 0) {
            return s + ":";
        }
        return s;
    }

    private String getRequiredFormat(int i) {
        if (i == 0) {
            if (showFullFormat) {
                if (keepDigitsLength) {
                    return "00";
                } else {
                    return "0";
                }
            } else {
                return "";
            }
        } else {
            return keepDigitsLength ? String.format("%02d", i) : String.valueOf(i);
        }
    }

    private String getRequiredFormatMs(int i) {
        if (!showMillis) {
            return "";
        }
        if (i == 0) {
            if (keepDigitsLength) {
                return ".000";
            } else {
                return ".0";
            }
        } else {
            return keepDigitsLength ? String.format(".%03d", i) : "." + i;
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (countdown) {
            if (startMs == 0 && pausedMs == 0) {
                time = countDownSec * 1000L;
            } else {
                time = startMs + pausedMs - System.currentTimeMillis();
                if (time <= 0) {
                    time = 0;
                    updateText();
                    updateGui = false;
                    MinecraftForge.EVENT_BUS.post(new TimerCountdownReachEvent(this));
                }
            }
        } else {
            time = System.currentTimeMillis() - startMs - pausedMs;
        }
        int ms = (int) (time % 1000);
        try {
            ((TextComponentAccessor) text).setText(finalString
                    .substring(0, finalString.indexOf("."))
                    + getRequiredFormatMs(ms)
            );
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public void start() {
        updateGui = true;
        if (countdown) {
            startMs = System.currentTimeMillis() + countDownSec * 1000L;
        } else {
            startMs = System.currentTimeMillis();
        }
    }

    private long pauseA = 0;

    public void pause() {
        pauseA = System.currentTimeMillis();
        updateGui = false;
    }

    public void resume() {
        pausedMs = System.currentTimeMillis() - pauseA;
        updateGui = true;
    }

    public void stop() {
        updateGui = false;
    }

    public long getStartMs() {
        return startMs;
    }

    public void setStartMs(long startMs) {
        this.startMs = startMs;
    }

    public long getPausedMs() {
        return pausedMs;
    }

    public void setPausedMs(long pausedMs) {
        this.pausedMs = pausedMs;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isShowFullFormat() {
        return showFullFormat;
    }

    public void setShowFullFormat(boolean showFullFormat) {
        this.showFullFormat = showFullFormat;
    }

    public boolean isKeepDigitsLength() {
        return keepDigitsLength;
    }

    public void setKeepDigitsLength(boolean keepDigitsLength) {
        this.keepDigitsLength = keepDigitsLength;
    }

    public boolean isShowMillis() {
        return showMillis;
    }

    public void setShowMillis(boolean showMillis) {
        this.showMillis = showMillis;
    }

    public boolean isUpdateGui() {
        return updateGui;
    }

    public void setUpdateGui(boolean updateGui) {
        this.updateGui = updateGui;
    }

    public TimeCategory getShowUpto() {
        return showUpto;
    }

    public void setShowUpto(TimeCategory showUpto) {
        this.showUpto = showUpto;
    }

    public boolean isCountdown() {
        return countdown;
    }

    public void setCountdown(boolean countdown) {
        this.countdown = countdown;
    }

    public int getCountDownSec() {
        return countDownSec;
    }

    public void setCountDownSec(int countDownSec) {
        this.countDownSec = countDownSec;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public enum TimeCategory {
        SEC,
        MIN,
        HOUR,
        DAY
    }
}
