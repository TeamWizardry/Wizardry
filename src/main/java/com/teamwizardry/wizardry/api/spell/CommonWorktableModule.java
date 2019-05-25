package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;

/**
 * Server-friendly serializable worktable-convertable module data objects.
 */
public class CommonWorktableModule implements INBTSerializable<NBTTagCompound> {

	public ModuleInstance module;
	public Vec2d pos;
	@Nullable
	public CommonWorktableModule linksTo = null;
	@Nonnull
	public HashMap<ModuleInstanceModifier, Integer> modifiers = new HashMap<>();
	public int hash = -1;

	public CommonWorktableModule(int hash, ModuleInstance module, Vec2d pos, @Nullable CommonWorktableModule linksTo, @Nonnull HashMap<ModuleInstanceModifier, Integer> modifiers) {
		this.hash = hash;
		this.module = module;
		this.pos = pos;
		this.linksTo = linksTo;
		this.modifiers = modifiers;
	}

	private CommonWorktableModule() {
	}

	public static CommonWorktableModule deserailize(NBTTagCompound compound) {
		CommonWorktableModule worktableModule = new CommonWorktableModule();
		worktableModule.deserializeNBT(compound);
		return worktableModule;
	}

	public void addModifier(ModuleInstanceModifier moduleModifier, int count) {
		modifiers.put(moduleModifier, count);
	}

	public void setLinksTo(@Nullable CommonWorktableModule linksTo) {
		this.linksTo = linksTo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommonWorktableModule that = (CommonWorktableModule) o;
		return Objects.equals(hash, that.hash);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hash);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		if (hash != -1) {
			compound.setInteger("hash", hash);
		}

		if (module != null)
			compound.setString("module", module.getNBTKey());

		if (pos != null) {
			compound.setDouble("x", pos.getX());
			compound.setDouble("y", pos.getY());
		}
		if (linksTo != null)
			compound.setTag("linksTo", linksTo.serializeNBT());

		NBTTagCompound modifierNBT = new NBTTagCompound();
		for (ModuleInstanceModifier modifier : modifiers.keySet()) {
			int count = modifiers.get(modifier);
			modifierNBT.setInteger(modifier.getNBTKey(), count);
		}
		compound.setTag("modifiers", modifierNBT);

		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("module")) {
			module = ModuleInstance.deserialize(nbt.getString("module"));
		}
		if (nbt.hasKey("x") && nbt.hasKey("y")) {
			pos = new Vec2d(nbt.getDouble("x"), nbt.getDouble("y"));
		}
		if (nbt.hasKey("linksTo")) {
			linksTo = deserailize(nbt.getCompoundTag("linksTo"));
		}
		if (nbt.hasKey("modifiers")) {
			modifiers = new HashMap<>();
			for (String base : nbt.getCompoundTag("modifiers").getKeySet()) {
				ModuleInstance module = ModuleRegistry.INSTANCE.getModule(base);
				if (!(module instanceof ModuleInstanceModifier)) continue;
				int count = nbt.getCompoundTag("modifiers").getInteger(base);

				modifiers.put((ModuleInstanceModifier) module, count);
			}
		}

		if (nbt.hasKey("hash")) {
			hash = nbt.getInteger("hash");
		}

	}
}
