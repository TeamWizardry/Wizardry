package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketPearlSwappingKeybindState;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketRightClickPearlBelt;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketSwapOnRightClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.UUID;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class ModKeybinds {

	@SideOnly(Side.CLIENT)
	public static KeyBinding pearlSwapping;

	private static final WeakHashMap<UUID, Boolean> trackerPearlSwapping = new WeakHashMap<>();

	@SideOnly(Side.CLIENT)
	public static void register() {
		ClientRegistry.registerKeyBinding(pearlSwapping = new KeyBinding("key.pearl_swapping.desc", Keyboard.KEY_C, Wizardry.MODNAME));
	}

	public static boolean getPearlSwapping(UUID uuid) {
		return trackerPearlSwapping.getOrDefault(uuid, false);
	}

	public static void setPearlSwapping(UUID uuid, boolean state) {
		trackerPearlSwapping.put(uuid, state);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent event) {
		if (!Keyboard.isCreated()) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if ((pearlSwapping.isKeyDown() && !getPearlSwapping(player.getUniqueID())) || (!pearlSwapping.isKeyDown() && getPearlSwapping(player.getUniqueID()))) {
			PacketHandler.NETWORK.sendToServer(new PacketPearlSwappingKeybindState(pearlSwapping.isKeyDown()));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (!event.getWorld().isRemote) return;
		if (getPearlSwapping(event.getEntityPlayer().getUniqueID())) {
			if (event.getItemStack().getItem() instanceof IPearlSwappable) {
				PacketHandler.NETWORK.sendToServer(new PacketSwapOnRightClick());
				event.setCancellationResult(EnumActionResult.FAIL);
				event.setResult(Event.Result.DENY);
				event.setCanceled(true);
			}
		}

		if (event.getItemStack().getItem() instanceof IPearlBelt) {
			PacketHandler.NETWORK.sendToServer(new PacketRightClickPearlBelt(pearlSwapping.isKeyDown()));
			event.setCancellationResult(EnumActionResult.FAIL);
			event.setResult(Event.Result.DENY);
			event.setCanceled(true);
		}
	}
}