package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;

/**
 * Created by Demoniaque.
 */
public class PacketSyncModules extends PacketBase {

	private ArrayList<Module> modules;

	public PacketSyncModules() {
	}

	public PacketSyncModules(ArrayList<Module> modules) {
		this.modules = modules;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		if (compound == null) return;
		NBTTagList list = compound.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound1 = list.getCompoundTagAt(i);
			Module module = ModuleRegistry.INSTANCE.getModule(compound1.getString("id"));
			modules.add(module);
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (Module module : modules) {
			list.appendTag(module.serializeNBT());
		}
		nbt.setTag("list", list);
		return nbt;
	}

	@Override
	public void handle(MessageContext messageContext) {
		ModuleRegistry.INSTANCE.modules = modules;
	}
}
