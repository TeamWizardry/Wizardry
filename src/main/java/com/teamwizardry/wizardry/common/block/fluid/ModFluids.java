package com.teamwizardry.wizardry.common.block.fluid;

import com.teamwizardry.librarianlib.features.base.fluid.ModFluid;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author WireSegal
 * Created at 2:29 PM on 3/3/18.
 */
public class ModFluids {
	public static final ModFluid MANA = new ModFluid("mana_fluid",
			new ResourceLocation(Wizardry.MODID, "fluid/mana_still"),
			new ResourceLocation(Wizardry.MODID, "fluid/mana_flowing"), true)
			.setViscosity(500)
			.setDensity(200)
			.setTemperature(310)
			.setVaporizes(false);

	public static final ModFluid NACRE = new ModFluid("nacre_fluid",
			new ResourceLocation(Wizardry.MODID, "fluid/nacre_still"),
			new ResourceLocation(Wizardry.MODID, "fluid/nacre_flowing"), true)
			.setViscosity(100000)
			.setDensity(3000)
			.setVaporizes(false)
			.makeBlock(Material.WATER);

	public static void init() {
		if (MANA.getActualBlock() == null)
			new BlockFluidMana(MANA);

		MinecraftForge.EVENT_BUS.register(BlockFluidMana.class);
	}
}
