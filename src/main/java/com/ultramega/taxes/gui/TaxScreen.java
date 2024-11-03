package com.ultramega.taxes.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultramega.taxes.TaxTypes;
import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.container.TaxContainerMenu;
import com.ultramega.taxes.network.SetSelectedTaxData;
import com.ultramega.taxes.registry.ModAttachments;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedHashMap;

public class TaxScreen extends AbstractContainerScreen<TaxContainerMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "textures/gui/tax_screen.png");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller_disabled");

    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_FULL_HEIGHT = 80;

    private static final int TAX_LENGTH = 6;
    private static final int TAX_HEIGHT = 4;
    private static final int TAX_AMOUNT = TAX_LENGTH * TAX_HEIGHT;

    public static final int BAR_SIZE = 20;

    private LinkedHashMap<String, Integer> selectedTax;

    private final TaxContainerMenu containerMenu;
    private final Player player;

    private float scrollOffs;
    private boolean scrolling;

    private int startIndex;

    public TaxScreen(TaxContainerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, Component.empty());
        this.menu.registerUpdateListener(this::containerChanged);
        this.selectedTax = menu.getSelectedTaxDataIntCopy();
        this.containerMenu = menu;
        this.player = menu.getPlayer();
        this.imageWidth = 176;
        this.imageHeight = 193;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    protected void init() {
        super.init();

        assert this.minecraft != null;

        this.addRenderableWidget(new SideButton(Items.DIAMOND_PICKAXE.getDefaultInstance(), TaxTypes.MINING_TAX, leftPos - 20, topPos + 5, (context) ->
                pressedSideButton(TaxTypes.MINING_TAX)));
        this.addRenderableWidget(new SideButton(Items.FURNACE.getDefaultInstance(), TaxTypes.SMELTING_TAX, leftPos - 20, topPos + 5 + 20, (context) ->
                pressedSideButton(TaxTypes.SMELTING_TAX)));
        this.addRenderableWidget(new SideButton(Items.EMERALD.getDefaultInstance(), TaxTypes.TRADING_TAX, leftPos - 20, topPos + 5 + 40, (context) ->
                pressedSideButton(TaxTypes.TRADING_TAX)));
    }

    private void pressedSideButton(TaxTypes taxType) {
        PacketDistributor.sendToServer(new SetSelectedTaxData(taxType));
        containerMenu.setSelectedTaxType(taxType);

        selectedTax = containerMenu.getSelectedTaxDataIntCopy();
        this.containerChanged();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());

        graphics.blitSprite(isScrollBarActive() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE, leftPos + 132, topPos + SCROLLER_HEIGHT + 1 + (int)((SCROLLER_FULL_HEIGHT - SCROLLER_HEIGHT) * this.scrollOffs), SCROLLER_WIDTH, SCROLLER_HEIGHT);

        int xStart = this.leftPos + 8;
        int yStart = this.topPos + 14;
        int maxIndex = this.startIndex + TAX_AMOUNT;
        renderTaxButtons(graphics, mouseX, mouseY, xStart, yStart, maxIndex);
        renderTaxItems(graphics, xStart, yStart, maxIndex);
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

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        int xStart = this.leftPos + 8;
        int yStart = this.topPos + 14;
        int maxIndex = this.startIndex + TAX_AMOUNT;

        for (int i = this.startIndex; i < maxIndex && i < selectedTax.size(); i++) {
            int currentIndex = i - this.startIndex;
            int barX = xStart + currentIndex % TAX_LENGTH * BAR_SIZE;
            int barY = yStart + currentIndex / TAX_LENGTH * BAR_SIZE + 3;
            if (x >= barX && x < barX + BAR_SIZE && y >= barY && y < barY + BAR_SIZE) {
                String itemId = selectedTax.keySet().toArray()[i].toString();
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
                guiGraphics.renderTooltip(this.font, stack, x, y);
            }
        }
    }

    private void renderTaxButtons(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int lastVisibleElementIndex) {
        for (int i = this.startIndex; i < lastVisibleElementIndex && i < selectedTax.size(); i++) {
            int currentIndex = i - this.startIndex;
            int barX = x + currentIndex % TAX_LENGTH * BAR_SIZE;
            int barY = y + (currentIndex / TAX_LENGTH) * BAR_SIZE + 3;

            boolean isHovering = mouseX >= barX && mouseY >= barY && mouseX < barX + BAR_SIZE && mouseY < barY + BAR_SIZE;
            graphics.blit(BACKGROUND, barX, barY - 1, 176, isHovering ? BAR_SIZE : 0, BAR_SIZE, BAR_SIZE);
        }
    }

    private void renderTaxItems(GuiGraphics graphics, int x, int y, int startIndex) {
        PoseStack pose = graphics.pose();

        for (int i = this.startIndex; i < startIndex && i < selectedTax.size(); i++) {
            int currentIndex = i - this.startIndex;
            int barX = x + currentIndex % TAX_LENGTH * BAR_SIZE + 2;
            int barY = y + (currentIndex / TAX_LENGTH) * BAR_SIZE + 4;

            String itemId = selectedTax.keySet().toArray()[i].toString();
            String amount = String.valueOf(selectedTax.get(itemId));

            graphics.renderItem(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId))), barX, barY);
            pose.pushPose();
            pose.translate(0, 0, 399.0F);
            graphics.drawString(this.font, amount, barX + 17 - font.width(amount), barY + 9, 16777215, true);
            pose.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int x = this.leftPos + 132;
        int y = this.topPos + 9;
        if (mouseX >= (double)x && mouseX < (double)(x + 12) && mouseY >= (double)y && mouseY < (double)(y + SCROLLER_FULL_HEIGHT + 8)) {
            this.scrolling = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && isScrollBarActive()) {
            int yStart = this.topPos + 14;
            this.scrollOffs = ((float)mouseY - (float)yStart - 7.5F) / ((float)((yStart + SCROLLER_FULL_HEIGHT) - yStart) - 16.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5) * TAX_LENGTH;
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.isScrollBarActive()) {
            int offscreenRows = this.getOffscreenRows();
            float scrollIncrement = (float)scrollY / (float)offscreenRows;
            this.scrollOffs = Mth.clamp(this.scrollOffs - scrollIncrement, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)offscreenRows) + 0.5) * TAX_LENGTH;
        }

        return true;
    }

    private boolean isScrollBarActive() {
        return selectedTax.size() > TAX_AMOUNT;
    }

    protected int getOffscreenRows() {
        return (selectedTax.size() + TAX_LENGTH - 1) / TAX_LENGTH - TAX_HEIGHT;
    }

    private void containerChanged() {
        this.scrollOffs = 0.0F;
        this.startIndex = 0;
    }
}
