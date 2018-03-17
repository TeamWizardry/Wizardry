package com.teamwizardry.wizardry.api.spell;

import com.google.common.collect.ArrayListMultimap;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Modules ala IBlockStates
 */
@Savable
public class SpellRing implements INBTSerializable<NBTTagCompound> {

	/**
	 * Store all processed modifier info and any extra you want here.
	 * Used by modifier processing and the WorktableGUI to save GUI in TileWorktable
	 */
	private NBTTagCompound informationTag = new NBTTagCompound();

	/**
	 * Stores the actual modifier data into a serializable compound.
	 */
	@Nonnull
	private HashMap<ModuleModifier, Integer> modifiers = new HashMap<>();

	/**
	 * Primary rendering color.
	 */
	@Nonnull
	private Color primaryColor = Color.WHITE;

	/**
	 * Secondary rendering color.
	 */
	@Nonnull
	private Color secondaryColor = Color.WHITE;

	/**
	 * The Module of this Ring.
	 */
	@Nullable
	private Module module;

	/**
	 * The parent ring of this Ring, the ring that will have been run before this.
	 */
	@Nullable
	private SpellRing parentRing = null;

	/**
	 * The child ring of this Ring, the ring that will run after this.
	 */
	@Nullable
	private SpellRing childRing = null;

	private float powerMultiplier = 1, manaMultiplier = 1, burnoutMultiplier = 0;

	private SpellRing() {
	}

	public SpellRing(@Nonnull Module module) {
		setModule(module);
	}

	public static SpellRing deserializeRing(NBTTagCompound compound) {
		SpellRing ring = new SpellRing();
		ring.deserializeNBT(compound);
		return ring;
	}

	/**
	 * If a child has this as true, it's parents will not run their run methods.
	 */
	public boolean overrideParentRuns() {
		return module != null && module.overrideParentRuns();
	}

	/**
	 * Will check if this ring's run method is overridden by any of it's children
	 */
	public boolean isRunOverriden() {
		for (SpellRing ring : getAllChildRings()) {
			if (ring.overrideParentRuns()) return true;
		}
		return false;
	}

	/**
	 * If a child has this as true, it's parents will not run their render methods.
	 */
	public boolean overrideParentRenders() {
		return module != null && module.overrideParentRenders();
	}

	/**
	 * Will check if this ring's render method is overridden by any of it's children
	 */
	public boolean isRenderOverridden() {
		for (SpellRing ring : getAllChildRings()) {
			if (ring.overrideParentRenders()) return true;
		}
		return false;
	}

	/**
	 * Will run the spellData from this ring and down to it's children including rendering.
	 *
	 * @param data The SpellData object.
	 */
	public boolean runSpellRing(SpellData data) {
		if (module == null) return false;
		boolean success = !isRunOverriden() && module.castSpell(data, this) && !module.ignoreResult();
		if (success) {

			if (!isRenderOverridden() && module != null) {
				module.sendRenderPacket(data, this);
			}

			if (getChildRing() != null) return getChildRing().runSpellRing(data);
		} else if (module.ignoreResult()) {
			if (!isRenderOverridden() && module != null) {
				module.sendRenderPacket(data, this);
			}
		}

		return success;
	}

	/**
	 * Get a modifier in this ring between the range.
	 *
	 * @param attribute The attribute you want. List in AttributeRegistry for default ones.
	 * @param min       Min range.
	 * @param max       Max range.
	 * @return The double potency of a modifier.
	 */
	public final double getModifier(AttributeRegistry.Attribute attribute, double min, double max) {
		double current = informationTag.getDouble(attribute.getNbtName());

		return MathHelper.clamp(min + current, min, max) * getBurnoutMultiplier() * getPowerMultiplier();
	}

