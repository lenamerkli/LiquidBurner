package com.ferriarnus.liquidburner;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class Tags {

    public static void init(){
        Fuilds.init();
    }

    public static class Fuilds {
        private static void init() {}

        public static final TagKey<Fluid> BLAZE_BURNER_FUEL_SPECIAL = tag("blaze_burner_fuel_special");
        public static final TagKey<Fluid> BLAZE_BURNER_FUEL_REGULAR = tag("blaze_burner_fuel_regular");
        public static final TagKey<Fluid> BLAZE_BURNER_FUEL_ALL = tag("blaze_burner_fuel_all");

        private static TagKey<Fluid> tag(String name) {
            return TagKey.create(Registries.FLUID, new ResourceLocation(LiquidBurner.MODID, name));
        }
    }
}
