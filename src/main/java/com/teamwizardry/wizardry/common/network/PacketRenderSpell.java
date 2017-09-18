package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketRenderSpell extends PacketBase {

	private Module module;
	@Save
	private NBTTagCompound data;

	public PacketRenderSpell() {
	}

	public PacketRenderSpell(Module module, SpellData data) {
		this.module = module;
		this.data = data.serializeNBT();
	}

	@SaveMethodGetter(saveName = "module_saver")
	public NBTTagCompound getter() {
		return module != null ? module.serializeNBT() : null;
	}

	@SaveMethodSetter(saveName = "module_saver")
	public void setter(NBTTagCompound compound) {
		if (compound != null) {
			this.module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
			if (module != null)
				this.module.deserializeNBT(compound);
		}
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null) return;

		SpellData data = new SpellData(world);
		data.deserializeNBT(this.data);

		if (module == null) return;

		module.runClient(data);
	}
}
