package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.teamwizardry.wizardry.api.spell.SpellStack.getModules;

/**
 * Created by LordSaad.
 */
public class PacketRenderSpell extends PacketBase {

	@Save
	private ItemStack stack;
	@Save
	private NBTTagCompound data;

	public PacketRenderSpell() {
	}

	public PacketRenderSpell(ItemStack stack, SpellData data) {
		this.stack = stack;
		this.data = data.serializeNBT();
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		if (Minecraft.getMinecraft().player == null) return;
		World world = Minecraft.getMinecraft().player.world;

		SpellData data = new SpellData(world);
		data.deserializeNBT(this.data);
		for (Module module : getModules(stack)) {
			if (module != null) {
				Module tempModule = module;
				while (tempModule != null) {
					tempModule.runClient(stack, data);
					tempModule = tempModule.nextModule;
				}
			}
		}
	}
}
