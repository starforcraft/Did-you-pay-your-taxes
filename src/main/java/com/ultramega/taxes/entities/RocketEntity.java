package com.ultramega.taxes.entities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RocketEntity extends Monster {
    private final int explosionAmount;
    private int currentExplosion;

    public RocketEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.explosionAmount = random.nextInt(3, 10);
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
        if (!level().getBlockState(blockPosition().below()).isEmpty()) {
            explode(null);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public void die(DamageSource damageSource) {
        explode(damageSource);
        super.die(damageSource);
    }

    private void explode(DamageSource damageSource) {
        currentExplosion++;
        level().explode(this, damageSource, null, this.getX(), this.getY(), this.getZ(), 8.0F, true, Level.ExplosionInteraction.MOB);

        if (currentExplosion > explosionAmount) {
            this.remove(RemovalReason.KILLED);
        }
    }
}
