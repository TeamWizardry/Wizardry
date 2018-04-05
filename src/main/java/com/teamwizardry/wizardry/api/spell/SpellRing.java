package com.teamwizardry.wizardry.api.spell;

import com.google.common.collect.ArrayListMultimap;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Modules ala IBlockStates
 */
public class SpellRing implements INBTSerializable<NBTTagCompound> {

	/**
	 * Store all processed modifier info and any extra you want here.
	 * Used by modifier processing and the WorktableGUI to save GUI in TileWorktable
	 */
	private NBTTagCompound informationTag = new NBTTagCompound();

	/**
	 * A map holding modifiers.
	 */
	@Nonnull
	private ArrayListMultimap<Operation, AttributeModifier> modifiers = ArrayListMultimap.create();

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

	private SpellRing() {	
	}

	public SpellRing(@Nonnull Module module) {
		setModule(module);
	}

	public static SpellRing deserializeRing(NBTTagCompound compound) {
		SpellRing ring = new SpellRing();
		ring.deserializeNBT(compound);

		SpellRing lastRing = ring;
		while (lastRing != null) {
			if (lastRing.getChildRing() == null) break;

			lastRing = lastRing.getChildRing();
		}
		if (lastRing != null) lastRing.updateColorChain();

		return ring;
	}

	/**
	 * Will run the spellData from this ring and down to it's children including rendering.
	 *
	 * @param data The SpellData object.
	 */
	public boolean runSpellRing(SpellData data) {
		if (module == null) return false;

		boolean success = module.castSpell(data, this) && !module.ignoreResult();
		if (success) {

			if (module != null) {
				module.sendRenderPacket(data, this);
			}

			if (getChildRing() != null) return getChildRing().runSpellRing(data);
		} else if (module.ignoreResult()) {
			if (module != null) {
				module.sendRenderPacket(data, this);
			}
		}

		return success;
	}

	public boolean isContinuous() {
		return this.module != null && module instanceof IContinuousModule && !isRunBeingOverriden();
	}

	public Set<SpellRing> getOverridingRings() {
		Set<SpellRing> set = new HashSet<>();
		if (module == null) return set;

		for (SpellRing child : getAllChildRings()) {
			if (child.getModule() == null) continue;
			if (isRunBeingOverridenBy(child.getModule())) set.add(child);
		}

		return set;
	}

