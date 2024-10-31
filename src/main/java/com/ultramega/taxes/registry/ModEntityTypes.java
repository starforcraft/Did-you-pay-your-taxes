package com.ultramega.taxes.registry;

import com.ultramega.taxes.Taxes;
import com.ultramega.taxes.entities.IRSEntity;
import com.ultramega.taxes.entities.RocketEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Taxes.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Taxes.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<IRSEntity>> IRS_ENTITY = ENTITY_TYPE.register("irs", () -> EntityType.Builder.of(IRSEntity::new, MobCategory.MONSTER)
            .sized(0.8F, 1.95F)
            .eyeHeight(1.7F)
            .clientTrackingRange(8)
            .fireImmune()
            .build(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "irs").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<RocketEntity>> ROCKET_ENTITY = ENTITY_TYPE.register("rocket", () -> EntityType.Builder.of(RocketEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.15F)
            .eyeHeight(0.1F)
            .clientTrackingRange(8)
            .build(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "rocket").toString()));

    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(IRS_ENTITY.get(), IRSEntity.createAttributes().build());
        event.put(ROCKET_ENTITY.get(), RocketEntity.createAttributes().build());
    }
}
