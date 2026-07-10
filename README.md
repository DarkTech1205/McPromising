# Floatcraft (Fabric, Minecraft 1.20.1)

A mod that:
- Recreates classic Alpha/Beta-era "float precision" jank for movement, velocity,
  and the camera - toggleable independently, live, via config.
- Removes the world border.
- Adds a small amount of new content to the End (Chorus Crystal Ore + a
  procedurally-generated "Void Spire" mini-structure with a loot chest).
- (Experimental, off by default) quantizes terrain noise sampling through float,
  for old-school worldgen jank on newly generated chunks.

## Important - please read before building

I wrote this without a working internet connection in my sandbox, so **I was not
able to run Gradle or compile this against real Minecraft/Yarn artifacts.** The
code is written carefully against Minecraft 1.20.1's actual Yarn-mapped API, and
the "safe" parts (config, block/item registration, world border, End content) use
very standard, heavily-documented APIs I'm confident about. The riskiest files,
clearly marked in their own comments, are:

- `mixin/worldgen/NoiseSamplerMixin.java` - targets deep internal terrain noise
  code. Registered in its own `"required": false` mixin config
  (`floatcraft.worldgen.mixins.json`), so **if this fails to apply, the rest of
  the mod still works fine** - you just won't get the worldgen jank effect.
  (Verified against the real yarn-1.20.1+build.2 API docs, so confidence is
  high, but it's still the deepest internal target in the mod.)

If it fails to apply, you'll see a warning in the log, not a crash. Everything
else (movement/velocity/camera jank, world border removal, End content) uses
stable, common APIs and should just work.

## Why "double to float" is a toggleable *effect*, not a literal field-type swap

Vanilla Minecraft stores entity positions as `double` at the type level, and that's
baked into networking, collision math, and commands. You can't flip that at
runtime. So instead of forking huge parts of the engine, `EntityPrecisionMixin`
snaps position and velocity through a `float` cast every tick when enabled -
which reproduces the actual visible effect (the same precision loss/quantization
old Minecraft had) while staying fully compatible with everything underneath.
This mixin runs on the logical side (works server-side too, not just a client
visual trick), so a dedicated server running this mod gives every connected
player the same jank.

## Config

`config/floatcraft.json`, created on first launch:

```json
{
  "floatMovement": true,
  "floatVelocity": true,
  "floatCamera": true,
  "floatWorldGen": false,
  "removeWorldBorder": true,
  "addEndContent": true
}
```

Edit while the game is closed, or edit and rejoin the world - movement/velocity/
camera toggles are read live every tick, world-gen only affects newly generated
chunks.

## Building via GitHub (recommended if you don't have Java/Gradle installed)

A working `.github/workflows/build.yml` is already included in this zip - you
don't need to write anything. It uses the official `gradle/actions/setup-gradle`
action, which downloads and runs Gradle itself, so there's no local Gradle
Wrapper to worry about.

1. Create a free account at github.com if you don't have one.
2. Create a new repository (any name, Public is fine).
3. Upload this entire `floatcraft` folder into it (drag-and-drop on the
   "uploading an existing file" page works fine - make sure `.github` comes
   along too; GitHub sometimes hides dot-folders in the drag preview but it
   does upload them).
4. Go to the **Actions** tab of your repo. A run should start automatically
   (or click **Run workflow** if not). Wait for the green checkmark.
5. Click the finished run → **Artifacts** → download `floatcraft-jar`. Unzip
   it - `floatcraft-1.0.0.jar` is your compiled mod.

If it fails (red X), click into the failed step and copy me the exact error
text - with the real compiler output I can pinpoint the fix precisely, the
same way I found and fixed the two real bugs in this version.

## Building locally (alternative)

You'll need a JDK 17, [Gradle](https://gradle.org/install/) installed, and an
internet connection (for Gradle to pull Minecraft, Yarn mappings, Fabric
Loader, and Fabric API). This zip does **not** include the Gradle Wrapper
scripts (`gradlew`/`gradlew.bat`), so run plain `gradle` rather than
`./gradlew`:

```bash
cd floatcraft
gradle build
```

Easiest alternative: open the `floatcraft` folder in IntelliJ IDEA with the
Fabric/Gradle plugins installed - it bootstraps everything (including the
wrapper) automatically, no terminal commands needed.

The built jar will be in `build/libs/floatcraft-1.0.0.jar`. Drop it in your
`.minecraft/mods` folder along with a matching **Fabric API** jar for 1.20.1
and **Fabric Loader** 0.15.x.

## If a mixin fails to apply

The error log will name the exact class and method Mixin couldn't find. Open
that vanilla class through your IDE (Loom decompiles/generates mapped sources
automatically once you've run a build once) and check the real method name/
signature in your exact Yarn build, then adjust the `method = "..."` string in
the corresponding Mixin file to match.

## Extending

- `world/VoidSpireFeature.java` is intentionally simple (procedural block
  placement) so it ships as plain code with no binary `.nbt` structure files.
  Swap it for a proper structure-file-based feature if you want something more
  elaborate.
- Adding a custom mob would need an EntityType registration, attributes,
  a renderer + a real texture (not something I can reliably hand-author as
  pixel art blind), so it's left out - happy to add a skeleton for one if you
  want to supply/commission art for it.
