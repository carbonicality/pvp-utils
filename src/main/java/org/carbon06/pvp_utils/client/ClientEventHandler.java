package org.carbon06.pvp_utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(value=Dist.CLIENT,modid="pvp_utils")
public class ClientEventHandler {
    private static boolean fbEnabled=false;
    private static boolean zoomActive=false;
    private static double normalFOV=70.0;
    private static final double ZOOM_FOV=5.0;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc=Minecraft.getInstance();
        if (mc.player==null) return;
        if (KeyBindings.FULLBRIGHT_KEY.consumeClick()) {
            fbEnabled=!fbEnabled;
            mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(fbEnabled?"§aFullbright enabled":"§cFullbright disabled"),
                    true
            );
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase==TickEvent.Phase.END) {
            Minecraft mc=Minecraft.getInstance();
            if (mc.player!=null&&fbEnabled) {
                if (mc.player.hasEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION)) {
                    mc.player.removeEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION);
                }
                mc.player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.NIGHT_VISION,
                        400,
                        0,
                        false,
                        false,
                        false
                ));
            } else if (mc.player!=null&&!fbEnabled) {
                if (mc.player.hasEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION)) {
                    mc.player.removeEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFOVModifier(ViewportEvent.ComputeFov event) {
        Minecraft mc=Minecraft.getInstance();
        if (KeyBindings.ZOOM_KEY.isDown()) {
            if (!zoomActive) {
                zoomActive=true;
                normalFOV=mc.options.fov().get();
            }
            double currentFOV=mc.options.fov().get();
            double targetFOV=ZOOM_FOV;
            double newFOV=currentFOV+(targetFOV-currentFOV)*0.5;
            event.setFOV(newFOV);
        }else{
            if (zoomActive) {
                zoomActive=false;
            }
        }
    }
}
