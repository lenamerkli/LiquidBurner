package com.ferriarnus.liquidburner.recipe;

import com.ferriarnus.liquidburner.LiquidBurner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.C;

public class RecipeRegistry {

    static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, LiquidBurner.MODID);
    static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, LiquidBurner.MODID);

    public static void register() {
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    public static RegistryObject<RecipeType<LiquidBurning>> LIQUIDBURNING = RECIPES.register("liquidburning", ()-> new RecipeTypeImpl<LiquidBurning>(new ResourceLocation(LiquidBurner.MODID, "liquidburning")) );

    static {
        SERIALIZERS.register("liquidburning", ()-> LiquidBurning.SERIALIZER);
    }

    private static class RecipeTypeImpl<T extends Recipe<?>> implements RecipeType<T> {

        private final ResourceLocation rl;

        public RecipeTypeImpl(ResourceLocation rl) {
            this.rl = rl;
        }

        @Override
        public String toString() {
            return rl.toString();
        }
    }

}
