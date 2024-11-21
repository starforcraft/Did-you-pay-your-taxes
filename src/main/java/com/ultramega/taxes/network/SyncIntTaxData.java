package com.ultramega.taxes.network;

import com.ultramega.taxes.utils.TaxTypes;
import com.ultramega.taxes.Taxes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record SyncIntTaxData(int tax, TaxTypes taxType) implements CustomPacketPayload {
    public static final Type<SyncIntTaxData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "sync_int_tax_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncIntTaxData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncIntTaxData::tax,
            NeoForgeStreamCodecs.enumCodec(TaxTypes.class), SyncIntTaxData::taxType,
            SyncIntTaxData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
