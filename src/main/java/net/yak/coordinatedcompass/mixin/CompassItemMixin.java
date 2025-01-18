package net.yak.coordinatedcompass.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Objects;

@Mixin(CompassItem.class)
public abstract class CompassItemMixin extends Item {

	public CompassItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient()) {
			if (user.getStackInHand(hand).contains(DataComponentTypes.LODESTONE_TRACKER) && user.getStackInHand(hand).get(DataComponentTypes.LODESTONE_TRACKER) != null
					&& Objects.requireNonNull(user.getStackInHand(hand).get(DataComponentTypes.LODESTONE_TRACKER)).target().isPresent() && user.isSneaking()) {
				BlockPos lodestonePosition = Objects.requireNonNull(user.getStackInHand(hand).get(DataComponentTypes.LODESTONE_TRACKER)).target().get().pos();
				user.sendMessage(Text.translatable("message.coordinatedcompass.lodestone_coordinates", (int) lodestonePosition.getX(), (int) lodestonePosition.getZ()), true);
			}
			else {
				user.sendMessage(Text.translatable("message.coordinatedcompass.coordinates", (int) user.getX(), (int) user.getZ()), true);
			}
			user.playSound(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, 0.7f, 0.8f);
			user.swingHand(hand);
		}
		return super.use(world, user, hand);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		tooltip.add(Text.translatable("tooltip.coordinatedcompass.compass_tooltip"));
		if (stack.contains(DataComponentTypes.LODESTONE_TRACKER)) {
			tooltip.add(Text.translatable("tooltip.coordinatedcompass.lodestone_compass_tooltip"));
		}
		super.appendTooltip(stack, context, tooltip, type);
	}
}