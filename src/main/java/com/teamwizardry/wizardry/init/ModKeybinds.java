package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketSetPearlSwapKeyState;
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

import java.util.UUID;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class ModKeybinds {

	@SideOnly(Side.CLIENT)
	public static KeyBinding pearlSwapping;

	// if true, button is down (server friendly)
	private static WeakHashMap<UUID, Boolean> pearlSwappingState = new WeakHashMap<>();

	@SideOnly(Side.CLIENT)
	public static void register() {
		ClientRegistry.registerKeyBinding(pearlSwapping = new KeyBinding("key.pearl_swapping.desc", Keyboard.KEY_C, Wizardry.MODNAME));
	}

	public static void putPearlSwappingState(UUID uuid, boolean state) {
		pearlSwappingState.put(uuid, state);
	}

	public static boolean getPearlSwappingState(UUID uuid) {
		return pearlSwappingState.getOrDefault(uuid, false);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void keyInput(InputEvent.KeyInputEvent event) {
		if (!Keyboard.isCreated()) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (ModKeybinds.pearlSwapping.isKeyDown() && !ModKeybinds.getPearlSwappingState(player.getUniqueID())) {
			PacketHandler.NETWORK.sendToServer(new PacketSetPearlSwapKeyState(true));
		} else if (!ModKeybinds.pearlSwapping.isKeyDown() && ModKeybinds.getPearlSwappingState(player.getUniqueID())) {
			PacketHandler.NETWORK.sendToServer(new PacketSetPearlSwapKeyState(false));
		}

	}
}