package com.ultramega.taxes.network;

import com.ultramega.taxes.utils.TaxTypes;
import com.ultramega.taxes.Taxes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record AddTaxData(String itemId, double amount, int taxRate, TaxTypes taxType) implements CustomPacketPayload {
    public static final Type<AddTaxData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "add_tax_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AddTaxData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AddTaxData::itemId,
            ByteBufCodecs.DOUBLE, AddTaxData::amount,
            ByteBufCodecs.INT, AddTaxData::taxRate,
            NeoForgeStreamCodecs.enumCodec(TaxTypes.class), AddTaxData::taxType,
            AddTaxData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
