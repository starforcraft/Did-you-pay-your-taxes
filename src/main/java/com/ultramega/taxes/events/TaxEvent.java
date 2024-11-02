package com.ultramega.taxes.events;

import com.ultramega.taxes.TaxTypes;
import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.entities.IRSEntity;
import com.ultramega.taxes.network.AddTaxData;
import com.ultramega.taxes.network.SyncIntTaxData;
import com.ultramega.taxes.registry.ModAttachments;
import com.ultramega.taxes.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedHashMap;
import java.util.List;

@EventBusSubscriber(modid = Taxes.MODID, bus = EventBusSubscriber.Bus.GAME)
public class TaxEvent {
    @SubscribeEvent
    public static void onBlockBreak(final BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            LootParams.Builder builder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(event.getPos()))
                    .withParameter(LootContextParams.TOOL, player.getMainHandItem());

            List<ItemStack> drops = event.getState().getDrops(builder);

            for (ItemStack drop : drops) {
                sendTax(player, drop.getItem(), drop.getCount(), TaxTypes.MINING_TAX);
            }
        }
    }

    @SubscribeEvent
    public static void onItemSmelted(final PlayerEvent.ItemSmeltedEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getSmelting();

        sendTax(player, itemStack.getItem(), itemStack.getCount(), TaxTypes.SMELTING_TAX);
    }

    @SubscribeEvent
    public static void onItemTraded(final TradeWithVillagerEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getMerchantOffer().getResult();

        sendTax(player, itemStack.getItem(), itemStack.getCount(), TaxTypes.TRADING_TAX);
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(final PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();
        long dayTime = level.getDayTime();
        if (dayTime % 24000 == 0) {
            int daysUntilTaxPayDay = player.getData(ModAttachments.TAX_DAYS_LEFT.get());

            daysUntilTaxPayDay--;

            if (daysUntilTaxPayDay == 0) {
                daysUntilTaxPayDay = 30;

                int taxRate = player.getData(ModAttachments.TAX_RATE.get());
                player.setData(ModAttachments.TAX_RATE.get(), Math.min(42, taxRate + 1));

                LinkedHashMap<String, Double> miningTax = player.getData(ModAttachments.MINING_TAX.get());
                LinkedHashMap<String, Double> smeltingTax = player.getData(ModAttachments.SMELTING_TAX.get());
                LinkedHashMap<String, Double> tradingTax = player.getData(ModAttachments.TRADING_TAX.get());

                boolean hasPaidAllTaxes = areAllTaxesPaid(miningTax) && areAllTaxesPaid(smeltingTax) && areAllTaxesPaid(tradingTax);

                if (!level.isClientSide && !hasPaidAllTaxes) {
                    spawnEnemyWave(level, player.blockPosition(), taxRate);
                }
            }

            player.setData(ModAttachments.TAX_DAYS_LEFT.get(), daysUntilTaxPayDay);
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(final EntityJoinLevelEvent event) {
        // Sync attachments to client
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncTaxWithClient(serverPlayer.getData(ModAttachments.MINING_TAX.get()), serverPlayer, TaxTypes.MINING_TAX);
            syncTaxWithClient(serverPlayer.getData(ModAttachments.SMELTING_TAX.get()), serverPlayer, TaxTypes.SMELTING_TAX);
            syncTaxWithClient(serverPlayer.getData(ModAttachments.TRADING_TAX.get()), serverPlayer, TaxTypes.TRADING_TAX);

            PacketDistributor.sendToPlayer(serverPlayer, new SyncIntTaxData(serverPlayer.getData(ModAttachments.TAX_DAYS_LEFT.get()), TaxTypes.TAX_DAYS_LEFT));
            PacketDistributor.sendToPlayer(serverPlayer, new SyncIntTaxData(serverPlayer.getData(ModAttachments.TAX_RATE.get()), TaxTypes.TAX_RATE));
        }
    }

    private static void spawnEnemyWave(Level level, BlockPos playerPos, int taxRate) {
        int radius = 15;
        double angleCount = Math.toRadians((float) 360 / taxRate);
        for (int i = 0; i < taxRate; i++) {
            double angle = angleCount * (i + 1);
            double x = radius * Math.sin(angle);
            double z = radius * Math.cos(angle);

            IRSEntity entity = new IRSEntity(ModEntityTypes.IRS_ENTITY.get(), level);
            if (taxRate > 20) {
                entity.allowSpawningAirstrike();
            }
            entity.setPos(playerPos.getX() + x, playerPos.getY(), playerPos.getZ() + z);
            entity.setAttackPlayer(true);
            level.addFreshEntity(entity);
        }
    }

    private static void sendTax(Player player, Item item, int amount, TaxTypes taxType) {
        int taxRate = player.getData(ModAttachments.TAX_RATE.get());
        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();

        PacketDistributor.sendToServer(new AddTaxData(itemId, amount, taxRate, taxType));
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new AddTaxData(itemId, amount, taxRate, taxType));
        }
    }

    private static void syncTaxWithClient(LinkedHashMap<String, Double> tax, ServerPlayer serverPlayer, TaxTypes taxType) {
        for (String itemId : tax.keySet()) {
            double amount = tax.get(itemId);
            PacketDistributor.sendToPlayer(serverPlayer, new AddTaxData(itemId, amount, 100, taxType));
        }
    }

    private static boolean areAllTaxesPaid(LinkedHashMap<String, Double> taxMap) {
        for (Double tax : taxMap.values()) {
            if (tax >= 1) {
                return false;
            }
        }
        return true;
    }
}
