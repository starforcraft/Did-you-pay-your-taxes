package com.ultramega.taxes.network;

import com.ultramega.taxes.utils.TaxTypes;
import com.ultramega.taxes.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.LinkedHashMap;

public class BiPayloadHandler {
    public static void addTaxData(final AddTaxData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            LinkedHashMap<String, Double> taxes = player.getData(getAttachment(data.taxType()));

            double amount = data.amount() * ((double) data.taxRate() / 100);

            taxes.put(data.itemId(), taxes.getOrDefault(data.itemId(), 0.0) + amount);

            player.setData(getAttachment(data.taxType()), taxes);
        }).exceptionally(e -> null);
    }

    public static void syncTaxData(final SyncTaxData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.setData(getAttachment(data.taxType()), data.tax());
        }).exceptionally(e -> null);
    }

    private static AttachmentType<LinkedHashMap<String, Double>> getAttachment(TaxTypes taxType) {
        return switch (taxType) {
            case MINING_TAX -> ModAttachments.MINING_TAX.get();
            case SMELTING_TAX -> ModAttachments.SMELTING_TAX.get();
            case TRADING_TAX -> ModAttachments.TRADING_TAX.get();
            default -> null;
        };
    }
}
