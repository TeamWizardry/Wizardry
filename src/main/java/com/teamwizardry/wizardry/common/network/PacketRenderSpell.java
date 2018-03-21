package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public class PacketRenderSpell extends PacketBase {

	private NBTTagCompound spellData;
	private SpellRing spellRing;

	public PacketRenderSpell() {
	}

	public PacketRenderSpell(SpellData spellData, SpellRing spellRing) {
		this.spellData = spellData.serializeNBT();
		this.spellRing = spellRing;
	}

	@SaveMethodGetter(saveName = "module_saver")
	public NBTTagCompound getter() {
		NBTTagCompound compound = new NBTTagCompound();

		if (spellData == null || spellRing == null) return compound;

		compound.setTag("spell_data", spellData);
		compound.setTag("spell_ring", spellRing.serializeNBT());
		return compound;
	}

	@SaveMethodSetter(saveName = "module_saver")
	public void setter(NBTTagCompound compound) {
		if (compound.hasKey("spell_data")) spellData = compound.getCompoundTag("spell_data");
		if (compound.hasKey("spell_ring")) spellRing = SpellRing.deserializeRing(compound.getCompoundTag("spell_ring"));
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		if (messageContext.side.isServer()) return;
		World world = LibrarianLib.PROXY.getClientPlayer().world;
		if (world == null || spellRing == null || spellData == null) return;

		SpellData data = new SpellData(world);
		data.deserializeNBT(spellData);

		try {
			if (spellRing.getModule() != null) {
				spellRing.getModule().render(data, spellRing);
			}
		} catch (Exception ignored) {
		}
	}
}