	public void processModifiers() {
		List<AttributeModifier> attributeModifiers = new ArrayList<>();

		if (module != null) attributeModifiers.addAll(module.getAttributes());

		for (ModuleModifier modifier : getModifiers().keySet()) {
			attributeModifiers.addAll(modifier.getAttributes());
		}

		ArrayListMultimap<Operation, AttributeModifier> sortedMap = ArrayListMultimap.create();
		for (AttributeModifier modifier : attributeModifiers)
			sortedMap.put(modifier.getOperation(), modifier);

		for (Operation op : Operation.values()) {
			for (AttributeModifier modifier : sortedMap.get(op)) {

				double current = informationTag.getDouble(modifier.getAttribute().getNbtName());
				double newValue = modifier.apply(current);

				informationTag.setDouble(modifier.getAttribute().getNbtName(), newValue);

				//Wizardry.logger.info(module == null ? "<null module>" : module.getID() + ": Attribute: " + attribute + ": " + current + "-> " + newValue);
			}
		}
	}

	public final float getReductionMultiplier(EntityLivingBase caster) {
		ItemStack stack = BaublesSupport.getItem(caster, ModItems.CAPE);
		if (stack != null) {
			float time = ItemNBTHelper.getInt(stack, "maxTick", 0);
			return (float) MathHelper.clamp(1 - (time / 1000000.0), 1, 0.25);
		}
		return 1;
	}

	/**
	 * Get all the children rings of this ring excluding itself.
	 */
	private Set<SpellRing> getAllChildRings() {
		Set<SpellRing> childRings = new HashSet<>();

		if (childRing == null) return childRings;

		SpellRing tempModule = childRing;
		while (tempModule != null) {
			childRings.add(tempModule);
			tempModule = tempModule.getChildRing();
		}
		return childRings;
	}

	@Nullable
	public SpellRing getChildRing() {
		return childRing;
	}

	public void setChildRing(@Nonnull SpellRing childRing) {
		this.childRing = childRing;
	}

	@Nullable
	public SpellRing getParentRing() {
		return parentRing;
	}

	public void setParentRing(@Nullable SpellRing parentRing) {
		this.parentRing = parentRing;
	}

	@Nullable
	public Module getModule() {
		return module;
	}

	public void setModule(@Nonnull Module module) {
		this.module = module;

		setManaMultiplier(module.getManaMultiplier());
		setBurnoutMultiplier(module.getBurnoutMultiplier());
		setPowerMultiplier(module.getPowerMultiplier());
	}

	@Nonnull
	public Color getPrimaryColor() {
		return primaryColor;
	}

	public void setPrimaryColor(@Nonnull Color primaryColor) {
		this.primaryColor = primaryColor;
		updateColorChain();
	}

	@Nonnull
	public Color getSecondaryColor() {
		return secondaryColor;
	}

	public void setSecondaryColor(@Nonnull Color secondaryColor) {
		this.secondaryColor = secondaryColor;
	}

	public void updateColorChain() {
		if (getParentRing() == null) return;

		getParentRing().setPrimaryColor(getPrimaryColor());
		getParentRing().setSecondaryColor(getSecondaryColor());
		getParentRing().updateColorChain();
	}

	public float getPowerMultiplier() {
		return powerMultiplier;
	}

	public void setPowerMultiplier(float powerMultiplier) {
		this.powerMultiplier = powerMultiplier;
	}

	public void multiplyPowerMultiplier(float powerMultiplier) {
		this.powerMultiplier *= powerMultiplier;
	}

	public float getManaMultiplier() {
		return manaMultiplier;
	}

	public void setManaMultiplier(float manaMultiplier) {
		this.manaMultiplier = manaMultiplier;
	}

	public void multiplyManaMultiplier(float manaMultiplier) {
		this.manaMultiplier *= manaMultiplier;
	}

	/**
	 * This multiplier is special because we take it's inverse.
	 * When multiplying it, if your burnout is 0, then that means you have no burnout
	 * but your multiplier completely nullifies your numbers, so we want the inverse
	 * because this should be obvious and why am I even explaining this, grow a brain.
	 *
	 * @return The INVERTED burnout multiplier.
	 */
	public float getBurnoutMultiplier() {
		return 1 - burnoutMultiplier;
	}

	public void setBurnoutMultiplier(float burnoutMultiplier) {
		this.burnoutMultiplier = burnoutMultiplier;
	}

	public void multiplyBurnoutMultiplier(float burnoutMultiplier) {
		this.burnoutMultiplier *= burnoutMultiplier;
	}

