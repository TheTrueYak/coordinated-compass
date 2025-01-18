package net.yak.coordinatedcompass.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.yak.coordinatedcompass.CoordinatedCompass;
import net.yak.coordinatedcompass.component.RecoveryCompassComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(Item.class)
public abstract class RecoveryCompassItemMixin implements ToggleableFeature, ItemConvertible, FabricItem {

    @Inject(method = "inventoryTick", at = @At(value = "HEAD"))
	private void coordinated$showRecoveryCompassCoordinates(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (stack.isOf(Items.RECOVERY_COMPASS)) {
            if (entity instanceof PlayerEntity user && user.age % 5 == 0 && stack.contains(CoordinatedCompass.RECOVERY_COMPASS_CYCLE) && world.isClient()) {
                int value = Objects.requireNonNull(stack.get(CoordinatedCompass.RECOVERY_COMPASS_CYCLE)).getCurrentCycle();
                switch (value) {
                    case 1: { // coordinates only
                        user.sendMessage(Text.translatable("message.coordinatedcompass.coordinates", (int) user.getX(), (int) user.getZ()).formatted(Formatting.AQUA), true);
                        break;
                    }
                    case 2: { // biome only
                        String biomeName = "biome." + world.getBiome(user.getBlockPos()).getIdAsString().replaceAll(":", ".");
                        user.sendMessage(Text.translatable("message.coordinatedcompass.biome").append(Text.translatable(biomeName)).formatted(Formatting.AQUA), true);
                        break;
                    }
                    case 3: { // coordinates and biome
                        String biomeName = "biome." + world.getBiome(user.getBlockPos()).getIdAsString().replaceAll(":", ".");
                        user.sendMessage(Text.translatable("message.coordinatedcompass.coordinates_biome", (int) user.getX(), (int) user.getZ()).append(Text.translatable(biomeName)).formatted(Formatting.AQUA), true);
                        break;
                    }
                }
            }
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void coordinated$biomeToggle(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(Items.RECOVERY_COMPASS) && clickType == ClickType.RIGHT && otherStack.isEmpty()) {
            if (stack.contains(CoordinatedCompass.RECOVERY_COMPASS_CYCLE)) {
                int value = Objects.requireNonNull(stack.get(CoordinatedCompass.RECOVERY_COMPASS_CYCLE)).getCurrentCycle();
                stack.set(CoordinatedCompass.RECOVERY_COMPASS_CYCLE, RecoveryCompassComponent.cycleValue(value));
                slot.markDirty();
                if (player.getWorld().isClient()) {
                    player.playSound(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, 0.7f, 1.2f);
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "appendTooltip", at = @At(value = "HEAD"))
    private void coordinated$recoveryCompassTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if (stack.isOf(Items.RECOVERY_COMPASS)) {
            if (stack.contains(CoordinatedCompass.RECOVERY_COMPASS_CYCLE)) {
                int value = Objects.requireNonNull(stack.get(CoordinatedCompass.RECOVERY_COMPASS_CYCLE)).getCurrentCycle();
                tooltip.add(Text.translatable("tooltip.coordinatedcompass.recovery_compass_tooltip").formatted(Formatting.AQUA));
                switch (value) { // I tried to do it in the component I'm sorry :/
                    case 1: { // coordinates enabled
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.coordinates_enabled").formatted(Formatting.AQUA));
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.biome_disabled").setStyle(Style.EMPTY.withColor(0x326569)));
                        break;
                    }
                    case 2: { // biome enabled
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.coordinates_disabled").setStyle(Style.EMPTY.withColor(0x326569)));
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.biome_enabled").formatted(Formatting.AQUA));
                        break;
                    }
                    case 3: { // coordinates and biome enabled
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.coordinates_enabled").formatted(Formatting.AQUA));
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.biome_enabled").formatted(Formatting.AQUA));
                        break;
                    }
                    default: { // both disabled
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.coordinates_disabled").setStyle(Style.EMPTY.withColor(0x326569)));
                        tooltip.add(Text.translatable("tooltip.coordinatedcompass.biome_disabled").setStyle(Style.EMPTY.withColor(0x326569)));
                    }
                }
                if (MinecraftClient.getInstance().cameraEntity instanceof PlayerEntity user && user.getWorld().isClient()) {
                    tooltip.add(Text.translatable("message.coordinatedcompass.coordinates", (int) user.getX(), (int) user.getZ()).formatted(Formatting.AQUA));
                }
            }
        }
    }

}