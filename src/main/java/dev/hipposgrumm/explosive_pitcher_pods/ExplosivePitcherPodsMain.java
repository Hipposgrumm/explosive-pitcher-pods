package dev.hipposgrumm.explosive_pitcher_pods;

import dev.hipposgrumm.explosive_pitcher_pods.entity.PitcherPodExplosive;
import dev.hipposgrumm.explosive_pitcher_pods.entity.PitcherPodExplosiveRenderer;
import dev.hipposgrumm.explosive_pitcher_pods.util.RegisterHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import java.util.function.Supplier;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
//? if fabric {
/*import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
//? if <1.21 {
/^import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
 ^///?} else {
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
//?}
*///?} elif forge {
/*import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
*///?} elif neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
//?}

//? if forge || neoforge {
@Mod(ExplosivePitcherPodsMain.MODID)
//?}
public class ExplosivePitcherPodsMain /*? if fabric {*//*implements net.fabricmc.api.ModInitializer, net.fabricmc.api.ClientModInitializer*//*?}*/ {
    public static final String MODID = "explosive_pitcher_pods";
    private static final Logger LOGGER = LoggerFactory.getLogger("Explosive Pitcher Pods");;

    public static final Supplier<EntityType<PitcherPodExplosive>> PITCHER_POD = RegisterHelper.entity("pitcher_pod", () -> EntityType.Builder.<PitcherPodExplosive>of(PitcherPodExplosive::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(ResourceLocation.tryBuild(MODID,"pitcher_pod").toString()));

    //? if forge || neoforge {
    public ExplosivePitcherPodsMain(/*? if neoforge {*/IEventBus bus/*?}*/) {
        //? if forge {
        /*IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        *///?}
        RegisterHelper.register(bus);

        //? if forge {
        /*IEventBus eventBus = MinecraftForge.EVENT_BUS;
        *///?} elif neoforge {
        IEventBus eventBus = NeoForge.EVENT_BUS;
        //?}
        if (FMLEnvironment.dist.isClient()) bus.addListener(ClientModEvents::entitySetup);
        eventBus.addListener(LiveModEvents::detectPitcherPodBreak);
        eventBus.addListener(LiveModEvents::throwPitcherGrenadesItem);
        eventBus.addListener(LiveModEvents::throwPitcherGrenadesEmpty);
    }
    //?}

    //? if fabric {
    /*public void onInitialize() {
        // Init Entities
        PITCHER_POD.get();

        // Init Events
        PlayerBlockBreakEvents.BEFORE.register(LiveModEvents::breakPitcherPod);
        UseItemCallback.EVENT.register(LiveModEvents::throwPitcherGrenade);
    }

    public void onInitializeClient() {
        EntityRendererRegistry.register(PITCHER_POD.get(), PitcherPodExplosiveRenderer::new);
    }
    *///?}

    //? if forge || neoforge {
    public static class ClientModEvents {
        public static void entitySetup(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(PITCHER_POD.get(), PitcherPodExplosiveRenderer::new);
        }
    }
    //?}

    public static class LiveModEvents {
        public static boolean breakPitcherPod(LevelAccessor level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
            if (!player.getAbilities().instabuild && !player.getMainHandItem().is(/*? if forge {*//*Tags.Items.SHEARS*//*?} elif neoforge {*//*? if <1.21 {*/Tags.Items.TOOLS_SHEARS/*?} else {*//*Tags.Items.TOOLS_SHEAR*//*?}*//*?} else {*//*/^? if <1.21 {^//^ConventionalItemTags.SHEARS^//^?} else {^/ConventionalItemTags.SHEAR_TOOLS/^?}^/*//*?}*/) && !player.isCrouching() && state.is(Blocks.PITCHER_PLANT) && state.getValue(DoublePlantBlock.HALF)==DoubleBlockHalf.UPPER) { // Most Readable Line
                PitcherPodExplosive projectile = new PitcherPodExplosive(pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, player.level());
                projectile.setOwner(player);
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.5F, 1.0F);
                level.addFreshEntity(projectile);

                BlockPos bottomBlockPos = pos.below();
                BlockState bottomBlock = level.getBlockState(bottomBlockPos);
                if (bottomBlock.is(Blocks.PITCHER_PLANT) && bottomBlock.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    BlockState replaceWith = bottomBlock.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(bottomBlockPos, replaceWith, 35);
                    level.levelEvent(player, 2001, bottomBlockPos, Block.getId(bottomBlock));
                }
                return false;
            }
            return true;
        }

        public static InteractionResultHolder<ItemStack> throwPitcherGrenade(Player player, Level level, InteractionHand hand) {
            if (!level.isClientSide() && player.isAlive() && !player.isSpectator() && !player.getCooldowns().isOnCooldown(Items.PITCHER_PLANT)) {
                ItemStack item = null;
                switch (hand) {
                    case MAIN_HAND -> item = player.getMainHandItem();
                    case OFF_HAND -> item = player.getOffhandItem();
                }
                if (item != null && item.is(Items.PITCHER_PLANT)) {
                    PitcherPodExplosive projectile = new PitcherPodExplosive(player, level);
                    projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.25F, 1.0F);
                    level.addFreshEntity(projectile);

                    if (!player.getAbilities().instabuild) item.shrink(1);

                    player.getCooldowns().addCooldown(Items.PITCHER_PLANT, 30);
                    return InteractionResultHolder.consume(item);
                }
            }
            return InteractionResultHolder.pass(ItemStack.EMPTY);
        }

        //? if forge || neoforge {
        public static void detectPitcherPodBreak(BlockEvent.BreakEvent event) {
            if (!event.isCanceled()) event.setCanceled(!breakPitcherPod(event.getLevel(),event.getPlayer(),event.getPos(),event.getState(),null));
        }

        public static void throwPitcherGrenadesItem(PlayerInteractEvent.RightClickItem event) {
            throwPitcherGrenade(event.getEntity(),event.getLevel(),event.getHand());
        }

        public static void throwPitcherGrenadesEmpty(PlayerInteractEvent.RightClickEmpty event) {
            throwPitcherGrenade(event.getEntity(),event.getLevel(),event.getHand());
        }
        //?}
    }
}