	public void setMultiplierForAll(float multiplier) {
		this.powerMultiplier = multiplier;
		this.burnoutMultiplier = multiplier;
		this.manaMultiplier = multiplier;
	}

	public void multiplyMultiplierForAll(float multiplier) {
		this.powerMultiplier *= multiplier;
		this.burnoutMultiplier *= multiplier;
		this.manaMultiplier *= multiplier;
	}

	public double getManaDrain() {
		return module != null ? module.getManaDrain() : 0;
	}

	public double getBurnoutFill() {
		return module != null ? module.getBurnoutFill() : 0;
	}

	@Nonnull
	public HashMap<ModuleModifier, Integer> getModifiers() {
		return modifiers;
	}

	public void addModifier(ModuleModifier modifier) {
		if (!modifiers.containsKey(modifier)) modifiers.put(modifier, 1);
		else modifiers.put(modifier, modifiers.get(modifier) + 1);
	}

	public int getCooldownTime(@Nullable SpellData data) {
		if (data != null && module instanceof IOverrideCooldown)
			return ((IOverrideCooldown) module).getNewCooldown(data, this);

		return module != null ? module.getCooldownTime() : 0;
	}

	public int getCooldownTime() {
		return getCooldownTime(null);
	}

	public int getChargeUpTime() {
		return module != null ? module.getChargeupTime() : 0;
	}

	@Nullable
	public String getModuleReadableName() {
		return module != null ? module.getReadableName() : null;
	}

	public NBTTagCompound getInformationTag() {
		return informationTag;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		SpellRing ring = this;
		while (ring != null) {
			builder.append(ring.getModuleReadableName()).append(getChildRing() == null ? "" : " > ");
			ring = ring.getChildRing();
		}

		return builder.toString();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagList attribs = new NBTTagList();
		for (Map.Entry<ModuleModifier, Integer> modifier : modifiers.entrySet()) {
			NBTTagCompound modifierCompound = new NBTTagCompound();

			modifierCompound.setTag("modifier", modifier.getKey().serialize());
			modifierCompound.setInteger("count", modifier.getValue());

			attribs.appendTag(modifierCompound);
		}

		compound.setTag("extra", informationTag);
		compound.setTag("modifiers", attribs);
		compound.setFloat("power_multiplier", powerMultiplier);
		compound.setFloat("burnout_multiplier", burnoutMultiplier);
		compound.setFloat("mana_multiplier", manaMultiplier);

		if (childRing != null) compound.setTag("child_ring", this.childRing.serializeNBT());
		if (module != null) compound.setString("module", module.getID());

		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("extra")) informationTag = nbt.getCompoundTag("extra");
		if (nbt.hasKey("power_multiplier")) powerMultiplier = nbt.getFloat("power_multiplier");
		if (nbt.hasKey("burnout_multiplier")) burnoutMultiplier = nbt.getFloat("burnout_multiplier");
		if (nbt.hasKey("mana_multiplier")) manaMultiplier = nbt.getFloat("mana_multiplier");

		if (nbt.hasKey("modifiers")) {
			modifiers = new HashMap<>();
			for (NBTBase base : nbt.getTagList("modifiers", Constants.NBT.TAG_COMPOUND))
				if (base instanceof NBTTagCompound) {
					NBTTagCompound modifierComound = (NBTTagCompound) base;
					if (modifierComound.hasKey("modifier") && modifierComound.hasKey("count")) {
						ModuleModifier moduleModifier = (ModuleModifier) Module.deserialize(modifierComound.getString("modifier"));

						if (moduleModifier == null) continue;
						modifiers.put(moduleModifier, modifierComound.getInteger("count"));
					}
				}
		}

		if (nbt.hasKey("module")) this.module = ModuleRegistry.INSTANCE.getModule(nbt.getString("module"));

		if (nbt.hasKey("child_ring")) {
			SpellRing childRing = deserializeRing(nbt.getCompoundTag("child_ring"));
			childRing.setParentRing(this);
			setChildRing(childRing);
		}
	}

	public SpellRing copy() {
		return deserializeRing(serializeNBT());
	}
}
