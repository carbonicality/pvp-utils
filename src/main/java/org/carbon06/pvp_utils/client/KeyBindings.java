package org.carbon06.pvp_utils.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY="key.categories.pvp_utils";
    public static KeyMapping ZOOM_KEY;
    public static KeyMapping FULLBRIGHT_KEY;
    public static void register() {
        ZOOM_KEY=new KeyMapping(
                "key.pvp_utils.zoom",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C, // to zoom, we use the C key
                CATEGORY
        );

        FULLBRIGHT_KEY=new KeyMapping(
                "key.pvp_utils.fullbright",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B, // to use fullbright, it's the B key
                CATEGORY
        );
    }
}
