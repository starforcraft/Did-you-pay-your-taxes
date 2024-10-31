package com.ultramega.taxes.entities;

import com.ultramega.taxes.registry.ModEntityTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class IRSEntity extends Monster {
    private static final EntityDataAccessor<Boolean> DATA_ATTACK_PLAYER = SynchedEntityData.defineId(IRSEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_AIR_STRIKE = SynchedEntityData.defineId(IRSEntity.class, EntityDataSerializers.BOOLEAN);

    public IRSEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.4D, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && isAttackingPlayer();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isAttackingPlayer();
            }
        });
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // TODO: Follow player and ask him why he didn't pay taxes
        //this.goalSelector.addGoal(2, new FollowMobGoal(this, 1.2D, 1.0F, 32.0F));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new IRSHurtByOtherGoal(this).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (isAttackingPlayer() && isAirStrikeAllowed() && random.nextInt(300) == 0) {
            // Spawn airstrike
            int x = random.nextInt(10);
            int z = random.nextInt(10);
            for (int i = 0; i < 4; i++) {
                Entity entity = new RocketEntity(ModEntityTypes.ROCKET_ENTITY.get(), level());
                entity.setPos(getX() + x + i * 4, getY() + 60, getZ() + z + i * 4);
                level().addFreshEntity(entity);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 4.0);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player) {
            setAttackPlayer(true);
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ATTACK_PLAYER, false);
        builder.define(DATA_AIR_STRIKE, false);
    }

    public boolean isAttackingPlayer() {
        return this.getEntityData().get(DATA_ATTACK_PLAYER);
    }

    public void setAttackPlayer(boolean attackPlayer) {
        this.getEntityData().set(DATA_ATTACK_PLAYER, attackPlayer);
    }

    public boolean isAirStrikeAllowed() {
        return this.getEntityData().get(DATA_AIR_STRIKE);
    }

    public void allowSpawningAirstrike() {
        this.getEntityData().set(DATA_AIR_STRIKE, true);
    }

    static class IRSHurtByOtherGoal extends HurtByTargetGoal {
        IRSHurtByOtherGoal(IRSEntity mob) {
            super(mob);
        }

        @Override
        protected void alertOthers() {
            double followDistance = this.getFollowDistance();
            AABB detectionArea = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(followDistance, 100.0, followDistance);

            List<? extends Mob> nearbyMobs = this.mob.level().getEntitiesOfClass(this.mob.getClass(), detectionArea, EntitySelector.NO_SPECTATORS);

            for (Mob nearbyMob : nearbyMobs) {
                if (nearbyMob == this.mob) {
                    continue;
                }

                alertOther(nearbyMob, this.mob.getLastHurtByMob());
            }
        }

        @Override
        protected void alertOther(Mob mob, LivingEntity target) {
            if (mob instanceof IRSEntity entity) {
                entity.setTarget(target);
                entity.setAttackPlayer(true);
            }
        }
    }
}
