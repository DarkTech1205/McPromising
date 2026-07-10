package net.floatcraft.world;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.floatcraft.FloatcraftMod;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block CHORUS_CRYSTAL_ORE = new Block(FabricBlockSettings.create()
            .mapColor(MapColor.PURPLE)
            .requiresTool()
            .strength(3.0F, 6.0F)
            .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
            .luminance(state -> 5));

    public static final Item CHORUS_CRYSTAL = new Item(new Item.Settings());

    public static void init() {
        Registry.register(Registries.BLOCK, id("chorus_crystal_ore"), CHORUS_CRYSTAL_ORE);
        Registry.register(Registries.ITEM, id("chorus_crystal_ore"),
                new BlockItem(CHORUS_CRYSTAL_ORE, new Item.Settings()));
        Registry.register(Registries.ITEM, id("chorus_crystal"), CHORUS_CRYSTAL);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries ->
                entries.add(CHORUS_CRYSTAL_ORE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->
                entries.add(CHORUS_CRYSTAL));
    }

    private static Identifier id(String path) {
        return new Identifier(FloatcraftMod.MOD_ID, path);
    }
}
