package net.floatcraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.floatcraft.FloatcraftMod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * All toggles live in config/floatcraft.json and can be flipped without a rebuild.
 * Restart the world/game after editing for changes to fully apply (some toggles,
 * like movement/velocity/camera, are read live every tick and take effect instantly;
 * world-gen ones only affect newly generated chunks).
 */
public class FloatcraftConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("floatcraft.json");
    private static FloatcraftConfig INSTANCE;

    // --- double -> float precision toggles (recreate Bedrock's "Stripe Lands") ---
    // These have essentially zero effect near spawn (float32 has huge precision
    // for small numbers) and naturally start producing visible 1-2 block snapping
    // right around +/-16,777,216 (2^24) - no distance check needed, it's just how
    // IEEE 754 float32 works. That's the real mechanism behind Bedrock's Stripe
    // Lands, reproduced here in Java.
    public boolean floatMovement = true;   // quantize entity position through float each tick
    public boolean floatVelocity = true;   // quantize entity velocity through float each tick
    public boolean floatCamera = true;     // quantize the render camera position through float
    public boolean floatWorldGen = false;  // recreate classic Far Lands-style terrain corruption (new chunks only)

    // --- other toggles ---
    public boolean removeWorldBorder = true;
    public boolean addEndContent = true;

    public static FloatcraftConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                INSTANCE = GSON.fromJson(reader, FloatcraftConfig.class);
                if (INSTANCE == null) INSTANCE = new FloatcraftConfig();
            } catch (IOException e) {
                FloatcraftMod.LOGGER.warn("Failed to read floatcraft.json, regenerating defaults", e);
                INSTANCE = new FloatcraftConfig();
            }
        } else {
            INSTANCE = new FloatcraftConfig();
        }
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            FloatcraftMod.LOGGER.error("Failed to save floatcraft.json", e);
        }
    }
}
