package dev.cassettewalkman;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CassetteWalkman implements ModInitializer {
    public static final String MOD_ID = "cassettewalkman";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        LOGGER.info("Cassette Walkman initialized.");
    }
}
