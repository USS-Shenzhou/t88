package cn.ussshenzhou.t88.util;

import cn.ussshenzhou.t88.config.TConfig;
import cn.ussshenzhou.t88.gui.util.ITranslatable;

/**
 * @author USS_Shenzhou
 */
public class T88Config implements TConfig {

    public boolean replaceStyle = true;
    public NetworkUnit networkUnit = NetworkUnit.BYTE;


    public enum NetworkUnit implements ITranslatable {
        BYTE("gui.t88.net.options.byte"),
        BIT("gui.t88.net.options.bit");

        private final String translateKey;

        NetworkUnit(String translateKey) {
            this.translateKey = translateKey;
        }

        @Override
        public String translateKey() {
            return translateKey;
        }
    }
}
