package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ModKeybinds {

	public static KeyBinding pearlSwapping = new KeyBinding("key.pearl_swapping.desc", Keyboard.KEY_C, Wizardry.MODNAME);

	public static void register() {
		ClientRegistry.registerKeyBinding(pearlSwapping);
	}
}