package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by LordSaad.
 */
public class Module implements INBTSerializable<NBTTagCompound> {

	/**
	 * Extra information that can be edited and read by the module.
	 * Used by modifiers.
	 */
	public NBTTagCompound attributes = new NBTTagCompound();

	/**
	 * The module that is to be ran from the run methods of the current module.
	 */
	@Nullable
	public Module nextModule;

	public Module() {
	}

	/**
	 * The stack required during crafting to run this module.
	 *
	 * @return An itemstack representing the price.
	 */
	@NotNull
	public ItemStack getRequiredStack() {
		return new ItemStack(Blocks.STONE);
	}

	/**
	 * The type of module this module is.
	 *
	 * @return A ModuleType representing the type of module this is.
	 */
	@NotNull
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	/**
	 * A lower case snake_case string getID that reflects the module to identify it during serialization/deserialization.
	 *
	 * @return A lower case snake_case string.
	 */
	@NotNull
	public String getID() {
		return "null_id";
	}

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 *
	 * @return A readable string representing it's name or title.
	 */
	@NotNull
	public String getReadableName() {
		return "Null Module";
	}

	/**
	 * The description of what this module does.
	 *
	 * @return A string representing what this module does.
	 */
	@NotNull
	public String getDescription() {
		return "This module is null";
	}

	public double getManaToConsume() {
		return 10;
	}

	public double getBurnoutToFill() {
		return 10;
	}

	/**
	 * Run the whatever is required on the SpellStack and then trigger the next step.
	 *
	 * @return whether this spell has succeeded or failed this step.
	 */
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		return false;
	}

	@Nullable
	public IWizardryCapability getCap(EntityLivingBase entity) {
		if (entity != null && entity instanceof EntityPlayer)
			return WizardryCapabilityProvider.get((EntityPlayer) entity);
		return null;
	}

	/**
	 * Will apply modifiers to the head module given a stream of itemstacks representing the modifiers.
	 *
	 * @param modifiers The list of itemstacks representing a stream of modifiers to apply.
	 */
	public void processModifiers(List<ItemStack> modifiers) {
		for (ItemStack stack : modifiers) {
			Module modifier = ModuleRegistry.INSTANCE.getModule(stack);
			if (modifier == null) continue;
			if (!(modifier instanceof IModifier)) continue;

			((IModifier) modifier).apply(this);
		}
	}

	@NotNull
	public Module copy() {
		Module module = new Module();
		module.deserializeNBT(serializeNBT());
		return module;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("attributes", attributes);
		if (nextModule != null) compound.setTag("next_module", nextModule.serializeNBT());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		attributes = nbt.getCompoundTag("attributes");
		if (nbt.hasKey("next_module")) {
			nextModule = new Module();
			nextModule.deserializeNBT(nbt.getCompoundTag("next_module"));
		}
	}
}
