package com.ultramega.taxes.network;

import com.ultramega.taxes.Taxes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Taxes.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PayloadEvent {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Taxes.MODID).versioned("1.0");
        registrar.playToServer(
                OpenTaxMenuData.TYPE,
                OpenTaxMenuData.STREAM_CODEC,
                ServerPayloadHandler::openTaxMenuData
        );
        registrar.playToServer(
                UpdateTaxSlotsData.TYPE,
                UpdateTaxSlotsData.STREAM_CODEC,
                ServerPayloadHandler::updateTaxSlotsData
        );
        registrar.playBidirectional(
                AddTaxData.TYPE,
                AddTaxData.STREAM_CODEC,
                BiPayloadHandler::addTaxData
        );
        registrar.playBidirectional(
                SyncTaxData.TYPE,
                SyncTaxData.STREAM_CODEC,
                BiPayloadHandler::syncTaxData
        );
        registrar.playToClient(
                SyncIntTaxData.TYPE,
                SyncIntTaxData.STREAM_CODEC,
                ClientPayloadHandler::syncIntTaxData
        );
    }
}
