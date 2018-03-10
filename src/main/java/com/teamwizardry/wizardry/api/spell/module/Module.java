package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import kotlin.Pair;
import net.minecraft.entity.Entity;
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

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.MAX_TIME;

/**
 * Created by Demoniaque.
 */
public abstract class Module {

	private final Color primaryColor;
	private final Color secondaryColor;
	private final int cooldownTime;
	private final int chargeupTime;
	private final double manaDrain;
	private final double burnoutFill;
	private final ItemStack itemStack;
	private final float powerMultiplier;
	private final float manaMultiplier;
	private final float burnoutMultiplier;

	public Module(ItemStack stack,
	              double manaDrain,
	              double burnoutFill,
	              Color primaryColor,
	              Color secondaryColor,
	              float powerMultiplier,
	              float manaMultiplier,
	              float burnoutMultiplier,
	              int cooldownTime,
	              int chargeupTime) {
		itemStack = stack;
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

	@Nullable
	public final Color getPrimaryColor() {
		return primaryColor;
	}

	@Nullable
	public final Color getSecondaryColor() {
		return secondaryColor;
	}

	@Nullable
	public static Module deserialize(NBTTagCompound compound) {
		if (compound.hasKey("id")) return ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
		return null;
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

	/**
	 * Only return false if the spell cannot be taxed from mana. Return true otherwise.
	 */
	public abstract boolean run(@Nonnull SpellData spell);

	/**
	 * This method runs client side when the spell runs. Spawn particles here.
	 */
	@SideOnly(Side.CLIENT)
	public abstract void render(@Nonnull SpellData spell, SpellRing spellRing);

	/**
	 * Use this to run the module properly without rendering.
	 *
	 * @param data The spellData associated with it.
	 * @param spellRing
	 * @return If the spell has succeeded.
	 */
	public final boolean castSpell(@Nonnull SpellData data, SpellRing spellRing) {
		if (this instanceof ILingeringModule)
			if (!SpellTicker.ticker.containsKey(this)) {
				data.addData(MAX_TIME, ((ILingeringModule) this).getLingeringTime(data));
				SpellTicker.ticker.put(this, new Pair<>(data, ((ILingeringModule) this).getLingeringTime(data)));
			}

		SpellCastEvent event = new SpellCastEvent(this, data);
		MinecraftForge.EVENT_BUS.post(event);

		return !event.isCanceled() && run(data);
	}

	public final void sendRenderPacket(@Nonnull SpellData data, SpellRing spellRing) {
		Entity caster = data.getData(CASTER);
		Vec3d target = data.hasData(SpellData.DefaultKeys.ORIGIN) ?
				data.getData(SpellData.DefaultKeys.ORIGIN) : data.hasData(SpellData.DefaultKeys.TARGET_HIT) ?
				data.getData(SpellData.DefaultKeys.TARGET_HIT) : caster != null ?
				caster.getPositionVector() : null;

		if (target != null)
			PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(this, data, spellRing),
					new NetworkRegistry.TargetPoint(data.world.provider.getDimension(), target.x, target.y, target.z, 60));
	}

	@Nonnull
	public NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("id", getID());
		return compound;
	}
}
