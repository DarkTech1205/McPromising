package net.floatcraft.mixin;

import net.floatcraft.config.FloatcraftConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs server- and client-side (this class isn't in a client-only mixin set), so a
 * dedicated server running this mod will produce the authentic jank for everyone
 * connected, rather than it just being a lonely client-side visual trick that
 * desyncs from what the server thinks is happening.
 *
 * We deliberately do NOT change Entity's internal x/y/z fields from double to float
 * (that's baked into vanilla's networking, collision epsilon math, and command
 * system at the type level and can't be toggled at runtime). Instead we snap the
 * double position/velocity through a float cast every tick, which reproduces the
 * same visible precision loss and "jitter at high coordinates" feel that old
 * Minecraft had, while staying fully compatible with vanilla systems underneath.
 */
@Mixin(Entity.class)
public abstract class EntityPrecisionMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void floatcraft$quantizeAfterTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        FloatcraftConfig cfg = FloatcraftConfig.get();

        if (cfg.floatMovement) {
            double x = (float) self.getX();
            double y = (float) self.getY();
            double z = (float) self.getZ();
            self.setPosition(new Vec3d(x, y, z));
        }

        if (cfg.floatVelocity) {
            Vec3d v = self.getVelocity();
            self.setVelocity((float) v.x, (float) v.y, (float) v.z);
        }
    }
}
