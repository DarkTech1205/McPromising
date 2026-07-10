package net.floatcraft.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.floatcraft.FloatcraftMod;
import net.floatcraft.config.FloatcraftConfig;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {

    public static final Feature<DefaultFeatureConfig> VOID_SPIRE = new VoidSpireFeature(DefaultFeatureConfig.CODEC);

    public static void init() {
        // Register the raw Feature<?> type (code registry). The actual
        // ConfiguredFeature/PlacedFeature instances are data-driven JSON
        // under data/floatcraft/worldgen/ - see void_spire.json and
        // void_spire_placed.json.
        Registry.register(Registries.FEATURE, id("void_spire"), VOID_SPIRE);

        if (FloatcraftConfig.get().addEndContent) {
            BiomeModifications.addFeature(
                    BiomeSelectors.tag(BiomeTags.IS_END),
                    GenerationStep.Feature.SURFACE_STRUCTURES,
                    RegistryKey.of(RegistryKeys.PLACED_FEATURE, id("void_spire_placed"))
            );
        }
    }

    private static Identifier id(String path) {
        return new Identifier(FloatcraftMod.MOD_ID, path);
    }
}
