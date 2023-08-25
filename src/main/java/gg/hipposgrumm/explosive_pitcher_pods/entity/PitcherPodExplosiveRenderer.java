package gg.hipposgrumm.explosive_pitcher_pods.entity;

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
    public void render(T entity, float p_116086_, float p_116087_, PoseStack poseStack, MultiBufferSource p_116089_, int p_116090_) {
        super.render(entity,p_116086_,p_116087_,poseStack,p_116089_,p_116090_);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(entity.tiltX));
        poseStack.mulPose(Axis.YP.rotation(entity.tiltY));
        poseStack.mulPose(Axis.ZP.rotation(entity.tiltZ));
        poseStack.popPose();
    }
}
