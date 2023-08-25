package gg.hipposgrumm.explosive_pitcher_pods;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.explosive_pitcher_pods.entity.PitcherPodExplosive;
import gg.hipposgrumm.explosive_pitcher_pods.entity.PitcherPodExplosiveRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(ExplosivePitcherPods.MODID)
public class ExplosivePitcherPods {
    public static final String MODID = "explosive_pitcher_pods";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<PitcherPodExplosive>> PITCHER_POD = ENTITIES.register("pitcher_pod", () -> EntityType.Builder.<PitcherPodExplosive>of(PitcherPodExplosive::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(new ResourceLocation(MODID,"pitcher_pod").toString()));

    public ExplosivePitcherPods() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void entitySetup(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(PITCHER_POD.get(), PitcherPodExplosiveRenderer::new);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class LiveModEvents {
        @SubscribeEvent
        public static void detectPitcherPodBreak(BlockEvent.BreakEvent event) {
            if (!event.isCanceled() && !event.getPlayer().getAbilities().instabuild && !event.getPlayer().getMainHandItem().is(Tags.Items.SHEARS) && !event.getPlayer().isCrouching() && event.getState().is(Blocks.PITCHER_PLANT) && event.getState().getValue(DoublePlantBlock.HALF)==DoubleBlockHalf.UPPER) {
                Player player = event.getPlayer();
                Level level = player.level();
                PitcherPodExplosive projectile = new PitcherPodExplosive(event.getPos().getCenter().x, event.getPos().getCenter().y, event.getPos().getCenter().z, level);
                projectile.setOwner(player);
                projectile.setItem(Items.PITCHER_PLANT.getDefaultInstance());
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.5F, 1.0F);
                level.addFreshEntity(projectile);
                event.setCanceled(true);
                BlockPos bottomBlockPos = event.getPos().below();
                BlockState bottomBlock = level.getBlockState(bottomBlockPos);
                if (bottomBlock.is(Blocks.PITCHER_PLANT) && bottomBlock.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    BlockState replaceWith = bottomBlock.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(bottomBlockPos, replaceWith, 35);
                    level.levelEvent(player, 2001, bottomBlockPos, Block.getId(bottomBlock));
                }
            }
        }

        public static void throwPitcherGrenade(ItemStack item, Player entity, Level level) {
            if (item.is(Items.PITCHER_PLANT)) {
                PitcherPodExplosive projectile = new PitcherPodExplosive(entity, level);
                projectile.setItem(Items.PITCHER_PLANT.getDefaultInstance());
                projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.25F, 1.0F);
                level.addFreshEntity(projectile);

                item.shrink(1);

                entity.getCooldowns().addCooldown(Items.PITCHER_PLANT, 100);
            }
        }

        @SubscribeEvent
        public static void throwPitcherGrenades(PlayerInteractEvent.RightClickItem event) {
            throwPitcherGrenade(event.getItemStack(),event.getEntity(),event.getLevel());
        }

        @SubscribeEvent
        public static void throwPitcherGrenades(PlayerInteractEvent.RightClickEmpty event) {
            throwPitcherGrenade(event.getItemStack(),event.getEntity(),event.getLevel());
        }
    }
}
