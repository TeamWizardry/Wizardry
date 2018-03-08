package com.teamwizardry.wizardry.api.spell.module;

import com.google.common.collect.ArrayListMultimap;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import com.teamwizardry.wizardry.init.ModItems;
import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.MAX_TIME;

/**
 * Created by Demoniaque.
 */
public abstract class Module implements INBTSerializable<NBTTagCompound> {

	/**
	 * Stores the actual modifier data
	 */
	@Nonnull
	public NBTTagCompound attributes = new NBTTagCompound();
	/**
	 * Temporarily stores modifiers before spell construction
	 */
	@Nonnull
	public List<AttributeModifier> modifiersToApply = new ArrayList<>();
	/**
	 * Stores a list of attribute modifiers to use in spell construction, used by Modifier modules
	 */
	@Nonnull
	public List<AttributeModifier> modifiers = new ArrayList<>();
	@Nullable
	public Module prevModule = null;
	@Nullable
	public Module nextModule = null;
	private Color primaryColor = null;
	private Color secondaryColor = null;
	private int cooldownTime = 0;
	private int chargeupTime = 0;
	private ItemStack itemStack = ItemStack.EMPTY;
	private double multiplier = 1;
	private boolean isHead = false;

	public Module() {
	}

	public static void processColor(Module module) {
		if (module == null) return;

		if (module.nextModule == null) {
			if (module.getPrimaryColor() == null) {
				module.setPrimaryColor(Color.WHITE);
			}
			if (module.getSecondaryColor() == null) {
				module.setSecondaryColor(Color.WHITE);
			}
			return;
		}

		processColor(module.nextModule);

		if (module.getPrimaryColor() == null) {
			module.setPrimaryColor(module.nextModule.getPrimaryColor());
		}
		if (module.getSecondaryColor() == null) module.setSecondaryColor(module.nextModule.getSecondaryColor());

	}

	@Override
	public String toString() {
		return getID();
	}

	/**
	 * The type of module this module is.
	 *
	 * @return A ModuleType representing the type of module this is.
	 */
	@Nonnull
	public abstract ModuleType getModuleType();

	/**
	 * A lower case snake_case string id that reflects the module to identify it during serialization/deserialization.
	 *
	 * @return A lower case snake_case string.
	 */
	@Nonnull
	public abstract String getID();

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 */
	@Nonnull
	public String getReadableName() {
		return LibrarianLib.PROXY.translate(getNameKey());
	}

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 */
	@Nonnull
	public String getNameKey() {
		return "wizardry.spell." + getID() + ".name";
	}

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public String getDescription() {
		return LibrarianLib.PROXY.translate(getDescriptionKey());
	}

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public String getDescriptionKey() {
		return "wizardry.spell." + getID() + ".desc";
	}

	/**
	 * Specify all applicable modifiers that can be applied to this module.
	 *
	 * @return Any set with applicable ModuleModifiers.
	 */
	@Nullable
	public ModuleModifier[] applicableModifiers() {
		return null;
	}

	/**
	 * Only return false if the spell cannot be taxed from mana. Return true otherwise.
	 */
	public abstract boolean run(@Nonnull SpellData spell);

	/**
	 * This method runs client side when the spell runs. Spawn particles here.
	 */
	@SideOnly(Side.CLIENT)
	public abstract void runClient(@Nonnull SpellData spell);

	@Nullable
	public final Color getPrimaryColor() {
		return primaryColor;
	}

	public final void setPrimaryColor(Color primaryColor) {
		this.primaryColor = primaryColor;
	}

	@Nullable
	public final Color getSecondaryColor() {
		return secondaryColor;
	}

	public final void setSecondaryColor(Color secondaryColor) {
		this.secondaryColor = secondaryColor;
	}

	public final double getManaDrain() {
		return attributes.getDouble(Attributes.MANA);
	}

	public final void setManaDrain(double manaDrain) {
		attributes.setDouble(Attributes.MANA, manaDrain);
	}

