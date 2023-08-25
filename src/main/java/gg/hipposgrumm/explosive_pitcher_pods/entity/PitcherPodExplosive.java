package gg.hipposgrumm.explosive_pitcher_pods.entity;

import gg.hipposgrumm.explosive_pitcher_pods.ExplosivePitcherPods;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PitcherPodExplosive extends ThrowableItemProjectile {
    public float tiltX = 0;
    public float tiltY = 0;
    public float tiltZ = 0;
    private int age = 0;

    public PitcherPodExplosive(EntityType<PitcherPodExplosive> type, Level level) {
        super(type,level);
    }

    public PitcherPodExplosive(double p_37433_, double p_37434_, double p_37435_, Level p_37436_) {
        super(ExplosivePitcherPods.PITCHER_POD.get(), p_37433_, p_37434_, p_37435_, p_37436_);
    }

    public PitcherPodExplosive(LivingEntity p_37439_, Level p_37440_) {
        super(ExplosivePitcherPods.PITCHER_POD.get(), p_37439_, p_37440_);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.PITCHER_PLANT;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        this.explode();
    }

    protected void explode() {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.level().explode(this, level().damageSources().explosion(this.getOwner(), this.getOwner()), null, this.getOnPos().getCenter(), 1.5F, false, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 inertia = this.getDeltaMovement();
        tiltX += inertia.x;
        tiltY += inertia.y;
        tiltZ += inertia.z;
        if (age >= 100) {
            this.explode();
        }
        if (this.getDeltaMovement().y>=-2) age++;
        if (this.isInWater()) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, 0.1, vec3.z);
        }
        if (this.isInLava()) this.explode();
    }
}
