package cn.ussshenzhou.t88.networkanalyzer;

import cn.ussshenzhou.t88.config.TConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcherBlacklist implements TConfig {
    public ArrayList<ResourceLocation> blacklist = new ArrayList<>() {{
        add(ResourceLocation.fromNamespaceAndPath("example_namespace", "example_path"));
    }};
}