	public final float getReductionMultiplier(EntityLivingBase caster) {
		ItemStack stack = BaublesSupport.getItem(caster, ModItems.CAPE);
		if (stack != null) {
			float time = ItemNBTHelper.getInt(stack, "maxTick", 0);
			// Max reduction = 0.25
			return (float) MathHelper.clamp(1 - (time / 1000000.0), 1, 0.25);
		}
		return 1;
	}

	public final double getBurnoutFill() {
		return attributes.getDouble(Attributes.BURNOUT);
	}

	public final void setBurnoutFill(double burnoutFill) {
		attributes.setDouble(Attributes.BURNOUT, burnoutFill);
	}

	public final int getCooldownTime() {
		return cooldownTime;
	}

	public final void setCooldownTime(int cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public final int getChargeupTime() {
		return chargeupTime;
	}

	public final void setChargeupTime(int chargeupTime) {
		this.chargeupTime = chargeupTime;
	}

	public final ItemStack getItemStack() {
		return itemStack;
	}

	public final void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public final double getMultiplier() {
		return multiplier;
	}

	public final void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public final boolean isHead() {
		return isHead;
	}

	public final void setIsHead(boolean isHead) {
		this.isHead = isHead;
	}

	/**
	 * Use this to effectively run the entire module, rendering and all.
	 *
	 * @param data The spellData associated with it.
	 * @return If the spell has succeeded.
	 */
	public final boolean castSpell(@Nonnull SpellData data) {
		if (this instanceof ILingeringModule)
			if (!SpellTicker.ticker.containsKey(this)) {
				data.addData(MAX_TIME, ((ILingeringModule) this).lingeringTime(data));
				SpellTicker.ticker.put(this, new Pair<>(data, ((ILingeringModule) this).lingeringTime(data)));
			}
		//data.addData(SpellData.DefaultKeys.STRENGTH, calculateStrength(data) * getMultiplier());

		SpellCastEvent event = new SpellCastEvent(this, data);
		MinecraftForge.EVENT_BUS.post(event);

		if (!event.isCanceled()) {
			boolean success = run(data);
			if (event.castParticles) castParticles(data);
			return success;
		} else {
			if (event.castParticles) castParticles(data);
			return false;
		}
	}

	public final void castParticles(@Nonnull SpellData data) {
		Entity caster = data.getData(CASTER);
		Vec3d target = data.hasData(SpellData.DefaultKeys.ORIGIN) ?
				data.getData(SpellData.DefaultKeys.ORIGIN) : data.hasData(SpellData.DefaultKeys.TARGET_HIT) ?
				data.getData(SpellData.DefaultKeys.TARGET_HIT) : caster != null ?
				caster.getPositionVector() : null;

		if (target != null)
			PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(this, data),
					new NetworkRegistry.TargetPoint(data.world.provider.getDimension(), target.x, target.y, target.z, 60));
	}

	protected final double calcBurnoutPercent(@Nullable Entity entity) {
		if (entity == null) return 1;
		//if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) return 1;
		CapManager manager = new CapManager(entity);
		return ((manager.getMaxBurnout() - manager.getBurnout()) / (manager.getMaxBurnout() * 1.0));
	}

	protected final boolean runNextModule(@Nonnull SpellData data) {
		if (nextModule != null) {
			nextModule.setMultiplier(nextModule.getMultiplier() * getMultiplier());
		}
		return nextModule != null && nextModule.castSpell(data);
	}

	/**
	 * Get all the children modules of this module including itself.
	 */
	public final Set<Module> getAllChildModules() {
		Set<Module> modules = new HashSet<>();
		Module tempModule = this;
		while (tempModule != null) {
			modules.add(tempModule);
			tempModule = tempModule.nextModule;
		}
		return modules;
	}

	protected final double getModifier(SpellData data, String attribute, double min, double max) {
		Entity caster = data.getData(CASTER);
		double burnout = calcBurnoutPercent(caster);
		return (attributes.hasKey(attribute) ? MathHelper.clamp(min + attributes.getDouble(attribute), min, max) : min) * getMultiplier() * burnout;
	}

