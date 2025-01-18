package net.yak.coordinatedcompass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

public class CoordinatedCompassClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //HudRenderCallback.EVENT.register(new CompassOverlay());
    }
}
