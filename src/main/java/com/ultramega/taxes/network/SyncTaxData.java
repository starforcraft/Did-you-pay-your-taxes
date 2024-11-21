package com.ultramega.taxes.network;

import com.ultramega.taxes.utils.TaxTypes;
import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.utils.ModCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.LinkedHashMap;

public record SyncTaxData(LinkedHashMap<String, Double> tax, TaxTypes taxType) implements CustomPacketPayload {
    public static final Type<SyncTaxData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "sync_tax_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTaxData> STREAM_CODEC = StreamCodec.composite(
            ModCodecs.STREAM_CODEC, SyncTaxData::tax,
            NeoForgeStreamCodecs.enumCodec(TaxTypes.class), SyncTaxData::taxType,
            SyncTaxData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
