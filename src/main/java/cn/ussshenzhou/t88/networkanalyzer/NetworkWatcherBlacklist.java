package cn.ussshenzhou.t88.networkanalyzer;

import cn.ussshenzhou.t88.config.TConfig;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcherBlacklist implements TConfig {
    public ArrayList<Identifier> blacklist = new ArrayList<>() {{
        add(Identifier.fromNamespaceAndPath("example_namespace", "example_path"));
    }};
}
