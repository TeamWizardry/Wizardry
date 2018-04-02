package com.teamwizardry.wizardry.api;

import com.teamwizardry.librarianlib.features.config.ConfigProperty;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientConfigValues
{
	@ConfigProperty(category = "render", comment = "If enabled, the crude halo will render.")
	public static boolean renderCrudeHalo = true;
	
	@ConfigProperty(category = "render", comment = "If enabled, the real halo will render.")
	public static boolean renderRealHalo = true;
	
	@ConfigProperty(category = "render", comment = "If enabled, the creative halo will render.")
	public static boolean renderCreativeHalo = true;
	
	@ConfigProperty(category = "render", comment = "If enabled, the cape will render.")
	public static boolean renderCape = true;
}