	public boolean isRunBeingOverriden() {
		if (module == null) return false;

		for (SpellRing child : getAllChildRings()) {
			if (child.getModule() == null) continue;
			if (isRunBeingOverridenBy(child.getModule())) return true;
		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean isRenderBeingOverriden() {
		if (module == null) return false;

		for (SpellRing child : getAllChildRings()) {
			if (child.getModule() == null) continue;
			if (isRenderBeingOverridenBy(child.getModule())) return true;
		}

		return false;
	}

	/**
	 * If the given module is overriding this module's run
	 */
	public boolean isRunBeingOverridenBy(@Nonnull Module module) {
		return this.module != null && module instanceof ModuleEffect && ((ModuleEffect) module).hasRunOverrideFor(this.module);
	}

	@Nullable
	public BiConsumer<SpellData, SpellRing> getRunOverrideFrom(@Nonnull Module module) {
		if (!(module instanceof ModuleEffect)) return null;
		if (this.module == null) return null;

		return ((ModuleEffect) module).getRunOverrideFor(this.module);
	}

	/**
	 * If the given module is overriding this module's run
	 */
	@SideOnly(Side.CLIENT)
	public boolean isRenderBeingOverridenBy(@Nonnull Module module) {
		return this.module != null && module instanceof ModuleEffect && ((ModuleEffect) module).hasRenderOverrideFor(this.module);
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public BiConsumer<SpellData, SpellRing> getRenderOverrideFrom(@Nonnull Module module) {
		if (!(module instanceof ModuleEffect)) return null;
		if (this.module == null) return null;

		return ((ModuleEffect) module).getRenderOverrideFor(this.module);
	}

	public boolean taxCaster(SpellData data) {
		Entity caster = data.getCaster();
		if (caster == null) return false;

		double manaDrain = getManaDrain();
		double burnoutFill = getBurnoutFill();

		if (caster instanceof EntityLivingBase) {
			float reduction = getCapeReduction((EntityLivingBase) caster);
			manaDrain *= reduction;
			burnoutFill *= reduction;
		}

		CapManager manager = new CapManager(data.getCapability());

		manager.setEntity(caster);
		manager.setManualSync(true);

		boolean fail = false;
		if (manager.getMana() < manaDrain) fail = true;

		manager.removeMana(manaDrain);
		manager.addBurnout(burnoutFill);

		manager.sync();

		return !fail;
	}

	/**
	 * Get a modifier in this ring between the range. Returns the attribute value, modified by burnout and multipliers, for use in a spell.
	 *
	 * @param attribute The attribute you want. List in {@link AttributeRegistry} for default ones.
	 * @param data The data of the spell being cast, used to get caster-specific modifiers.
	 * @return The {@code double} potency of a modifier.
	 */
	public final double getAttributeValue(Attribute attribute, SpellData data) {
		if (module == null) return 0;

		double current = informationTag.getDouble(attribute.getNbtName());

		AttributeRange range = module.getAttributeRanges().get(attribute);

		current = MathHelper.clamp(current, range.min, range.max);
		current *= getPlayerBurnoutMultiplier(data);
		current *= getTrueAttributeValue(AttributeRegistry.POWER_MULTI);
		return current;
	}
	
	/**
	 * Get a modifier in this ring between the range. Returns the true attribute value, unmodified by any other attributes.
	 * 
	 * @param attribute The attribute you want. List in {@link AttributeRegistry} for default attributes.
	 * @return The {@code double} potency of a modifier.
	 */
	public final double getTrueAttributeValue(Attribute attribute)
	{
		if (module == null) return 0;
		
		double current = informationTag.getDouble(attribute.getNbtName());
		
		AttributeRange range = module.getAttributeRanges().get(attribute);
		
		return MathHelper.clamp(current, range.min, range.max);
	}

	/**
	 * Will process all modifiers and attributes set.
	 * WILL RESET THE INFORMATION TAG.
	 */
	public void processModifiers() {
		informationTag = new NBTTagCompound();

		if (module != null) {
			module.getAttributeRanges().forEach((attribute, range) -> {
				informationTag.setDouble(attribute.getNbtName(), range.base);
			});
		}

		for (Operation op : Operation.values()) {
			for (AttributeModifier modifier : modifiers.get(op)) {

				if (!informationTag.hasKey(modifier.getAttribute().getNbtName()))
					continue;
				double current = informationTag.getDouble(modifier.getAttribute().getNbtName());
				double newValue = modifier.apply(current);

				informationTag.setDouble(modifier.getAttribute().getNbtName(), newValue);

				Wizardry.logger.info(module == null ? "<null module>" : module.getID() + ": Attribute: " + modifier.getAttribute() + ": " + current + "-> " + newValue);
			}
		}
	}

	public final float getCapeReduction(EntityLivingBase caster) {
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

		setPrimaryColor(module.getPrimaryColor());
		setSecondaryColor(module.getSecondaryColor());
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

	public double getPowerMultiplier() {
		return getTrueAttributeValue(AttributeRegistry.POWER_MULTI);
	}

	public double getManaMultiplier() {
		return getTrueAttributeValue(AttributeRegistry.MANA_MULTI);
	}

	public double getBurnoutMultiplier() {
		return getTrueAttributeValue(AttributeRegistry.BURNOUT_MULTI);
	}

	public double getManaDrain() {
		return informationTag.getDouble(AttributeRegistry.MANA.getNbtName()) * getManaMultiplier();
	}

	public double getBurnoutFill() {
		return informationTag.getDouble(AttributeRegistry.BURNOUT.getNbtName()) * getBurnoutMultiplier();
	}

	@Nonnull
	public ArrayListMultimap<Operation, AttributeModifier> getModifiers() {
		return modifiers;
	}

	public void addModifier(ModuleModifier moduleModifier) {
		moduleModifier.getAttributes().forEach(modifier -> modifiers.put(modifier.getOperation(), modifier));
	}
	
	public void addModifier(AttributeModifier attributeModifier)
	{
		modifiers.put(attributeModifier.getOperation(), attributeModifier);
	}

	public int getCooldownTime(@Nullable SpellData data) {
		if (data != null && module instanceof IOverrideCooldown)
			return ((IOverrideCooldown) module).getNewCooldown(data, this);

		return (int) informationTag.getDouble(AttributeRegistry.COOLDOWN.getNbtName());
	}

	public int getCooldownTime() {
		return getCooldownTime(null);
	}

	public int getChargeUpTime() {
		return (int) informationTag.getDouble(AttributeRegistry.CHARGEUP.getNbtName());
	}

	/**
	 * All non mana, burnout, and multiplier attributes are reduced based on the caster's burnout level. This returns how much to reduce them by.
	 * 
	 * @return The INVERTED burnout multiplier.
	 */
	public double getPlayerBurnoutMultiplier(SpellData data)
	{
		Entity caster = data.getCaster();
		if (caster == null || caster instanceof EntityLivingBase && BaublesSupport.getItem((EntityLivingBase) caster, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return 1;
		CapManager manager = new CapManager(caster);
		
		double multiplier = manager.getBurnout() / manager.getMaxBurnout();
		double burnoutLimit = 0.5; //TODO: Probably put this into config, limit to [0, 1)
		return Math.min(1, 1 - (multiplier - burnoutLimit) / (1 - burnoutLimit));
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
			builder.append(ring.getModuleReadableName()).append(ring.getChildRing() == null ? "" : " > ");
			ring = ring.getChildRing();
		}

		return builder.toString();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		if (!modifiers.isEmpty()) {
			NBTTagList attribs = new NBTTagList();
			modifiers.forEach((op, modifier) -> {
				NBTTagCompound modifierCompound = new NBTTagCompound();

				modifierCompound.setInteger("operation", modifier.getOperation().ordinal());
				modifierCompound.setString("attribute", modifier.getAttribute().getNbtName());
				modifierCompound.setDouble("modifier", modifier.getModifier());
				attribs.appendTag(modifierCompound);
			});
			compound.setTag("modifiers", attribs);
		}

		compound.setTag("extra", informationTag);
		compound.setString("primary_color", String.valueOf(primaryColor.getRGB()));
		compound.setString("secondary_color", String.valueOf(secondaryColor.getRGB()));

		if (childRing != null) compound.setTag("child_ring", this.childRing.serializeNBT());
		if (module != null) compound.setString("module", module.getID());

		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("module")) this.module = Module.deserialize(nbt.getString("module"));
		if (nbt.hasKey("extra")) informationTag = nbt.getCompoundTag("extra");
		if (nbt.hasKey("primary_color")) primaryColor = Color.decode(nbt.getString("primary_color"));
		if (nbt.hasKey("secondary_color")) secondaryColor = Color.decode(nbt.getString("secondary_color"));

		if (nbt.hasKey("modifiers")) {
			modifiers.clear();
			for (NBTBase base : nbt.getTagList("modifiers", Constants.NBT.TAG_COMPOUND))
			{
				if (base instanceof NBTTagCompound) {
					NBTTagCompound modifierCompound = (NBTTagCompound) base;
					if (modifierCompound.hasKey("operation") && modifierCompound.hasKey("attribute") && modifierCompound.hasKey("modifier"))
					{
						Operation operation = Operation.values()[modifierCompound.getInteger("operation") % Operation.values().length];
						Attribute attribute = AttributeRegistry.getAttributeFromName(modifierCompound.getString("attribute"));
						double modifier = modifierCompound.getDouble("modifier");
						modifiers.put(operation, new AttributeModifier(attribute, modifier, operation));
					}
				}
			}
		}

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
