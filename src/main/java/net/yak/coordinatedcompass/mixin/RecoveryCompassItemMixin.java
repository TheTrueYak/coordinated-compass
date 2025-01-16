package net.yak.coordinatedcompass.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
            if (entity instanceof PlayerEntity user && user.age % 5 == 0 && world.isClient()) {
                if (stack.get(DataComponentTypes.CUSTOM_DATA) != null && !user.isSleeping()) {
                    if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") == 1) { // coordiantes only
                        user.sendMessage(Text.literal("X: " + (int) user.getX() + ", Z: " + (int) user.getZ()).formatted(Formatting.AQUA), true);
                    }
                    else if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") == 2) { // biome only
                        String biomeName = "biome." + world.getBiome(user.getBlockPos()).getIdAsString().replaceAll(":", ".");
                        user.sendMessage(Text.literal("Biome: ").append(Text.translatable(biomeName)).formatted(Formatting.AQUA), true);
                    }
                    else if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") == 3) { // both
                        String biomeName = "biome." + world.getBiome(user.getBlockPos()).getIdAsString().replaceAll(":", ".");
                        user.sendMessage(Text.literal("X: " + (int) user.getX() + ", Z: " + (int) user.getZ() + ", Biome: ").append(Text.translatable(biomeName)).formatted(Formatting.AQUA), true);
                    }
                }
            }
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void coordinated$biomeToggle(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(Items.RECOVERY_COMPASS) && clickType == ClickType.RIGHT && otherStack.isEmpty()) {
            if (!stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                NbtCompound nbt = new NbtCompound();
                nbt.putInt("coordinate", 1);
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                slot.markDirty();
                if (player.getWorld().isClient()) {
                    player.playSound(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, 0.7f, 1.2f);
                }
                cir.setReturnValue(true);
            }
            else if (stack.get(DataComponentTypes.CUSTOM_DATA) != null && Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).contains("coordinate")) {
                int value = Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate");
                NbtCompound newNBT = new NbtCompound();
                newNBT.putInt("coordinate", cycleInt(value));
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(newNBT));
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
            tooltip.add(Text.literal("When in the inventory:").setStyle(Style.EMPTY.withColor(0x55FFFF)));
            if (stack.get(DataComponentTypes.CUSTOM_DATA) != null && Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).contains("coordinate")) {
                if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") == 1
                        || Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") == 3) {
                    tooltip.add(Text.literal("Display Coordinates: Enabled").setStyle(Style.EMPTY.withColor(0x55FFFF)));
                }
                else if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") == 0
                        || Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") == 2) {
                    tooltip.add(Text.literal("Display Coordinates: Disabled").setStyle(Style.EMPTY.withColor(0x326569)));
                }
                if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") >= 2) {
                    tooltip.add(Text.literal("Display Biome: Enabled").setStyle(Style.EMPTY.withColor(0x55FFFF)));
                }
                else if (Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getInt("coordinate") < 2) {
                    tooltip.add(Text.literal("Display Biome: Disabled").setStyle(Style.EMPTY.withColor(0x326569)));
                }
            }
            else {
                tooltip.add(Text.literal("Display Coordinates: Disabled").setStyle(Style.EMPTY.withColor(0x326569)));
                tooltip.add(Text.literal("Display Biome: Disabled").setStyle(Style.EMPTY.withColor(0x326569)));
            }
            if (MinecraftClient.getInstance().cameraEntity instanceof PlayerEntity user && user.getWorld().isClient()) {
                tooltip.add(Text.literal("X: " + (int) user.getX() + ", Z: " + (int) user.getZ()).setStyle(Style.EMPTY.withColor(0x55FFFF)));
            }
        }
    }

    @Unique
    private int cycleInt(int num) { // adds one whenever used, resets to 0 if at or above 3
        if (num >= 3) {
            return 0;
        }
        return num + 1;
    }

}