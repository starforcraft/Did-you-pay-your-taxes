package com.ultramega.taxes.container;

import com.ultramega.taxes.TaxTypes;
import com.ultramega.taxes.network.SyncTaxData;
import com.ultramega.taxes.registry.ModAttachments;
import com.ultramega.taxes.registry.ModMenuTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaxContainerMenu extends AbstractContainerMenu {
    private final LinkedHashMap<String, Double> miningTax;
    private final LinkedHashMap<String, Integer> miningTaxInt;
    private final LinkedHashMap<String, Double> smeltingTax;
    private final LinkedHashMap<String, Integer> smeltingTaxInt;
    private final LinkedHashMap<String, Double> tradingTax;
    private final LinkedHashMap<String, Integer> tradingTaxInt;

    private final Player player;
    private TaxTypes selectedTaxType = TaxTypes.MINING_TAX;

    private Runnable slotUpdateListener;

    public TaxContainerMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.TAX_CONTAINER_MENU.get(), containerId);
        this.player = playerInventory.player;
        this.miningTax = player.getData(ModAttachments.MINING_TAX.get());
        this.miningTaxInt = transformTax(miningTax);
        this.smeltingTax = player.getData(ModAttachments.SMELTING_TAX.get());
        this.smeltingTaxInt = transformTax(smeltingTax);
        this.tradingTax = player.getData(ModAttachments.TRADING_TAX.get());
        this.tradingTaxInt = transformTax(tradingTax);
        this.slotUpdateListener = () -> {
        };

        IItemHandler itemHandler = new InvWrapper(playerInventory);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(itemHandler, j + i * 9 + 9, 8 + j * 18, 111 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(itemHandler, i, 8 + i * 18, 169));
        }

        Container container = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                slotsChanged(this);
                slotUpdateListener.run();
            }
        };
        addSlot(new SlotItemHandler(new InvWrapper(container), 0, 150, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (!super.mayPlace(stack)) {
                    return false;
                }

                return getSelectedTaxDataIntCopy().keySet().stream()
                        .map(ResourceLocation::parse)
                        .map(BuiltInRegistries.ITEM::get)
                        .anyMatch(stack::is);
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index <= 35) {
                if (!this.moveItemStackTo(slotStack, 36, 37, false) && this.slots.get(36).mayPlace(slotStack)) {
                    return ItemStack.EMPTY;
                } else if (index == 0) {
                    if (!this.moveItemStackTo(slotStack, 2, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index == 1) {
                    if (!this.moveItemStackTo(slotStack, 2, 36, true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(slotStack, 29, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && !this.moveItemStackTo(slotStack, 0, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
            this.broadcastChanges();
        }

        return itemStack;
    }

    private void updateAttachments() {
        PacketDistributor.sendToServer(new SyncTaxData(getSelectedTaxData(), selectedTaxType));
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncTaxData(getSelectedTaxData(), selectedTaxType));
        }
    }

    public LinkedHashMap<String, Double> getSelectedTaxData() {
        return switch (selectedTaxType) {
            case MINING_TAX -> miningTax;
            case SMELTING_TAX -> smeltingTax;
            case TRADING_TAX -> tradingTax;
            default -> null;
        };
    }

    public LinkedHashMap<String, Integer> getSelectedTaxDataIntCopy() {
        return switch (selectedTaxType) {
            case MINING_TAX -> miningTaxInt;
            case SMELTING_TAX -> smeltingTaxInt;
            case TRADING_TAX -> tradingTaxInt;
            default -> null;
        };
    }

    public void setSelectedTaxType(TaxTypes selectedTaxType) {
        this.selectedTaxType = selectedTaxType;
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack stack = container.getItem(0);
        if (!stack.isEmpty()) {
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            int stackAmount = stack.getCount();

            LinkedHashMap<String, Double> tax = getSelectedTaxData();
            LinkedHashMap<String, Integer> taxInt = getSelectedTaxDataIntCopy();
            double currentTaxAmount = tax.getOrDefault(itemId, 0.0);

            double updatedTaxAmount = (int)currentTaxAmount - stackAmount;
            if ((int)updatedTaxAmount <= 0) {
                tax.remove(itemId);
                taxInt.remove(itemId);
            } else {
                tax.put(itemId, updatedTaxAmount);
                taxInt.put(itemId, (int)updatedTaxAmount);
            }

            updateAttachments();

            container.removeItem(0, (int)currentTaxAmount);
            this.clearContainer(player, container);
        }

        super.slotsChanged(container);
    }

    private LinkedHashMap<String, Integer> transformTax(LinkedHashMap<String, Double> tax) {
        LinkedHashMap<String, Integer> intTax = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : tax.entrySet()) {
            int amount = entry.getValue().intValue();

            if (amount > 0) {
                intTax.put(entry.getKey(), amount);
            }
        }

        return intTax;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void registerUpdateListener(Runnable listener) {
        this.slotUpdateListener = listener;
    }

    public TaxTypes getSelectedTaxType() {
        return selectedTaxType;
    }

    public Player getPlayer() {
        return player;
    }
}
