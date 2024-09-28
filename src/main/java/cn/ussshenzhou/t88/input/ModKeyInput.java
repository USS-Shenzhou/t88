package cn.ussshenzhou.t88.input;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.analyzer.back.T88AnalyzerClient;
import cn.ussshenzhou.t88.analyzer.front.AnalyzerScreen;
import cn.ussshenzhou.t88.gui.TestScreen;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.networkanalyzer.gui.NetworkWatcherScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class ModKeyInput {
    public static final KeyMapping GUI_TEST = new KeyMapping(
            "key.t88.gui_test", KeyConflictContext.UNIVERSAL, KeyModifier.ALT,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_T, "key.categories.t88"
    );
    public static final KeyMapping OPEN_ANALYZER = new KeyMapping(
            "key.t88.open_analyzer", KeyConflictContext.IN_GAME, KeyModifier.ALT,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_A, "key.categories.t88"
    );
    public static final KeyMapping CLEAR_ANALYZER = new KeyMapping(
            "key.t88.clear_recorder", KeyConflictContext.IN_GAME, KeyModifier.NONE,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.t88"
    );
    public static final KeyMapping OPEN_WATCHER = new KeyMapping(
            "key.t88.open_watcher", KeyConflictContext.IN_GAME, KeyModifier.ALT,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.t88"
    );

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (T88.TEST) {
            if (GUI_TEST.consumeClick()) {
                runGUITest();
            }
        }
        if (OPEN_ANALYZER.consumeClick()) {
            Minecraft.getInstance().setScreen(new AnalyzerScreen());
        } else if (CLEAR_ANALYZER.consumeClick()) {
            T88AnalyzerClient.RECORDERS.clear();
        } else if (OPEN_WATCHER.consumeClick()) {
            Minecraft.getInstance().setScreen(new NetworkWatcherScreen());

        }
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    private static void runGUITest() {
        String testScreen = System.getProperty("t88.test_screen_override");
        Minecraft minecraft = Minecraft.getInstance();
        if (testScreen == null) {
            minecraft.setScreen(new TestScreen());
        } else {
            try {
                Class<?> testScreenClsOvr = Class.forName(testScreen);
                TScreen testScreenOvr = (TScreen) testScreenClsOvr.getDeclaredConstructor().newInstance();
                minecraft.setScreen(testScreenOvr);
            } catch (ClassNotFoundException e) {
                LogUtils.getLogger().error("Failed to find {}.", testScreen);
                LogUtils.getLogger().error(e.getMessage());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                LogUtils.getLogger().error("Failed to create an instance of {}.", testScreen);
                LogUtils.getLogger().error(e.getMessage());
            }
        }
    }
}
