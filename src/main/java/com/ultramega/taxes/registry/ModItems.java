package com.ultramega.taxes.registry;

import com.ultramega.taxes.Taxes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Taxes.MODID);

    public static final DeferredItem<Item> IRS_SPAWN_EGG = ITEMS.register("irs_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.IRS_ENTITY, 0x3e3f40, 0xffe4de, new Item.Properties()));
    public static final DeferredItem<Item> ROCKET_SPAWN_EGG = ITEMS.register("rocket_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.ROCKET_ENTITY, 0x089c34, 0xa8ba02, new Item.Properties()));
}
