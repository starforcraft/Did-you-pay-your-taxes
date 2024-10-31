package com.ultramega.taxes.gui;

import com.ultramega.taxes.TaxTypes;
import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.container.TaxContainerMenu;
import com.ultramega.taxes.network.UpdateTaxSlotsData;
import com.ultramega.taxes.registry.ModAttachments;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedHashMap;

public class TaxScreen extends AbstractContainerScreen<TaxContainerMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "textures/gui/tax_screen.png");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller");

    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_FULL_HEIGHT = 95;

    public static final int BAR_WIDTH = 72;
    public static final int BAR_HEIGHT = 20;

    private final TaxContainerMenu containerMenu;
    private final Player player;

    private float scrollOffs;
    private boolean scrolling;

    public TaxScreen(TaxContainerMenu containerMenu, Inventory inventory, Component component) {
        super(containerMenu, inventory, Component.empty());
        this.containerMenu = containerMenu;
        this.player = containerMenu.getPlayer();
        this.imageWidth = 176;
        this.imageHeight = 208;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        assert this.minecraft != null;

        this.addRenderableWidget(new SideButton(Items.DIAMOND_PICKAXE.getDefaultInstance(), TaxTypes.MINING_TAX, leftPos - 20, topPos + 5, (context) -> {
            scrollOffs = 0.0F;
            containerMenu.setSelectedTaxType(TaxTypes.MINING_TAX);
        }));
        this.addRenderableWidget(new SideButton(Items.FURNACE.getDefaultInstance(), TaxTypes.SMELTING_TAX, leftPos - 20, topPos + 5 + 20, (context) -> {
            scrollOffs = 0.0F;
            containerMenu.setSelectedTaxType(TaxTypes.SMELTING_TAX);
        }));
        this.addRenderableWidget(new SideButton(Items.EMERALD.getDefaultInstance(), TaxTypes.TRADING_TAX, leftPos - 20, topPos + 5 + 40, (context) -> {
            scrollOffs = 0.0F;
            containerMenu.setSelectedTaxType(TaxTypes.TRADING_TAX);
        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());

        graphics.blitSprite(SCROLLER_SPRITE, leftPos + 156, topPos + SCROLLER_HEIGHT + 1 + (int)(80.0F * this.scrollOffs), SCROLLER_WIDTH, SCROLLER_HEIGHT);

        renderTaxes(graphics, containerMenu.getSelectedTaxData(), mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, containerMenu.getSelectedTaxType().getTranslation(), titleLabelX, titleLabelY, 4210752, false);

        int taxRate = player.getData(ModAttachments.TAX_RATE.get());
        graphics.drawString(this.font, taxRate + "%", titleLabelX + 70, titleLabelY, 4210752, false);

        int daysLeft = player.getData(ModAttachments.TAX_DAYS_LEFT.get());
        String daysLeftText = daysLeft + " " + Component.translatable("gui.taxes.day" + (daysLeft == 1 ? "" : "s") + "_left").getString();
        graphics.drawString(this.font, daysLeftText, titleLabelX + 100, titleLabelY, 4210752, false);
    }

    private void renderTaxes(GuiGraphics graphics, LinkedHashMap<String, Double> tax, int mouseX, int mouseY) {
        int yOffset = 0;
        int xOffset = 0;

        graphics.enableScissor(getGuiLeft(), getGuiTop() + 16, getGuiLeft() + width, getGuiTop() + 111);

        for (String itemId : tax.keySet()) {
            int amount = tax.get(itemId).intValue();

            if (amount <= 0) {
                continue;
            }

            int x = getGuiLeft() + 8 + xOffset;
            int y = getGuiTop() + 16 + (BAR_HEIGHT * yOffset) - (int)(80.0F * this.scrollOffs);

            boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + BAR_WIDTH && mouseY < y + BAR_HEIGHT;

            graphics.blit(BACKGROUND, x, y, 176, isHovering ? BAR_HEIGHT : 0, BAR_WIDTH, BAR_HEIGHT);
            graphics.renderItem(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId))), x + 2, y + 2);
            graphics.blit(BACKGROUND, x + 53, y + 1, 7, 125, 18, 18);
            graphics.drawString(this.font, String.valueOf(amount), x + BAR_HEIGHT, y + 10, 16777215, true);

            if (xOffset == 0) {
                xOffset = BAR_WIDTH;
            } else {
                xOffset = 0;
                yOffset++;
            }
        }

        graphics.disableScissor();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = findSlot(mouseX, mouseY);
        if (slot != null && slot.index >= 36) {
            containerMenu.placeCarriedInTaxSlot(slot.index);
            PacketDistributor.sendToServer(new UpdateTaxSlotsData(slot.index));
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int x = this.leftPos + 156;
        int y = this.topPos + 9;
        if (mouseX >= (double)x && mouseX < (double)(x + 12) && mouseY >= (double)y && mouseY < (double)(y + SCROLLER_FULL_HEIGHT + 8)) {
            this.scrolling = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling) {
            int i = this.topPos + 14;
            this.scrollOffs = ((float)mouseY - (float)i - 7.5F) / ((float)((i + SCROLLER_FULL_HEIGHT) - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            containerMenu.updateTaxSlotsPos((int)(80.0F * this.scrollOffs));
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
