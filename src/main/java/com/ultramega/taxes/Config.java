package com.ultramega.taxes;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Taxes.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MIN_TAX_RATE = BUILDER
            .comment("The minimum tax rate (has to be smaller than maximum)")
            .defineInRange("minTaxRate", 10, 0, 100);
    private static final ModConfigSpec.IntValue MAX_TAX_RATE = BUILDER
            .comment("The maximum tax rate (has to be bigger than minimum)")
            .defineInRange("maxTaxRate", 42, 0, 100);

    // TODO: find better name
    private static final ModConfigSpec.IntValue TAX_PAYOUT_DAYS = BUILDER
            .comment("The amount of days for a payout")
            .defineInRange("taxPayoutDays", 30, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue SPAWN_IRS = BUILDER
            .comment("Spawn IRS if taxes are not paid")
            .define("spawnIRS", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int minTaxRate;
    public static int maxTaxRate;

    public static int taxPayoutDays;

    public static boolean spawnIRS;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        minTaxRate = MIN_TAX_RATE.get();
        maxTaxRate = MAX_TAX_RATE.get();
        taxPayoutDays = TAX_PAYOUT_DAYS.get();
        spawnIRS = SPAWN_IRS.get();
    }
}
