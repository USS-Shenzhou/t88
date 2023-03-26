package cn.ussshenzhou.t88.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class MultiInstanceConfigHelper {
    private static final File CONFIG_DIR = FMLPaths.CONFIGDIR.relative().toFile();
    private static final HashMap<Class<? extends TMultiInstanceConfig>, HashMap<String, TMultiInstanceConfig>> CACHE = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static void checkDir(File dir) {
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
    }

    private static File checkChildDir(TMultiInstanceConfig config) {
        return checkChildDir(config.getClass(), config.getChildDirName());
    }

    private static File checkChildDir(Class<? extends TMultiInstanceConfig> clazz, String childDirName) {
        checkDir(CONFIG_DIR);
        checkDir(CONFIG_DIR.toPath().resolve(childDirName).toFile());
        File f = CONFIG_DIR.toPath().resolve(childDirName).resolve(clazz.getName()).toFile();
        checkDir(f);
        return f;
    }

    public static void loadConfigInstances(TMultiInstanceConfig newInstance) {
        loadConfigInstances(newInstance.getClass(), newInstance.getChildDirName());
    }

    public static void loadConfigInstances(Class<? extends TMultiInstanceConfig> clazz, String childDirName) {
        File childDir = checkChildDir(clazz, childDirName);
        File[] instances = childDir.listFiles();
        if (instances == null) {
            return;
        }
        for (File f : instances) {
            try {
                TMultiInstanceConfig instance = GSON.fromJson(FileUtils.readFileToString(f, StandardCharsets.UTF_8), clazz);
                putCache(instance);
            } catch (IOException ignored) {
                LogUtils.getLogger().error("Failed to load config {}. Things may not work well.", f);
            }
        }
    }


    private static <T extends TMultiInstanceConfig> void putCache(T config) {
        if (CACHE.containsKey(config.getClass())) {
            CACHE.get(config.getClass()).put(config.getFileName(), config);
        } else {
            HashMap<String, TMultiInstanceConfig> children = new HashMap<>();
            children.put(config.getFileName(), config);
            CACHE.put(config.getClass(), children);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends TMultiInstanceConfig> HashMap<String, T> getConfigInstancesRead(Class<T> configClass) {
        return (HashMap<String, T>) CACHE.get(configClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TMultiInstanceConfig> T getConfigInstanceRead(Class<T> configClass, String childName) {
        return (T) CACHE.get(configClass).get(childName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TMultiInstanceConfig> void getConfigInstancesWrite(Class<T> configClass, Consumer<HashMap<String, T>> setter) {
        HashMap<String, T> configInstances = (HashMap<String, T>) CACHE.get(configClass);
        setter.accept(configInstances);
        configInstances.values().forEach(MultiInstanceConfigHelper::saveConfig);

    }

    @SuppressWarnings("unchecked")
    public static <T extends TMultiInstanceConfig> void getConfigInstanceWrite(Class<T> configClass, String childName, Consumer<T> setter) {
        T configInstances = (T) CACHE.get(configClass).get(childName);
        setter.accept(configInstances);
        saveConfig(configInstances);
    }

    public static <T extends TMultiInstanceConfig> void addNewConfigInstance(T config) {
        putCache(config);
        saveConfig(config);
    }

    public static <T extends TMultiInstanceConfig> void removeConfig(T config) {
        CACHE.get(config.getClass()).remove(config.getFileName());
        File configFile = checkChildDir(config).toPath().resolve(config.getFileName() + ".json").toFile();
        configFile.delete();
    }

    private static <T extends TMultiInstanceConfig> void saveConfig(T config) {
        File configFile = checkChildDir(config).toPath().resolve(config.getFileName() + ".json").toFile();
        try {
            FileUtils.write(configFile, GSON.toJson(config), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            LogUtils.getLogger().error("Failed to save config {}. Things may not work well.", config.getClass());
        }
    }
}
