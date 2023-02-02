package com.ferriarnus.liquidburner;

import com.ferriarnus.liquidburner.recipe.RecipeRegistry;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LiquidBurner.MODID)
public class LiquidBurner
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "liquidburner";

    public LiquidBurner() {
        Tags.init();
        RecipeRegistry.register();
    }
}
