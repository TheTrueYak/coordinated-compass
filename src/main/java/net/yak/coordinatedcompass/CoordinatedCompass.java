package net.yak.coordinatedcompass;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import net.yak.coordinatedcompass.component.RecoveryCompassComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class CoordinatedCompass implements ModInitializer {
	public static final String MOD_ID = "coordinatedcompass";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ComponentType<RecoveryCompassComponent> RECOVERY_COMPASS_CYCLE = register("recovery_compass_cycle",
			(builder) -> builder.codec(RecoveryCompassComponent.CODEC).packetCodec(RecoveryCompassComponent.PACKET_CODEC)); // custom component cheering


	@Override
	public void onInitialize() {

		LOGGER.info("they call me the coordinated compass");

		DefaultItemComponentEvents.MODIFY.register(context -> context.modify( // yayyyy default components
				Predicate.isEqual(Items.RECOVERY_COMPASS),
				(builder, item) -> builder.add(RECOVERY_COMPASS_CYCLE, new RecoveryCompassComponent(0))
		));

	}

	private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(ComponentType.builder())).build());
	}
}