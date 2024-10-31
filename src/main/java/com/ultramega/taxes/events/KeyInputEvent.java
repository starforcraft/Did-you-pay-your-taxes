package com.ultramega.taxes.events;

import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.network.OpenTaxMenuData;
import com.ultramega.taxes.registry.ModKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Taxes.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KeyInputEvent {
    @SubscribeEvent
    public static void onKeyInput(final InputEvent.Key event) {
        if (ModKeyBindings.OPEN_TAXES_MENU.isDown()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.isAlive()) {
                PacketDistributor.sendToServer(new OpenTaxMenuData());
            }
        }
    }
}
