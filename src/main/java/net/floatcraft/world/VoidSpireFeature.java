package net.floatcraft.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * A small procedurally-built spire: alternating obsidian and Chorus Crystal Ore
 * layers, topped with a chest seeded from the vanilla End City treasure loot
 * table. Written in code rather than as an .nbt structure file so it can be
 * shipped as plain text/JSON without any binary structure assets.
 */
public class VoidSpireFeature extends Feature<DefaultFeatureConfig> {

    public VoidSpireFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        int height = 5 + random.nextInt(6); // 5-10 blocks tall
        BlockPos.Mutable mutable = origin.mutableCopy();

        for (int i = 0; i < height; i++) {
            mutable.setY(origin.getY() + i);
            boolean crystalLayer = (i % 3 == 0);
            world.setBlockState(
                    mutable,
                    (crystalLayer ? ModBlocks.CHORUS_CRYSTAL_ORE : Blocks.OBSIDIAN).getDefaultState(),
                    3
            );
        }

        mutable.setY(origin.getY() + height);
        world.setBlockState(mutable, Blocks.CHEST.getDefaultState(), 3);

        if (world.getBlockEntity(mutable) instanceof ChestBlockEntity chest) {
            chest.setLootTable(LootTables.END_CITY_TREASURE_CHEST, random.nextLong());
        }

        return true;
    }
}
