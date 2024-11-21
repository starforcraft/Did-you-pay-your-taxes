package com.ultramega.taxes.gui;

import com.ultramega.taxes.utils.TaxTypes;
import com.ultramega.taxes.Taxes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SideButton extends Button {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "textures/gui/tax_screen.png");
    private static final int WIDTH = 18;
    private static final int HEIGHT = 18;

    private final ItemStack displayStack;
    private final TaxTypes taxType;

    public SideButton(ItemStack displayStack, TaxTypes taxType, int x, int y, OnPress onPress) {
        super(Button.builder(Component.empty(), onPress).pos(x, y).size(WIDTH, HEIGHT));
        this.displayStack = displayStack;
        this.taxType = taxType;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(BACKGROUND, getX(), getY(), 176, isHovered ? 58 : 40, WIDTH, HEIGHT);
        graphics.renderItem(displayStack, getX() + 1, getY() + 1);

        if (isHovered) {
            graphics.renderTooltip(Minecraft.getInstance().font, taxType.getTranslation(), mouseX, mouseY);
        }
    }
}
