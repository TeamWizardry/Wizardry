package me.lordsaad.wizardry.blocks;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

/**
 * Created by Saad on 6/9/2016.
 */
public class ManaFluid extends Fluid {

    public ManaFluid() {
        super("Mana", new ResourceLocation(Wizardry.MODID, "blocks/mana_still"), new ResourceLocation(Wizardry.MODID, "blocks/mana_flowing"));
        setLuminosity(10);
        setTemperature(283);
        setViscosity(1500);
        setRarity(EnumRarity.UNCOMMON);
    }
}
