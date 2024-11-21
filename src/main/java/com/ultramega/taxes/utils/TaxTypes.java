package com.ultramega.taxes.utils;

import net.minecraft.network.chat.Component;

public enum TaxTypes {
    MINING_TAX("gui.taxes.mining_tax"),
    SMELTING_TAX("gui.taxes.smelting_tax"),
    TRADING_TAX("gui.taxes.trading_tax"),
    TAX_DAYS_LEFT(""),
    TAX_RATE("");

    private final String translation;

    TaxTypes(String translation) {
        this.translation = translation;
    }

    public Component getTranslation() {
        return Component.translatable(translation);
    }
}
