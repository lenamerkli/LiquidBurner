package com.ferriarnus.liquidburner.recipe;

import com.ferriarnus.liquidburner.LiquidBurner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;


public class RecipeRegistry {

    public static void register() {

    }
    public static RecipeType<LiquidBurning> LIQUIDBURNING =  Registry.register(Registry.RECIPE_TYPE, LiquidBurner.MODID + ":liquidburning", new RecipeTypeImpl<LiquidBurning>(new ResourceLocation(LiquidBurner.MODID, "liquidburning")) );

    static {
        Registry.register(Registry.RECIPE_SERIALIZER, LiquidBurner.MODID + ":liquidburning", LiquidBurning.SERIALIZER);
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
