package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketRightClickPearlBelt;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketSwapOnRightClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class ModKeybinds {

	@SideOnly(Side.CLIENT)
	public static KeyBinding pearlSwapping;

	@SideOnly(Side.CLIENT)
	public static void register() {
		ClientRegistry.registerKeyBinding(pearlSwapping = new KeyBinding("key.pearl_swapping.desc", Keyboard.KEY_C, Wizardry.MODNAME));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onMouse(MouseEvent event) {
		if (!Keyboard.isCreated()) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (event.getButton() == 1 && !event.isButtonstate()) {
			ItemStack heldItem = player.getHeldItemMainhand();

			if (pearlSwapping.isKeyDown()) {

				if (heldItem.getItem() instanceof IPearlSwappable) {
					PacketHandler.NETWORK.sendToServer(new PacketSwapOnRightClick());
					event.setCanceled(true);
				}
			}

			if (heldItem.getItem() instanceof IPearlBelt) {
				PacketHandler.NETWORK.sendToServer(new PacketRightClickPearlBelt(pearlSwapping.isKeyDown()));
				event.setCanceled(true);
			}
		}
	}
}