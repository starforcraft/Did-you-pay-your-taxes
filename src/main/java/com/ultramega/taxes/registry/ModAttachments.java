package com.ultramega.taxes.registry;

import com.mojang.serialization.Codec;
import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.utils.ModCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Taxes.MODID);

    public static final Supplier<AttachmentType<Integer>> TAX_DAYS_LEFT = ATTACHMENT_TYPES.register(
            "tax_days_left", () -> AttachmentType.builder(() -> 30).serialize(Codec.INT).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<Integer>> TAX_RATE = ATTACHMENT_TYPES.register(
            "tax_rate", () -> AttachmentType.builder(() -> 10).serialize(Codec.INT).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<LinkedHashMap<String, Double>>> MINING_TAX = ATTACHMENT_TYPES.register(
            "mining_tax", () -> AttachmentType.builder(() -> new LinkedHashMap<String, Double>()).serialize(ModCodecs.CODEC).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<LinkedHashMap<String, Double>>> SMELTING_TAX = ATTACHMENT_TYPES.register(
            "smelting_tax", () -> AttachmentType.builder(() -> new LinkedHashMap<String, Double>()).serialize(ModCodecs.CODEC).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<LinkedHashMap<String, Double>>> TRADING_TAX = ATTACHMENT_TYPES.register(
            "trading_tax", () -> AttachmentType.builder(() -> new LinkedHashMap<String, Double>()).serialize(ModCodecs.CODEC).copyOnDeath().build()
    );
}
