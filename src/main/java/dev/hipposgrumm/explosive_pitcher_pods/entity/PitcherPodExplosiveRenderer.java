package dev.hipposgrumm.explosive_pitcher_pods.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class PitcherPodExplosiveRenderer<T extends PitcherPodExplosive> extends ThrownItemRenderer<T> {
    public PitcherPodExplosiveRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, PoseStack poseStack, MultiBufferSource shaderSource, int light) {
        super.render(entity, yaw, tickDelta, poseStack, shaderSource, light);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(entity.tiltX));
        poseStack.mulPose(Axis.YP.rotation(entity.tiltY));
        poseStack.mulPose(Axis.ZP.rotation(entity.tiltZ));
        poseStack.popPose();
    }
}
