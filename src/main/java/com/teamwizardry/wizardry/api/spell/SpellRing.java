package com.teamwizardry.wizardry.api.spell;

import com.google.common.collect.ArrayListMultimap;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry.Attribute;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideHandler;
import com.teamwizardry.wizardry.api.util.FixedPointUtils;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Modules ala IBlockStates. <br />
 * <b>IMPORTANT</b>: All new NBT fields, which are float or double should be stored in their fixed point form.
 * Also lists and compounds must be sorted by some arbitrary, but fixed, order.
 * This way it is assured that the {@link NBTBase#equals} in the resulting NBT is reliable.
 */
public class SpellRing implements INBTSerializable<NBTTagCompound> {

	/**
	 * Mostly used as a cache key. <br/>
	 * <b>NOTE</b>: Must be initialized only by {@link #serializeNBT()} to have a normalized key!
	 */
	private NBTTagCompound serializedTag = null;

	/**
	 * Store all processed modifier info and any extra you want here.
	 * Used by modifier processing and the WorktableGUI to save GUI in TileWorktable <br/>
	 * <b>NOTE</b>: Must be initialized only by {@link #processModifiers()} or {@link #deserializeNBT(NBTTagCompound)}
	 * to have normalized keys, used for cache nbt!
	 */
	private NBTTagCompound informationTag = new NBTTagCompound();

	/**
	 * A map holding compile time modifiers.
	 */
	@Nonnull
	private ArrayListMultimap<Operation, AttributeModifierSpellRing> compileTimeModifiers = ArrayListMultimap.create();

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
	private ModuleInstance module;

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

	/**
	 * A module override handler.
	 */
	private ModuleOverrideHandler lazy_overrideHandler = null;    // "lazy" means, that access to variable should be done only over getter

	/**
	 * Differentiate between spellrings easily.
	 */
	@Nonnull
	private UUID uniqueID = UUID.randomUUID();

	/**
	 * The constructor.<br/>
	 * <b>NOTE</b>: Called only for deserialization.
	 */
	private SpellRing() {
	}

	/**
	 * The constructor. <br/>
	 * <b>NOTE</b>: Called only by {@link SpellBuilder}.
	 *
	 * @param module the module to construct the spell from.
	 */
	SpellRing(@Nonnull ModuleInstance module) {
		setModule(module);
	}

	/**
	 * Deserializes a spell ring from given NBT. <br/>
	 * Please don't misuse this.
	 *
	 * @param compound the tag compound to deserialize from
	 * @return A created spell ring.
	 */
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
	 * Returns a normalized NBT tag compound for information from a source.
	 *
	 * @param informationMap a source key-value list, storing information.
	 * @return normalized information NBT compound
	 */
	private static NBTTagCompound sortInformationTag(Map<String, Float> informationMap) {
		ArrayList<Pair<String, Float>> sortedInformationList = new ArrayList<>(informationMap.size());
		informationMap.forEach((key, val) -> sortedInformationList.add(Pair.of(key, FixedPointUtils.doubleToFixed(val))));
		sortedInformationList.sort(Comparator.comparing(Pair::getKey));

		NBTTagCompound newInformationTag = new NBTTagCompound();
		for (Pair<String, Float> entry : sortedInformationList) {
			FixedPointUtils.setFixedToNBT(newInformationTag, entry.getKey(), entry.getValue());
		}

		return newInformationTag;
	}

	/**
	 * Returns a normalized NBT tag compound for information from a source.
	 *
	 * @param informationNbt a source NBT compound
	 * @return normalized information NBT compound
	 */
	private static NBTTagCompound sortInformationTag(NBTTagCompound informationNbt) {
		ArrayList<Pair<String, Float>> sortedInformationList = new ArrayList<>(informationNbt.getSize());
		for (String key : informationNbt.getKeySet()) {
			sortedInformationList.add(Pair.of(key, FixedPointUtils.getFixedFromNBT(informationNbt, key)));
		}
		sortedInformationList.sort(Comparator.comparing(Pair::getKey));

		NBTTagCompound newInformationTag = new NBTTagCompound();
		for (Pair<String, Float> entry : sortedInformationList) {
			FixedPointUtils.setFixedToNBT(newInformationTag, entry.getKey(), entry.getValue());
		}

		return newInformationTag;
	}

	/**
	 * Returns a normalized modifier tag list.
	 *
	 * @param modifierList the modifier list.
	 * @return the normalized tag list.
	 */
	private static NBTTagList sortModifierList(List<NBTTagCompound> modifierList) {
		NBTTagList attribs = new NBTTagList();

		modifierList.sort(SpellRing::compareModifierCompounds);
		for (NBTTagCompound modifierCompound : modifierList) {
			attribs.appendTag(modifierCompound);
		}

		return attribs;
	}

	private static int compareModifierCompounds(NBTTagCompound nbt1, NBTTagCompound nbt2) {
		int op1 = nbt1.getInteger("operation");
		int op2 = nbt2.getInteger("operation");

		if (op1 != op2)
			return (op1 - op2) > 0 ? 1 : -1;

		return nbt1.getString("attribute").compareTo(nbt2.getString("attribute"));
	}

	/**
	 * Will run the spellData from this ring and down to it's children including rendering.
	 *
	 * @param world
	 * @param data                 The SpellData object.
	 * @param makeSpellIndependant This field will make the spell run with another unique id so it doesn't
	 */
	public void runSpellRing(@Nonnull World world, SpellData data, boolean makeSpellIndependant) {
		if (module == null)
			return;

		SpellRing modifRing = makeSpellIndependant ? changeUUID() : this;
		SpellData modifData = makeSpellIndependant ? data.copy() : data;

		if (modifData.getCaster(world) != null)
			modifData.processCastTimeModifiers(modifData.getCaster(world), modifRing);

		boolean success = module.castSpell(world, modifData, modifRing, true);

		if (success && module.shouldRunChildren()) {
			if (getChildRing() != null) {
				getChildRing().runSpellRing(world, modifData, makeSpellIndependant);
			}
		}
	}

	/**
	 * Get a modifier in this ring between the range. Returns the true attribute value, unmodified by any other attributes.
	 *
	 * @param attribute The attribute you want. List in {@link AttributeRegistry} for default attributes.
	 * @return The {@code double} potency of a modifier.
	 */
	public final double getTrueAttributeValue(Attribute attribute) {
		if (module == null) return 0;

		double current = FixedPointUtils.getDoubleFromNBT(informationTag, attribute.getNbtName());

		AttributeRange range = module.getAttributeRanges().get(attribute);

		return MathHelper.clamp(current, range.min, range.max);
	}

	//TODO: pearl holders
	public boolean taxCaster(@Nonnull World world, SpellData data, double multiplier, boolean failSound) {
		Entity caster = data.getCaster(world);
		if (caster == null) return false;

		double manaDrain = getManaDrain(data) * multiplier;
		double burnoutFill = getBurnoutFill(data) * multiplier;

		boolean fail = false;

		try (CapManager.CapManagerBuilder mgr = CapManager.forObject(caster)) {
			if (mgr.getMana() < manaDrain) fail = true;

			mgr.removeMana(manaDrain);
			mgr.addBurnout(burnoutFill);
		}

		if (fail && failSound) {

			Vec3d origin = data.getOriginWithFallback(world);
			if (origin != null)
				world.playSound(null, new BlockPos(origin), ModSounds.SPELL_FAIL, SoundCategory.NEUTRAL, 1f, 1f);
		}

		return !fail;
	}

	public boolean taxCaster(@Nonnull World world, SpellData data, boolean failSound) {
		return taxCaster(world, data, 1, failSound);
	}

	public boolean isContinuous() {
		if (module != null) {
			return module.getModuleClass() instanceof IContinuousModule;
		}
		return false;
	}

//	public final float getCapeReduction(EntityLivingBase caster) {
//		ItemStack stack = BaublesSupport.getItem(caster, ModItems.CAPE);
//		if (stack != ItemStack.EMPTY) {
//			float time = NBTHelper.getInt(stack, "maxTick", 0);
//			return (float) MathHelper.clamp(1 - (time / 1000000.0), 0.25, 1);
//		}
//		return 1;
//	}

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

	/**
	 * Get a modifier in this ring between the range. Returns the attribute value, modified by burnout and multipliers, for use in a spell.
	 *
	 *
	 * @param world
	 * @param attribute The attribute you want. List in {@link AttributeRegistry} for default attributes.
	 * @param data      The data of the spell being cast, used to get caster-specific modifiers.
	 * @return The {@code double} potency of a modifier.
	 */
	public final float getAttributeValue(World world, Attribute attribute, SpellData data) {
		if (module == null) return 0;

		float current = FixedPointUtils.getDoubleFromNBT(informationTag, attribute.getNbtName());

		AttributeRange range = module.getAttributeRanges().get(attribute);

		current = MathHelper.clamp(current, range.min, range.max);
		current = data.getCastTimeValue(attribute, current);
		current *= getPlayerBurnoutMultiplier(world, data);
		current *= getPowerMultiplier();

		return current;
	}

	@Nullable
	public SpellRing getChildRing() {
		return childRing;
	}

	/**
	 * Will process all modifiers and attributes set.
	 * WILL RESET THE INFORMATION TAG. <br/>
	 * <b>NOTE</b>: Called only by {@link SpellBuilder}.
	 */
	void processModifiers() {
		HashMap<String, Float> informationMap = new HashMap<>();

		if (module != null) {
			module.getAttributeRanges().forEach((attribute, range) -> {
				informationMap.put(attribute.getNbtName(), range.base);
			});
		}

		for (Operation op : Operation.values()) {
			for (AttributeModifier modifier : compileTimeModifiers.get(op)) {

				float current = informationMap.get(modifier.getAttribute().getNbtName());

				float newValue = modifier.apply(current);

				informationMap.put(modifier.getAttribute().getNbtName(), newValue);

				if (ConfigValues.debugInfo)
					Wizardry.LOGGER.info(module == null ? "<null module>" : module.getSubModuleID() + ": Attribute: " + modifier.getAttribute() + ": " + current + "-> " + newValue);
			}
		}

		// Output a sorted list of tags to informationTag
		informationTag = sortInformationTag(informationMap);
	}

	@Nullable
	public SpellRing getParentRing() {
		return parentRing;
	}

	public SpellRing changeUUID() {
		uniqueID = UUID.randomUUID();
		return this;
	}

	/**
	 * Sets a child ring.<br/>
	 * <b>NOTE</b>: Called only by {@link SpellBuilder}.
	 *
	 * @param childRing the child ring.
	 */
	void setChildRing(@Nonnull SpellRing childRing) {
		this.childRing = childRing;
	}

	@Nullable
	public ModuleInstance getModule() {
		return module;
	}

	/**
	 * Sets a parent ring.<br/>
	 * <b>NOTE</b>: Called only by {@link SpellBuilder}.
	 *
	 * @param parentRing the parent ring to set
	 */
	void setParentRing(@Nullable SpellRing parentRing) {
		this.parentRing = parentRing;
	}

	@Nonnull
	public Color getPrimaryColor() {
		return primaryColor;
	}

	/**
	 * Returns the handler to invoke overrides of the whole spell chain.
	 *
	 * @return the override handler of the spell chain.
	 */
	@Nonnull
	public synchronized ModuleOverrideHandler getOverrideHandler() {
		if (lazy_overrideHandler == null) {
			if (parentRing != null)
				lazy_overrideHandler = parentRing.getOverrideHandler();
			else {
				lazy_overrideHandler = new ModuleOverrideHandler(this);
			}
		}

		return lazy_overrideHandler;
	}

	@Nonnull
	public Color getSecondaryColor() {
		return secondaryColor;
	}

	/**
	 * Sets a module.<br/>
	 * <b>NOTE</b>: Called from constructor and implicitly only by {@link SpellBuilder}.
	 *
	 * @param module the module to set
	 */
	void setModule(@Nonnull ModuleInstance module) {
		this.module = module;

		setPrimaryColor(module.getPrimaryColor());
		setSecondaryColor(module.getSecondaryColor());
	}

	/**
	 * Propagates color settings to parent.<br/>
	 * <b>NOTE</b>: Called only by {@link SpellBuilder}.
	 */
	void updateColorChain() {
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

	/**
	 * Sets a primary color.<br/>
	 * <b>NOTE</b>: Called implicitly only by {@link SpellBuilder}.
	 *
	 * @param primaryColor the primary color to set
	 */
	void setPrimaryColor(@Nonnull Color primaryColor) {
		this.primaryColor = primaryColor;
		updateColorChain();
	}

	/**
	 * Sets a secondary color.<br/>
	 * <b>NOTE</b>: Called implicitly only by {@link SpellBuilder}.
	 *
	 * @param secondaryColor
	 */
	void setSecondaryColor(@Nonnull Color secondaryColor) {
		this.secondaryColor = secondaryColor;
	}

	/**
	 * Returns mana drain value. If spell data is passed, then the value is modified additionally by runtime data,
	 * e.g. by cape and halo attributes of caster.
	 *
	 * @param data runtime data of active spell. Can be <code>null</code>.
	 * @return mana drain value
	 */
	public double getManaDrain(SpellData data) {
		float value = FixedPointUtils.getDoubleFromNBT(informationTag, AttributeRegistry.MANA.getNbtName());
		if (data != null)
			value = data.getCastTimeValue(AttributeRegistry.MANA, value);
		return value * getManaMultiplier();
	}

	/**
	 * Returns burnout fill value. If spell data is passed, then the value is modified additionally by runtime data,
	 * e.g. by cape and halo attributes of caster.
	 *
	 * @param data runtime data of active spell. Can be <code>null</code>.
	 * @return burnout fill value
	 */
	public double getBurnoutFill(SpellData data) {
		float value = FixedPointUtils.getDoubleFromNBT(informationTag, AttributeRegistry.BURNOUT.getNbtName());
		if (data != null)
			value = data.getCastTimeValue(AttributeRegistry.BURNOUT, value);
		return value * getBurnoutMultiplier();
	}

	/**
	 * Adds a modifier module to spell ring. <br/>
	 * <b>NOTE</b>: In actual implementation, only attributes are overtaken. <br/>
	 * <b>NOTE</b>: Called only by {@link SpellBuilder}.
	 *
	 * @param moduleModifier the modifier module instance
	 */
	void addModifier(ModuleInstanceModifier moduleModifier) {
		moduleModifier.getAttributes().forEach(modifier -> compileTimeModifiers.put(modifier.getOperation(), new AttributeModifierSpellRing(modifier)));
	}

	/**
	 * Adds an attribute modifier to spell ring. <br/>
	 * <b>NOTE</b>: Called only by {@link SpellBuilder}.
	 *
	 * @param attributeModifier the attribute modifier
	 */
	void addModifier(AttributeModifier attributeModifier) {
		compileTimeModifiers.put(attributeModifier.getOperation(), new AttributeModifierSpellRing(attributeModifier));
	}

	public int getChargeUpTime() {
		return (int) FixedPointUtils.getDoubleFromNBT(informationTag, AttributeRegistry.CHARGEUP.getNbtName());
	}

	public int getCooldownTime(@Nullable World world, @Nullable SpellData data) {
		if (module != null && data != null && world != null && module.getModuleClass() instanceof IOverrideCooldown)
			return ((IOverrideCooldown) module.getModuleClass()).getNewCooldown(world, data, this);

		return (int) FixedPointUtils.getDoubleFromNBT(informationTag, AttributeRegistry.COOLDOWN.getNbtName());
	}

	@Nullable
	public String getModuleReadableName() {
		return module != null ? module.getReadableName() : null;
	}

	public NBTTagCompound getInformationTag() {
		return informationTag;
	}

	public int getCooldownTime() {
		return getCooldownTime(null, null);
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

	/**
	 * All non mana, burnout, and multiplier attributes are reduced based on the caster's burnout level. This returns how much to reduce them by.
	 *
	 * @return The INVERTED burnout multiplier.
	 */
	public double getPlayerBurnoutMultiplier(World world, SpellData data) {
		Entity caster = data.getCaster(world);
		if (caster == null || caster instanceof EntityLivingBase && BaublesSupport.getItem((EntityLivingBase) caster, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return 1;

		double multiplier = CapManager.getBurnout(caster) / CapManager.getMaxBurnout(caster);
		double burnoutLimit = 0.5; //TODO: Probably put this into config, limit to [0, 1)
		return Math.min(1, 1 - (multiplier - burnoutLimit) / (1 - burnoutLimit));
	}

	@Nonnull
	public UUID getUniqueID() {
		return uniqueID;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		if (serializedTag == null) {
			serializedTag = internalSerializeNBT();
		}
		return serializedTag;
	}

	/**
	 * Core of {@link #serializeNBT()}. Doesn't set serializedTag.
	 *
	 * @return the serialized tag.
	 */
	private NBTTagCompound internalSerializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		if (!compileTimeModifiers.isEmpty()) {
			// Retrieve all modifier compounds
			ArrayList<NBTTagCompound> modifierList = new ArrayList<>(compileTimeModifiers.size());
			compileTimeModifiers.forEach((op, modifier) -> {
				NBTTagCompound modifierCompound = new NBTTagCompound();

				modifierCompound.setInteger("operation", modifier.getOperation().ordinal());
				modifierCompound.setString("attribute", modifier.getAttribute().getNbtName());
				FixedPointUtils.setFixedToNBT(modifierCompound, "modifier", modifier.getModifierFixed());
				modifierList.add(modifierCompound);
			});

			// Sort and store them
			NBTTagList attribs = sortModifierList(modifierList);
			compound.setTag("modifiers", attribs);
		}

		compound.setTag("extra", informationTag);
		compound.setString("primary_color", String.valueOf(primaryColor.getRGB()));
		compound.setString("secondary_color", String.valueOf(secondaryColor.getRGB()));

		if (childRing != null) compound.setTag("child_ring", this.childRing.serializeNBT());
		if (module != null) compound.setString("module", module.getSubModuleID());

		compound.setString("uuid", uniqueID.toString());

		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// NOTE: Don't store nbt argument to serializedNBT. This one must be generated only by serializeNBT()

		if (nbt.hasKey("module")) this.module = ModuleInstance.deserialize(nbt.getString("module"));
		if (nbt.hasKey("extra")) informationTag = sortInformationTag(nbt.getCompoundTag("extra"));
		if (nbt.hasKey("primary_color")) primaryColor = Color.decode(nbt.getString("primary_color"));
		if (nbt.hasKey("secondary_color")) secondaryColor = Color.decode(nbt.getString("secondary_color"));

		if (nbt.hasKey("modifiers")) {
			compileTimeModifiers.clear();
			for (NBTBase base : nbt.getTagList("modifiers", Constants.NBT.TAG_COMPOUND)) {
				if (base instanceof NBTTagCompound) {
					NBTTagCompound modifierCompound = (NBTTagCompound) base;
					if (modifierCompound.hasKey("operation") && modifierCompound.hasKey("attribute") && modifierCompound.hasKey("modifier")) {
						Operation operation = Operation.values()[modifierCompound.getInteger("operation") % Operation.values().length];
						Attribute attribute = AttributeRegistry.getAttributeFromName(modifierCompound.getString("attribute"));

						float modifierFixed = FixedPointUtils.getFixedFromNBT(modifierCompound, "modifier");
						compileTimeModifiers.put(operation, new AttributeModifierSpellRing(attribute, modifierFixed, operation));
					}
				}
			}
		}

		if (nbt.hasKey("child_ring")) {
			SpellRing childRing = deserializeRing(nbt.getCompoundTag("child_ring"));
			childRing.setParentRing(this);
			setChildRing(childRing);
		}

		if (nbt.hasKey("uuid")) uniqueID = UUID.fromString(nbt.getString("uuid"));

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SpellRing ring = (SpellRing) o;
		return Objects.equals(uniqueID, ring.uniqueID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueID);
	}

	////////////////////

	/**
	 * Storage class for attribute modifiers. An extension class to {@link AttributeModifier}
	 * is necessary to store values in their fixed value form to avoid conversions and roundup errors. <br />
	 * <b>NOTE</b>: Helps to avoid using double values in NBT. As the {@link #equals(Object)} method isn't reliable for them.
	 *
	 * @author Avatair
	 */
	private static class AttributeModifierSpellRing extends AttributeModifier {

		private float modifierFixed;

		public AttributeModifierSpellRing(AttributeModifier modifier) {
			this(modifier.getAttribute(), modifier.getModifier(), modifier.getOperation());
		}

		public AttributeModifierSpellRing(Attribute attribute, float modifier, Operation op) {
			super(attribute, modifier, op);
			this.modifierFixed = FixedPointUtils.doubleToFixed(modifier);
		}

		public AttributeModifierSpellRing(Attribute attribute, long modifierFixed, Operation op) {
			super(attribute, FixedPointUtils.fixedToDouble(modifierFixed), op);
		}

		public float getModifierFixed() {
			return this.modifierFixed;
		}

		public void setModifier(float newValue) {
			this.modifierFixed = FixedPointUtils.doubleToFixed(newValue);
			super.setModifier(newValue);
		}

		@Override
		public AttributeModifier copy() {
			return new AttributeModifierSpellRing(getAttribute(), modifierFixed, getOperation());
		}
	}
}
