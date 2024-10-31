package com.ultramega.taxes.network;

import com.ultramega.taxes.TaxTypes;
import com.ultramega.taxes.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    public static void syncIntTaxData(final SyncIntTaxData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.setData(getAttachment(data.taxType()), data.tax());
        }).exceptionally(e -> null);
    }

    private static AttachmentType<Integer> getAttachment(TaxTypes taxType) {
        return switch (taxType) {
            case TAX_DAYS_LEFT -> ModAttachments.TAX_DAYS_LEFT.get();
            case TAX_RATE -> ModAttachments.TAX_RATE.get();
            default -> null;
        };
    }
}
