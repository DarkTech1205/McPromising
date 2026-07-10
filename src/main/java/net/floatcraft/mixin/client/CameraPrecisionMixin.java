package net.floatcraft.mixin.client;

import net.floatcraft.config.FloatcraftConfig;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The GPU-facing render pipeline already works in floats after Minecraft subtracts
 * the camera position from world doubles (relative rendering), so this mixin isn't
 * needed for raw rendering precision. What it DOES do is quantize the camera's own
 * anchor position through float too, so panning/bobbing has the same slightly
 * "steppy" quality classic float-precision movement had, instead of perfectly
 * smooth double-precision camera motion riding on top of jankier entity movement.
 *
 * Kept as a separate toggle (floatCamera) from floatMovement/floatVelocity so you
 * can mix and match, e.g. jank movement with a smooth camera, or vice versa.
 *
 * Camera#setPos(Vec3d) is protected. An earlier version of this file tried
 * "extends Camera" as a way around that - Mixin's own compiler rejects that
 * outright (confirmed by a real build error, not a guess this time). The
 * correct, standard Mixin technique for calling a protected/private target
 * method is @Invoker, which generates a proper synthetic accessor for it.
 */
@Mixin(Camera.class)
public abstract class CameraPrecisionMixin {

    @Invoker("setPos")
    public abstract void floatcraft$invokeSetPos(Vec3d pos);

    @Inject(method = "update", at = @At("TAIL"))
    private void floatcraft$quantizeCamera(CallbackInfo ci) {
        if (!FloatcraftConfig.get().floatCamera) return;

        Camera self = (Camera) (Object) this;
        Vec3d pos = self.getPos();
        Vec3d quantized = new Vec3d((float) pos.x, (float) pos.y, (float) pos.z);
        this.floatcraft$invokeSetPos(quantized);
    }
}
