package com.ultramega.taxes.container;

import com.ultramega.taxes.TaxTypes;
import com.ultramega.taxes.mixin.SlotAccessor;
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
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ultramega.taxes.gui.TaxScreen.BAR_HEIGHT;
import static com.ultramega.taxes.gui.TaxScreen.BAR_WIDTH;

public class TaxContainerMenu extends AbstractContainerMenu {
    private final LinkedHashMap<String, Double> miningTax;
    private final LinkedHashMap<String, Double> smeltingTax;
    private final LinkedHashMap<String, Double> tradingTax;

    private final Player player;
    private final List<Slot> taxSlots = new ArrayList<>();
    private final List<Integer> taxSlotsY = new ArrayList<>();

    private TaxTypes selectedTaxType = TaxTypes.MINING_TAX;

    public TaxContainerMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.TAX_CONTAINER_MENU.get(), containerId);
        this.player = playerInventory.player;
        this.miningTax = player.getData(ModAttachments.MINING_TAX.get());
        this.smeltingTax = player.getData(ModAttachments.SMELTING_TAX.get());
        this.tradingTax = player.getData(ModAttachments.TRADING_TAX.get());

        IItemHandler itemHandler = new InvWrapper(playerInventory);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(itemHandler, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(itemHandler, i, 8 + i * 18, 184));
        }

        addTaxSlots(getSelectedTaxData());
    }

    private void addTaxSlots(LinkedHashMap<String, Double> tax) {
        Container container = new SimpleContainer(tax.size());
        IItemHandler itemHandler = new InvWrapper(container);

        int validItemCount = 0;
        int yOffset = 0;

        for (String itemId : tax.keySet()) {
            int amount = tax.get(itemId).intValue();

            if (amount <= 0) {
                continue;
            }

            int x = 8 + 54 + (validItemCount % 2 == 0 ? 0 : BAR_WIDTH);
            int y = 18 + (BAR_HEIGHT * yOffset);

            taxSlots.add(addSlot(new SlotItemHandler(itemHandler, validItemCount, x, y)));
            taxSlotsY.add(y);

            validItemCount++;
            if (validItemCount % 2 == 0) {
                yOffset++;
            }
        }

        // Check if slots are offscreen
        updateTaxSlotsPos(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
        // TODO: Implement this, this shit fried my brain and I need a break, I've been coding for too many hours straight
        /*ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            int remaining = -1;

            LinkedHashMap<String, Double> tax = getSelectedTaxData();
            int i = 0;
            for (String taxItemId : tax.keySet()) {
                String invItemId = BuiltInRegistries.ITEM.getKey(slotStack.getItem()).toString();
                double taxAmount = tax.get(taxItemId);
                if (taxAmount >= 1 && taxItemId.equals(invItemId)) {
                    remaining = placeStackInTaxSlot(slotStack, i + 35, false);
                    break;
                }
                i++;
            }

            if (remaining == -1) {
                //if (!moveItemStackTo(slotStack, 0, 35, false)) {
                //    return ItemStack.EMPTY;
                //}
            } else if (remaining == 0) {
                itemStack = ItemStack.EMPTY;
            } else if (remaining > 0) {
                itemStack.setCount(remaining);
            }

            slot.setChanged();
            //slot.onTake(player, stackToMove);
            //this.broadcastChanges();
        }

        return itemStack;*/
    }

    public void updateTaxSlotsPos(int yScroll) {
        for (int i = 0; i < this.taxSlots.size(); i++) {
            Slot slot = this.taxSlots.get(i);
            int newY = this.taxSlotsY.get(i) - yScroll;

            // Check if slots are in bounds
            if (newY < 2) {
                newY -= 500;
            }
            if (newY > 110) {
                newY += 500;
            }

            ((SlotAccessor)slot).setY(newY);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
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

    public void setSelectedTaxType(TaxTypes selectedTaxType) {
        if (this.selectedTaxType != selectedTaxType) {
            this.selectedTaxType = selectedTaxType;
            updateTaxSlots();
        }
    }

    public void placeCarriedInTaxSlot(int index) {
        ItemStack carriedStack = getCarried();
        if (!carriedStack.isEmpty()) {
            placeStackInTaxSlot(carriedStack, index, true);
        }
    }

    private int placeStackInTaxSlot(ItemStack stack, int slotIndex, boolean carried) {
        LinkedHashMap<String, Double> tax = getSelectedTaxData();

        String taxItemId = tax.keySet().toArray()[slotIndex - 34].toString();
        Item taxItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(taxItemId));

        if (!stack.is(taxItem))
            return - 1;

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        double taxAmount = tax.get(itemId);
        double carriedSize = (double)stack.getCount() - taxAmount;

        ItemStack newCarriedStack = stack.copy();
        if (carried) {
            if ((int)carriedSize <= 0) {
                newCarriedStack = ItemStack.EMPTY;
            } else {
                newCarriedStack.setCount((int)Math.ceil(carriedSize));
            }
        }

        if ((int)carriedSize >= 0) {
            tax.remove(itemId);
        } else {
            tax.put(itemId, Math.abs(carriedSize));
        }

        updateAttachments();
        if (carried) {
            setCarried(newCarriedStack);
        }

        if ((int)carriedSize >= 0) {
            Slot removedSlot = this.slots.remove(slotIndex);
            int index = this.taxSlots.indexOf(removedSlot);
            this.taxSlots.remove(index);
            this.taxSlotsY.remove(index);
        }
        this.broadcastChanges();

        return (int)carriedSize;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 36) {
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> items, ItemStack carried) {
        // Prevent crash
        for(int i = 0; i < items.size(); ++i) {
            if (this.slots.size() > i) {
                this.getSlot(i).set(items.get(i));
            }
        }

        setCarried(carried);
        this.stateId = stateId;
    }

    private void updateTaxSlots() {
        this.slots.removeAll(taxSlots);
        taxSlots.clear();
        taxSlotsY.clear();
        addTaxSlots(getSelectedTaxData());
        this.broadcastChanges();
    }

    public TaxTypes getSelectedTaxType() {
        return selectedTaxType;
    }

    public Player getPlayer() {
        return player;
    }
}
