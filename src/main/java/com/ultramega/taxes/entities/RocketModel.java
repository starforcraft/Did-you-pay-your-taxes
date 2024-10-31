package com.ultramega.taxes.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class RocketModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart body;

    public RocketModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20).addBox(-0.7956F, -16.5F, -4.0F, 1.5913F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(36, 33).addBox(-4.0F, -16.5F, -0.7956F, 8.0F, 12.0F, 1.5913F, new CubeDeformation(0.0F))
                .texOffs(18, 40).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(26, 40).addBox(-2.0F, -5.0F, 1.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 44).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 46).addBox(1.0F, -5.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 48).addBox(-2.0F, -5.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 46).addBox(0.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -16.0F, -0.4F, 0.0F, 0.0F, 0.5672F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(30, 48).addBox(0.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -15.0F, -0.4F, 0.0F, 0.0F, -0.5672F));

        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(36, 46).addBox(0.0F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, -16.0F, -3.0F, 0.5672F, 0.0F, 0.0F));

        PartDefinition cube_r4 = body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(18, 46).addBox(0.0F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, -16.0F, 3.0F, -0.5672F, 0.0F, 0.0F));

        PartDefinition hexadecagon_r1 = body.addOrReplaceChild("hexadecagon_r1", CubeListBuilder.create().texOffs(0, 40).addBox(-4.0F, -11.5F, -0.7956F, 8.0F, 12.0F, 1.5913F, new CubeDeformation(0.0F))
                .texOffs(18, 20).addBox(-0.7956F, -11.5F, -4.0F, 1.5913F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

        PartDefinition hexadecagon_r2 = body.addOrReplaceChild("hexadecagon_r2", CubeListBuilder.create().texOffs(36, 20).addBox(-4.0F, -11.5F, -0.7956F, 8.0F, 12.0F, 1.5913F, new CubeDeformation(0.0F))
                .texOffs(18, 0).addBox(-0.7956F, -11.5F, -4.0F, 1.5913F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

        PartDefinition hexadecagon_r3 = body.addOrReplaceChild("hexadecagon_r3", CubeListBuilder.create().texOffs(36, 0).addBox(-0.7956F, -11.5F, -4.0F, 1.5913F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition hexadecagon_r4 = body.addOrReplaceChild("hexadecagon_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-0.7956F, -11.5F, -4.0F, 1.5913F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
