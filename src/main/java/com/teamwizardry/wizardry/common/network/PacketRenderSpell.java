package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public class PacketRenderSpell extends PacketBase {

	private Module module;
	@Save
	private NBTTagCompound data;
	@Save
	private SpellRing spellRing;

	public PacketRenderSpell() {
	}

	public PacketRenderSpell(Module module, SpellData data, SpellRing spellRing) {
		this.module = module;
		this.data = data.serializeNBT();
		this.spellRing = spellRing;
	}

	@SaveMethodGetter(saveName = "module_saver")
	public NBTTagCompound getter() {
		return module != null ? module.serialize() : null;
	}

	@SaveMethodSetter(saveName = "module_saver")
	public void setter(NBTTagCompound compound) {
		if (compound != null) this.module = Module.deserialize(compound);
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null || module == null) return;

		SpellData data = new SpellData(world);
		data.deserializeNBT(this.data);


		module.render(data, spellRing);
	}
}
