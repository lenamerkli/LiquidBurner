package com.ferriarnus.liquidburner;

import com.ferriarnus.liquidburner.recipe.RecipeRegistry;
import net.fabricmc.api.ModInitializer;

// The value here should match an entry in the META-INF/mods.toml file
public class LiquidBurner implements ModInitializer {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "liquidburner";

    public LiquidBurner() {
        Tags.init();
        RecipeRegistry.register();
    }

    @Override
    public void onInitialize() {

    }
}
