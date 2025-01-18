package net.yak.coordinatedcompass;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CompassOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (MinecraftClient.getInstance().cameraEntity instanceof PlayerEntity player) {
            /*for (int i = 0; i < player.getInventory().size(); ++i) {
                if (player.getInventory().getStack(0).isOf(Items.RECOVERY_COMPASS)) {

                }
            }*/ // TODO: work more on a reimplementation of the action bar so the recovery compass and other compass overlays are minimally invasive
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("message.coordinated.coordinates", (int) player.getX(), (int) player.getZ()), 300, 300, 0xffffff, true);

        }
    }
}
