package com.teamwizardry.wizardry.api;

import com.teamwizardry.librarianlib.features.config.ConfigProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientConfigValues
{
	@ConfigProperty(category = "renderSpell", comment = "If enabled, the crude halo will renderSpell.")
	public static boolean renderCrudeHalo = true;

	@ConfigProperty(category = "renderSpell", comment = "If enabled, the real halo will renderSpell.")
	public static boolean renderRealHalo = true;

	@ConfigProperty(category = "renderSpell", comment = "If enabled, the creative halo will renderSpell.")
	public static boolean renderCreativeHalo = true;

	@ConfigProperty(category = "renderSpell", comment = "If enabled, the cape will renderSpell.")
	public static boolean renderCape = true;
}
