package com.ultramega.taxes.utils;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.LinkedHashMap;

public class ModMaths {
    public static float randomOffset(RandomSource random, float range) {
        return Mth.nextFloat(random, -range, range);
    }

    public static final Codec<LinkedHashMap<String, Double>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.DOUBLE)
            .xmap(LinkedHashMap::new, map -> map);

    public static final StreamCodec<ByteBuf, LinkedHashMap<String, Double>> STREAM_CODEC = ByteBufCodecs.map(
            LinkedHashMap::new,
            ByteBufCodecs.STRING_UTF8,
            ByteBufCodecs.DOUBLE,
            256
    );
}
