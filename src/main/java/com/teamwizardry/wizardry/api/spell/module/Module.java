package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public abstract class Module {

	private final List<AttributeModifier> attributes = new ArrayList<>();
	private Color primaryColor;
	private Color secondaryColor;
	private int cooldownTime;
	private int chargeupTime;
	private double manaDrain;
	private double burnoutFill;
	private ItemStack itemStack;
	private float powerMultiplier;
	private float manaMultiplier;
	private float burnoutMultiplier;

	@Nullable
	public static Module deserialize(NBTTagCompound compound) {
		if (compound.hasKey("id")) return ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
		return null;
	}

	public final void init(ItemStack itemStack,
	                       double manaDrain,
	                       double burnoutFill,
	                       Color primaryColor,
	                       Color secondaryColor,
	                       float powerMultiplier,
	                       float manaMultiplier,
	                       float burnoutMultiplier,
	                       int cooldownTime,
	                       int chargeupTime) {
		this.itemStack = itemStack;
		this.manaDrain = manaDrain;
		this.burnoutFill = burnoutFill;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.powerMultiplier = powerMultiplier;
		this.manaMultiplier = manaMultiplier;
		this.burnoutMultiplier = burnoutMultiplier;
		this.cooldownTime = cooldownTime;
		this.chargeupTime = chargeupTime;
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

	@Override
	public final String toString() {
		return getID();
	}

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 */
	@Nonnull
	public final String getReadableName() {
		return LibrarianLib.PROXY.translate(getNameKey());
	}

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 */
	@Nonnull
	public final String getNameKey() {
		return "wizardry.spellData." + getID() + ".name";
	}

	/**
	 * The description of what this module does.
	 */
	@Nonnull
	public final String getDescription() {
		return LibrarianLib.PROXY.translate(getDescriptionKey());
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
	 * The description of what this module does.
	 */
	@Nonnull
	public final String getDescriptionKey() {
		return "wizardry.spellData." + getID() + ".desc";
	}

	@Nonnull
	public final Color getPrimaryColor() {
		return primaryColor;
	}

	public final double getBurnoutFill() {
		return burnoutFill;
	}

	public final int getCooldownTime() {
		return cooldownTime;
	}

	public final int getChargeupTime() {
		return chargeupTime;
	}

	public final ItemStack getItemStack() {
		return itemStack;
	}

	public final double getManaDrain() {
		return manaDrain;
	}

	public final float getPowerMultiplier() {
		return powerMultiplier;
	}

	public final float getManaMultiplier() {
		return manaMultiplier;
	}

	public final float getBurnoutMultiplier() {
		return burnoutMultiplier;
	}

	@Nonnull
	public final Color getSecondaryColor() {
		return secondaryColor;
	}

	public List<AttributeModifier> getAttributes() {
		return attributes;
	}

	/**
	 * If a child has this as true, it's parents will not run their render methods.
	 */
	public boolean overrideParentRenders() {
		return false;
	}

	/**
	 * If a child has this as true, it's parents will not run their run methods.
	 */
	public boolean overrideParentRuns() {
		return false;
	}

	public final void addAttribute(AttributeModifier attribute) {
		this.attributes.add(attribute);
	}

	public boolean ignoreResult() {
		return false;
	}

	/**
	 * Only return false if the spellData cannot be taxed from mana. Return true otherwise.
	 */
	public abstract boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing);

	/**
	 * This method runs client side when the spellData runs. Spawn particles here.
	 */
	@SideOnly(Side.CLIENT)
	public abstract void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing);

	/**
	 * Use this to run the module properly without rendering.
	 *
	 * @param data      The spellData associated with it.
	 * @param spellRing The SpellRing made with this.
	 * @return If the spellData has succeeded.
	 */
	public final boolean castSpell(@Nonnull SpellData data, @Nonnull SpellRing spellRing) {
		if (data.world.isRemote) return true;

		if (this instanceof ILingeringModule) {
			boolean alreadyLingering = false;
			for (SpellTicker.LingeringObject lingeringObject : SpellTicker.getStorageMap()) {
				if (lingeringObject.getSpellRing() == spellRing
						|| lingeringObject.getSpellData() == data) {
					alreadyLingering = true;
					break;
				}
			}
			if (!alreadyLingering)
				SpellTicker.addLingerSpell(spellRing, data, ((ILingeringModule) this).getLingeringTime(data, spellRing));
		}

		SpellCastEvent event = new SpellCastEvent(spellRing, data);
		MinecraftForge.EVENT_BUS.post(event);

		return !event.isCanceled() && run(data, spellRing);
	}

	public final void sendRenderPacket(@Nonnull SpellData data, @Nonnull SpellRing spellRing) {
		Vec3d target = data.getTargetWithFallback();

		if (target != null)
			PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(data, spellRing),
					new NetworkRegistry.TargetPoint(data.world.provider.getDimension(), target.x, target.y, target.z, 60));
	}

	@Nonnull
	public final NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("id", getID());
		return compound;
	}
}
