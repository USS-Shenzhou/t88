package cn.ussshenzhou.t88.config;

/**
 * @author USS_Shenzhou
 */
public interface TConfig {
    default String getChildDirName() {
        return "";
    }

    /**
     * WARNING: Caller thread or time is not guaranteed by T88, which depends on your calling {@link ConfigHelper#loadConfig(TConfig)}.
     */
    default void onLoad() {}
}
