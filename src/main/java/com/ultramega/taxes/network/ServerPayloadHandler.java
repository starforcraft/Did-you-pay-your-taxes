package com.ultramega.taxes.network;

import com.ultramega.taxes.container.TaxContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void openTaxMenuData(final OpenTaxMenuData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player) -> new TaxContainerMenu(containerId, playerInventory),
                        Component.empty()
                ));
            }
        }).exceptionally(e -> null);
    }

    public static void setSelectedTaxData(final SetSelectedTaxData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof TaxContainerMenu menu) {
                menu.setSelectedTaxType(data.taxType());
            }
        }).exceptionally(e -> null);
    }
}
