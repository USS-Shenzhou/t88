package cn.ussshenzhou.t88.input;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.gui.TestScreen;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ModKeyInput {
    public static final KeyMapping GUI_TEST = new KeyMapping(
            "key.t88.gui_test", KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_T, "key.categories.t88"
    );

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (T88.TEST) {

            if (GUI_TEST.consumeClick()) {
                runGUITest();
            }
        }
    }

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
