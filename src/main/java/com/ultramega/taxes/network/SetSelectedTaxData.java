package com.ultramega.taxes.network;

import com.ultramega.taxes.utils.TaxTypes;
import com.ultramega.taxes.Taxes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record SetSelectedTaxData(TaxTypes taxType) implements CustomPacketPayload {
    public static final Type<SetSelectedTaxData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "set_selected_tax_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetSelectedTaxData> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(TaxTypes.class), SetSelectedTaxData::taxType,
            SetSelectedTaxData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
