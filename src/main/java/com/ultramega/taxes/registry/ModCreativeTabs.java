package com.ultramega.taxes.registry;

import com.ultramega.taxes.Taxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Taxes.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAXES_TAB = CREATIVE_MODE_TABS.register("taxes_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + Taxes.MODID))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.DIRT::getDefaultInstance)
            .displayItems((parameters, output) -> {
                for(DeferredHolder<Item, ?> item : ModItems.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            }).build());
}
