package com.teamwizardry.wizardry.api.spell.module;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.attribute.Operation;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;

/**
 * Created by LordSaad.
 */
public abstract class Module implements INBTSerializable<NBTTagCompound> {

	@Nonnull
	public NBTTagCompound attributes = new NBTTagCompound();
	@Nonnull
	public List<AttributeModifier> modifiers = new ArrayList<>();
	@Nullable
	public Module nextModule = null;
	private Color primaryColor = null;
	private Color secondaryColor = null;
	private int cooldownTime = 0;
	private int chargeupTime = 0;
	private ItemStack itemStack = ItemStack.EMPTY;
	private double multiplier = 1;

	public Module() {
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
	public abstract String getReadableName();

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public abstract String getDescription();

	public abstract boolean run(@Nonnull SpellData spell);

	/**
	 * This method runs client side when the spell runs. Spawn particles here.
	 */
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

	/**
	 * Use this to effectively run the entire module, rendering and all.
	 *
	 * @param data The spellData associated with it.
	 * @return If the spell has succeeded.
	 */
	public final boolean castSpell(@NotNull SpellData data) {
		if (this instanceof ILingeringModule)
			if (!SpellTicker.INSTANCE.ticker.containsKey(this))
				SpellTicker.INSTANCE.ticker.put(this, new Pair<>(data, ((ILingeringModule) this).lingeringTime(data)));

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

	public final void castParticles(@NotNull SpellData data) {
		Entity caster = data.getData(CASTER);
		Vec3d target = data.hasData(SpellData.DefaultKeys.ORIGIN) ?
				data.getData(SpellData.DefaultKeys.ORIGIN) : data.hasData(SpellData.DefaultKeys.TARGET_HIT) ?
				data.getData(SpellData.DefaultKeys.TARGET_HIT) : caster != null ?
				caster.getPositionVector() : null;

		if (target != null)
			PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(this, data),
					new NetworkRegistry.TargetPoint(data.world.provider.getDimension(), target.x, target.y, target.z, 60));
	}

	protected final double calcBurnoutPercent(@Nullable Entity player) {
		if (!(player instanceof EntityLivingBase)) return 1;
		if (player instanceof EntityPlayer && ((EntityPlayer) player).isCreative()) return 1;
		CapManager manager = new CapManager(player);
		return ((manager.getMaxBurnout() - manager.getBurnout()) / (manager.getMaxBurnout() * 1.0));
	}

	public final boolean runNextModule(@NotNull SpellData data) {
		if (nextModule != null) {
			nextModule.setMultiplier(nextModule.getMultiplier() * getMultiplier());
		}
		return nextModule != null && nextModule.castSpell(data);
	}

	protected final void forceCastNextModuleParticles(@NotNull SpellData data) {
		if (nextModule != null) nextModule.castParticles(data);
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

	protected final double getModifierPower(SpellData data, String attribute, double min, double max, boolean multiplyMultiplier, boolean multiplyBurnout) {
		Entity caster = data.getData(CASTER);
		return (attributes.hasKey(attribute) ? Math.min(Math.max(min, min + attributes.getDouble(attribute)), max) : min) * (multiplyMultiplier ? getMultiplier() : 1) * (multiplyBurnout ? calcBurnoutPercent(caster) : 1);
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

	public void processModifiers() {
		HashMultimap<Operation, AttributeModifier> sortedMap = HashMultimap.create();
		for (AttributeModifier modifier : modifiers)
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
		compound.setDouble("mana_drain", getManaDrain());
		compound.setDouble("burnout_fill", getBurnoutFill());
		compound.setDouble("chargeup_time", getChargeupTime());
		compound.setDouble("cooldown_time", getCooldownTime());
		compound.setDouble("multiplier", getMultiplier());

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
	}
}