	public void processModifiers() {
		ArrayListMultimap<Operation, AttributeModifier> sortedMap = ArrayListMultimap.create();
		for (AttributeModifier modifier : modifiersToApply)
			sortedMap.put(modifier.getOperation(), modifier);

		for (Operation op : Operation.values()) {
			for (AttributeModifier modifier : sortedMap.get(op)) {
				String attribute = modifier.getAttribute();
				double current = attributes.getDouble(attribute);
				double newValue = modifier.apply(current);
				attributes.setDouble(attribute, newValue);
				Wizardry.logger.info(getID() + ": Attribute: " + attribute + ": " + current + "-> " + newValue);
			}
		}
	}

	protected final <T extends Module> Module cloneModule(T toCloneTo) {
		toCloneTo.attributes = attributes.copy();
		toCloneTo.modifiers = new ArrayList<>(modifiers);
		toCloneTo.setPrimaryColor(getPrimaryColor());
		toCloneTo.setSecondaryColor(getSecondaryColor());
		toCloneTo.setBurnoutFill(getBurnoutFill());
		toCloneTo.setManaDrain(getManaDrain());
		toCloneTo.setCooldownTime(getCooldownTime());
		toCloneTo.setChargeupTime(getChargeupTime());
		toCloneTo.setItemStack(getItemStack());
		toCloneTo.setMultiplier(getMultiplier());
		return toCloneTo;
	}

	public abstract Module copy();

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setTag("attributes", attributes);

		if (nextModule != null) compound.setTag("next_module", nextModule.serializeNBT());

		compound.setString("id", getID());

		if (getManaDrain() != 0) compound.setDouble("mana_drain", getManaDrain());
		if (getBurnoutFill() != 0) compound.setDouble("burnout_fill", getBurnoutFill());
		if (getChargeupTime() != 0) compound.setDouble("chargeup_time", getChargeupTime());
		if (getCooldownTime() != 0) compound.setDouble("cooldown_time", getCooldownTime());
		if (getManaDrain() != 1) compound.setDouble("multiplier", getMultiplier());
		if (isHead) compound.setBoolean("is_head", true);

		if (getItemStack() != null) compound.setTag("item_stack", getItemStack().serializeNBT());
		if (getPrimaryColor() != null) compound.setInteger("primary_color", getPrimaryColor().getRGB());
		if (getSecondaryColor() != null) compound.setInteger("secondary_color", getSecondaryColor().getRGB());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("next_module")) {
			Module tempModule = ModuleRegistry.INSTANCE.getModule(nbt.getCompoundTag("next_module").getString("id"));
			if (tempModule != null) {
				nextModule = tempModule.copy();
				if (nextModule != null) nextModule.deserializeNBT(nbt.getCompoundTag("next_module"));
			} else nextModule = null;
		} else nextModule = null;

		if (nbt.hasKey("attributes")) attributes = nbt.getCompoundTag("attributes");
		else attributes = new NBTTagCompound();

		if (nbt.hasKey("primary_color")) setPrimaryColor(new Color(nbt.getInteger("primary_color")));
		if (nbt.hasKey("secondary_color")) setSecondaryColor(new Color(nbt.getInteger("secondary_color")));
		if (nbt.hasKey("mana_drain")) setManaDrain(nbt.getDouble("mana_drain"));
		if (nbt.hasKey("burnout_fill")) setBurnoutFill(nbt.getDouble("burnout_fill"));
		if (nbt.hasKey("chargeup_time")) setChargeupTime(nbt.getInteger("chargeup_time"));
		if (nbt.hasKey("cooldown_time")) setCooldownTime(nbt.getInteger("cooldown_time"));
		if (nbt.hasKey("item_stack")) setItemStack(new ItemStack(nbt.getCompoundTag("item_stack")));
		if (nbt.hasKey("multiplier")) setMultiplier(nbt.getDouble("multiplier"));
		if (nbt.hasKey("is_head")) isHead = true;
	}
}
