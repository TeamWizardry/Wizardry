package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlStorageHolder;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class ModKeybinds {

	@SideOnly(Side.CLIENT)
	public static KeyBinding pearlSwapping;

	@SideOnly(Side.CLIENT)
	public static void register() {
		ClientRegistry.registerKeyBinding(pearlSwapping = new KeyBinding("key.pearl_swapping.desc", Keyboard.KEY_C, Wizardry.MODNAME));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent event) {
		if (!Keyboard.isCreated()) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (pearlSwapping.isKeyDown() && player.getHeldItemMainhand().getItem() instanceof IPearlSwappable && !BaublesSupport.getItem(player, IPearlStorageHolder.class).isEmpty()) {
			player.openGui(Wizardry.instance, 1, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}
}