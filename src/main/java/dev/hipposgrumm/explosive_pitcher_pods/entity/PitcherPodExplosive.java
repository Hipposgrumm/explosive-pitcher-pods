package dev.hipposgrumm.explosive_pitcher_pods.entity;

import dev.hipposgrumm.explosive_pitcher_pods.ExplosivePitcherPodsMain;
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

    public PitcherPodExplosive(double x, double y, double z, Level level) {
        super(ExplosivePitcherPodsMain.PITCHER_POD.get(), x, y, z, level);
    }

    public PitcherPodExplosive(LivingEntity entity, Level level) {
        super(ExplosivePitcherPodsMain.PITCHER_POD.get(), entity, level);
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
            if (this.isInWater()) {
                spawnAtLocation(getItem());
                discard();
            } else {
                this.explode();
            }
        }
        if (this.getDeltaMovement().y>=-2) age++;
        if (this.isInWater()) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, 0.1, vec3.z);
        }
        if (this.isInLava()) this.explode();
    }
}
