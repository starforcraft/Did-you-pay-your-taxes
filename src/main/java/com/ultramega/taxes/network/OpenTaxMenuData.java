package com.ultramega.taxes.network;

import com.ultramega.taxes.Taxes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenTaxMenuData() implements CustomPacketPayload {
    public static final Type<OpenTaxMenuData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "open_tax_menu_data"));
    public static final StreamCodec<ByteBuf, OpenTaxMenuData> STREAM_CODEC = StreamCodec.unit(new OpenTaxMenuData());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
