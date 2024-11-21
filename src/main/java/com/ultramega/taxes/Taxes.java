package com.ultramega.taxes;

import com.ultramega.taxes.entities.IRSRenderer;
import com.ultramega.taxes.entities.RocketModel;
import com.ultramega.taxes.entities.RocketRenderer;
import com.ultramega.taxes.gui.TaxScreen;
import com.ultramega.taxes.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(Taxes.MODID)
public class Taxes {
    public static final String MODID = "taxes";

    public Taxes(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModEntityTypes.ENTITY_TYPE.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);

        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerEntityRenderers);
        modEventBus.addListener(this::registerEntityLayers);
    }

    private void registerScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.TAX_CONTAINER_MENU.get(), TaxScreen::new);
    }

    private void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.IRS_ENTITY.get(), IRSRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.ROCKET_ENTITY.get(), RocketRenderer::new);
    }

    private void registerEntityLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RocketRenderer.ROCKET_LAYER, RocketModel::createBodyLayer);
    }
}
