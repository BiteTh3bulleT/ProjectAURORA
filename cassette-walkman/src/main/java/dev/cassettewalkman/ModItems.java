package dev.cassettewalkman;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {
    private ModItems() {}

    private static final ResourceKey<Item> WALKMAN_KEY = key("walkman");
    private static final ResourceKey<Item> CAVE_VIOLENCE_CASSETTE_KEY = key("cave_violence_cassette");

    public static final Item WALKMAN = register(WALKMAN_KEY, new Item.Properties().stacksTo(1));
    public static final Item CAVE_VIOLENCE_CASSETTE = register(CAVE_VIOLENCE_CASSETTE_KEY, new Item.Properties().stacksTo(1));

    private static ResourceKey<Item> key(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CassetteWalkman.MOD_ID, path));
    }

    private static Item register(ResourceKey<Item> key, Item.Properties properties) {
        Item item = new Item(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> {
                    entries.accept(WALKMAN);
                    entries.accept(CAVE_VIOLENCE_CASSETTE);
                });
    }
}
