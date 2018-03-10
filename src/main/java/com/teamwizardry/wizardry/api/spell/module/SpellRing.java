package com.teamwizardry.wizardry.api.spell.module;

import com.google.common.collect.ArrayListMultimap;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Modules ala IBlockStates
 */
public class SpellRing implements INBTSerializable<NBTTagCompound> {

	/**
	 * Stores the actual modifier data into a serializable compound.
	 */
	@Nonnull
	private NBTTagCompound attributes = new NBTTagCompound();

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

	private float powerMultiplier = 1, manaMultiplier = 1, burnoutMultiplier = 1;

	private SpellRing() {
	}

	public SpellRing(@Nonnull Module module) {
		this.module = module;
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
	public final boolean isRunOverriden() {
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
	public final boolean isRenderOverridden() {
		for (SpellRing ring : getAllChildRings()) {
			if (ring.overrideParentRenders()) return true;
		}
		return false;
	}

	/**
	 * Will run the spell from this ring and down to it's children including rendering.
	 *
	 * @param data The SpellData object.
	 */
	public final boolean runSpellRing(SpellData data) {
		boolean success = !isRunOverriden() && module != null && module.castSpell(data, this);
		if (success) {

			if (!isRenderOverridden() && module != null) {
				module.sendRenderPacket(data, this);
			}

			if (getChildRing() != null) return getChildRing().runSpellRing(data);
		}

		return success;
	}

	/**
	 * Get a modifier in this ring between the range.
	 *
	 * @param attribute The attribute you want. List in Attributes for default ones.
	 * @param min       Min range.
	 * @param max       Max range.
	 * @return The final double potency of a modifier.
	 */
	protected final double getModifier(String attribute, double min, double max) {
		return (attributes.hasKey(attribute) ? MathHelper.clamp(min + attributes.getDouble(attribute), min, max) : min) * getBurnoutMultiplier();
	}

	public final void processModifiers(List<AttributeModifier> modifiersToApply) {
		ArrayListMultimap<Operation, AttributeModifier> sortedMap = ArrayListMultimap.create();
		for (AttributeModifier modifier : modifiersToApply)
			sortedMap.put(modifier.getOperation(), modifier);

		for (Operation op : Operation.values()) {
			for (AttributeModifier modifier : sortedMap.get(op)) {
				String attribute = modifier.getAttribute();
				double current = attributes.getDouble(attribute);
				double newValue = modifier.apply(current);
				attributes.setDouble(attribute, newValue);

				Wizardry.logger.info(module == null ? "<null module>" : module.getID() + ": Attribute: " + attribute + ": " + current + "-> " + newValue);
			}
		}
	}

	/**
	 * Get all the children rings of this ring.
	 */
	public final Set<SpellRing> getAllChildRings() {
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
	public final SpellRing getChildRing() {
		return childRing;
	}

	public final void setChildRing(@Nonnull SpellRing childRing) {
		this.childRing = childRing;

		updateColorChain();
	}

	@Nullable
	public final SpellRing getParentRing() {
		return parentRing;
	}

	public final void setParentRing(@Nullable SpellRing parentRing) {
		this.parentRing = parentRing;
	}

	@Nullable
	public final Module getModule() {
		return module;
	}

	public final void setModule(@NotNull Module module) {
		this.module = module;
	}

	@Nonnull
	public final Color getPrimaryColor() {
		return primaryColor;
	}

	public final void setPrimaryColor(@Nonnull Color primaryColor) {
		this.primaryColor = primaryColor;
		updateColorChain();
	}

	@Nonnull
	public final Color getSecondaryColor() {
		return secondaryColor;
	}

	public final void setSecondaryColor(@Nonnull Color secondaryColor) {
		this.secondaryColor = secondaryColor;
	}

	private void updateColorChain() {
		if (getParentRing() == null) return;

		getParentRing().setPrimaryColor(getPrimaryColor());
		getParentRing().setPrimaryColor(getSecondaryColor());
		getParentRing().updateColorChain();
	}

	public final float getPowerMultiplier() {
		return powerMultiplier;
	}

	public final void setPowerMultiplier(float powerMultiplier) {
		this.powerMultiplier = powerMultiplier;
	}

	public final float getManaMultiplier() {
		return manaMultiplier;
	}

	public final void setManaMultiplier(float manaMultiplier) {
		this.manaMultiplier = manaMultiplier;
	}

	public final float getBurnoutMultiplier() {
		return burnoutMultiplier;
	}

	public final void setBurnoutMultiplier(float burnoutMultiplier) {
		this.burnoutMultiplier = burnoutMultiplier;
	}

	@Nonnull
	public final NBTTagCompound getAttributes() {
		return attributes;
	}

	@Override
	public final NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setTag("attributes", attributes);
		if (childRing != null) compound.setTag("child_ring", this.childRing.serializeNBT());
		if (module != null) compound.setString("module", module.getID());

		return compound;
	}

	@Override
	public final void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("attributes")) {
			attributes = nbt.getCompoundTag("attributes");
		}

		if (nbt.hasKey("child_ring")) {
			SpellRing childRing = deserializeRing(nbt.getCompoundTag("child_ring"));
			childRing.setParentRing(this);
			setChildRing(childRing);
		}

		if (nbt.hasKey("module")) this.module = ModuleRegistry.INSTANCE.getModule(nbt.getString("module"));
	}

	public final SpellRing copy() {
		return deserializeRing(serializeNBT());
	}
}
