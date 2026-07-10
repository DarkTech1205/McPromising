package net.floatcraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.floatcraft.FloatcraftMod;

public class FloatcraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FloatcraftMod.LOGGER.info("Floatcraft client features ready (camera precision, border overlay hiding).");
    }
}
