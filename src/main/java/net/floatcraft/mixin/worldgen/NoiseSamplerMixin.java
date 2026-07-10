package net.floatcraft.mixin.worldgen;

import net.floatcraft.config.FloatcraftConfig;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Quantizes the x/y/z coordinates fed into terrain noise sampling through
 * float, so freshly-generated chunks develop the same kind of precision-loss
 * corruption that produces the classic Far Lands. Off by default - see
 * floatWorldGen in the config.
 *
 * Verified against the real yarn-1.20.1+build.2 API docs (not guessed from
 * memory): PerlinNoiseSampler#sample(double x, double y, double z) is a real,
 * public, non-deprecated method in net.minecraft.util.math.noise. An earlier
 * version of this file targeted a class called "ImprovedNoise" that turned
 * out not to exist at all in 1.20.1 - that was the actual cause of the
 * previous build failure, now confirmed and fixed.
 */
@Mixin(PerlinNoiseSampler.class)
public abstract class NoiseSamplerMixin {

    @ModifyVariable(method = "sample(DDD)D", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double floatcraft$quantizeX(double x) {
        return FloatcraftConfig.get().floatWorldGen ? (float) x : x;
    }

    @ModifyVariable(method = "sample(DDD)D", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double floatcraft$quantizeY(double y) {
        return FloatcraftConfig.get().floatWorldGen ? (float) y : y;
    }

    @ModifyVariable(method = "sample(DDD)D", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private double floatcraft$quantizeZ(double z) {
        return FloatcraftConfig.get().floatWorldGen ? (float) z : z;
    }
}
