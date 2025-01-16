package net.yak.coordinatedcompass;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordinatedCompass implements ModInitializer {
	public static final String MOD_ID = "coordinatedcompass";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("they call me the coordinated compass");
	}
}