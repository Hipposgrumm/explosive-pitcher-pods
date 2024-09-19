package dev.hipposgrumm.explosive_pitcher_pods.util;

import dev.hipposgrumm.explosive_pitcher_pods.ExplosivePitcherPodsMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

//? if forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
*///?} elif neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
//?}

import java.util.HashMap;
import java.util.function.Supplier;

public class RegisterHelper {
    //? if forge || neoforge {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(/*? if forge {*//*ForgeRegistries.ENTITY_TYPES*//*?} else {*/BuiltInRegistries.ENTITY_TYPE/*?}*/, ExplosivePitcherPodsMain.MODID);
    //?} else {
    /*private static final HashMap<ResourceLocation,EntityType<?>> ENTITIES = new HashMap<>();
    *///?}

    @SuppressWarnings("unchecked")
    public static <T extends Entity> Supplier<EntityType<T>> entity(String id, Supplier<EntityType<T>> entity) {
        //? if forge || neoforge {
        return ENTITIES.register(id,entity);
        //?} else {
        /*ResourceLocation location = ResourceLocation.tryBuild(ExplosivePitcherPodsMain.MODID,id);
        if (!ENTITIES.containsKey(location)) {
            ENTITIES.put(location, Registry.register(BuiltInRegistries.ENTITY_TYPE, location, entity.get()));
        }
        return () -> (EntityType<T>) ENTITIES.get(location);
        *///?}
    }

    //? if forge || neoforge {
    public static void register(IEventBus bus) {
        RegisterHelper.ENTITIES.register(bus);
    }
    //?}
}
