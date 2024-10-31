package com.ultramega.taxes.entities;

import com.ultramega.taxes.Taxes;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class RocketRenderer extends LivingEntityRenderer<RocketEntity, RocketModel<RocketEntity>> {
    public static final ModelLayerLocation ROCKET_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "rocket"), "main");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Taxes.MODID, "textures/entity/rocket.png");

    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context, new RocketModel<>(context.bakeLayer(ROCKET_LAYER)),0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(RocketEntity rocketEntity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(RocketEntity entity) {
        return false;
    }
}
