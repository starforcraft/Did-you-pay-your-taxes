package com.ultramega.taxes.registry;

import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.container.TaxContainerMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Taxes.MODID);

    public static final Supplier<MenuType<TaxContainerMenu>> TAX_CONTAINER_MENU = MENU_TYPES.register("tax_menu", () ->
            new MenuType<>(TaxContainerMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
