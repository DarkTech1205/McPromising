package net.floatcraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.floatcraft.config.FloatcraftConfig;
import net.floatcraft.world.ModBlocks;
import net.floatcraft.world.ModFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloatcraftMod implements ModInitializer {

    public static final String MOD_ID = "floatcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        FloatcraftConfig.load();
        FloatcraftConfig cfg = FloatcraftConfig.get();

        LOGGER.info(
            "Floatcraft loaded - movement={} velocity={} camera={} border={} endContent={}",
            cfg.floatMovement, cfg.floatVelocity, cfg.floatCamera,
            !cfg.removeWorldBorder, cfg.addEndContent
        );

        ModBlocks.init();
        ModFeatures.init();

        // Simplest, lowest-risk way to "remove" the world border: push it out past
        // the vanilla playable area every time a world loads, for every dimension.
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (FloatcraftConfig.get().removeWorldBorder) {
                world.getWorldBorder().setSize(60_000_000.0D);
            }
        });
    }
}
