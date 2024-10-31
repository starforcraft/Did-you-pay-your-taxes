package com.ultramega.taxes.network;

import com.ultramega.taxes.Taxes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateTaxSlotsData(int slotIndex) implements CustomPacketPayload {
    public static final Type<UpdateTaxSlotsData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "update_tax_slots_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateTaxSlotsData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateTaxSlotsData::slotIndex,
            UpdateTaxSlotsData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
