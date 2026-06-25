package com.scrp.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class SCRPModClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("scrpmod");

    @Override
    public void onInitializeClient() {
        LOGGER.info("SCRP Mod Client initialized!");
    }
}
