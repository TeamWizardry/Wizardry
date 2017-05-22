package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.WizardManager;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public abstract class Module implements INBTSerializable<NBTTagCompound> {

	/**
	 * Extra information that can be edited and read by the module.
	 * Used by modifiers.
	 */
	public NBTTagCompound attributes = new NBTTagCompound();

	/**
	 * The module that is to be ran from the run methods of the current module.
	 */
	@Nullable
	public Module nextModule = null;

	/**
	 * The final calculated cost of mana this spell consumes.
	 */
	public double finalManaDrain = 10;

	/**
	 * The final calculated cost of burnout this spell fills.
	 */
	public double finalBurnoutFill = 10;

	private double manaDrain = 0;
	private double burnoutFill = 0;
	private Color primaryColor = null;
	private Color secondaryColor = null;
	private int cooldownTime = 0;
	private int chargeupTime = 0;
	private double manaMultiplier = 1;
	private double burnoutMultiplier = 1;
	private ItemStack itemStack = ItemStack.EMPTY;

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
		return manaDrain;
	}

	public final void setManaDrain(double manaDrain) {
		this.manaDrain = manaDrain;
	}

	public final double getBurnoutFill() {
		return burnoutFill;
	}

	public final void setBurnoutFill(double burnoutFill) {
		this.burnoutFill = burnoutFill;
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

	public final double getManaMultiplier() {
		return manaMultiplier;
	}

	public final void setManaMultiplier(double manaMultiplier) {
		this.manaMultiplier = manaMultiplier;
	}

	public final double getBurnoutMultiplier() {
		return burnoutMultiplier;
	}

	public final void setBurnoutMultiplier(double burnoutMultiplier) {
		this.burnoutMultiplier = burnoutMultiplier;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	/**
	 * Use this to effectively run the entire module, rendering and all.
	 *
	 * @param data The spellData associated with it.
	 * @return If the spell has succeeded.
	 */
	public final boolean castSpell(@NotNull SpellData data) {
		if (LibrarianLib.DEV_ENVIRONMENT) processModule();

		if (this instanceof IlingeringModule)
			if (!SpellTicker.INSTANCE.ticker.containsKey(this))
				SpellTicker.INSTANCE.ticker.put(this, new Pair<>(data, ((IlingeringModule) this).lingeringTime(data)));

		boolean success = run(data);
		castParticles(data);

		if (!success && nextModule instanceof IParticleSpammable)
			nextModule.castParticles(data);

		return success;
	}

	public final void castParticles(@NotNull SpellData data) {
		Entity caster = data.getData(SpellData.DefaultKeys.CASTER);
		Vec3d target = data.hasData(SpellData.DefaultKeys.ORIGIN) ?
				data.getData(SpellData.DefaultKeys.ORIGIN) : data.hasData(SpellData.DefaultKeys.TARGET_HIT) ?
				data.getData(SpellData.DefaultKeys.TARGET_HIT) : caster != null ?
				caster.getPositionVector() : null;
		if (target != null)
			PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(this, data),
					new NetworkRegistry.TargetPoint(data.world.provider.getDimension(), target.xCoord, target.yCoord, target.zCoord, 60));
	}

	protected final double calcBurnoutPercent(@Nullable Entity player) {
		if (!(player instanceof EntityLivingBase)) return 1;
		if (player instanceof EntityPlayer && ((EntityPlayer) player).isCreative()) return 1;
		return ((WizardManager.getMaxBurnout((EntityLivingBase) player) - WizardManager.getBurnout((EntityLivingBase) player)) / (WizardManager.getMaxBurnout((EntityLivingBase) player) * 1.0));
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

	protected final void drainMana(double amount) {
		finalManaDrain += amount * getManaMultiplier();
	}

	protected final void fillBurnout(double amount) {
		finalBurnoutFill += amount * getBurnoutMultiplier();
	}

	protected final boolean runNextModule(@NotNull SpellData data) {
		return nextModule != null && nextModule.castSpell(data);
	}

	protected final void forceCastNextModuleParticles(@NotNull SpellData data) {
		if (nextModule != null) nextModule.castParticles(data);
	}

	public void processModule() {
		if (nextModule == null) {
			finalManaDrain = getManaDrain();
			finalBurnoutFill = getBurnoutFill();
			return;
		}
		nextModule.processModule();

		if (getPrimaryColor() == null) setPrimaryColor(nextModule.getPrimaryColor());
		if (getSecondaryColor() == null) setSecondaryColor(nextModule.getSecondaryColor());

		finalManaDrain = (getManaDrain() + nextModule.finalManaDrain) * getManaMultiplier();
		finalBurnoutFill = (getBurnoutFill() + nextModule.finalBurnoutFill) * getBurnoutMultiplier();
	}

	protected final <T extends Module> Module cloneModule(T toCloneTo) {
		toCloneTo.attributes = attributes;
		toCloneTo.nextModule = nextModule;
		toCloneTo.finalManaDrain = finalManaDrain;
		toCloneTo.finalBurnoutFill = finalBurnoutFill;
		toCloneTo.setPrimaryColor(getPrimaryColor());
		toCloneTo.setSecondaryColor(getSecondaryColor());
		toCloneTo.setBurnoutFill(getBurnoutFill());
		toCloneTo.setManaDrain(getManaDrain());
		toCloneTo.setBurnoutMultiplier(getBurnoutMultiplier());
		toCloneTo.setManaMultiplier(getManaMultiplier());
		toCloneTo.setCooldownTime(getCooldownTime());
		toCloneTo.setChargeupTime(getChargeupTime());
		toCloneTo.setItemStack(getItemStack());
		return toCloneTo;
	}

	public abstract Module copy();

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		if (attributes != null) compound.setTag("attributes", attributes);
		if (nextModule != null) compound.setTag("next_module", nextModule.serializeNBT());

		compound.setString("id", getID());
		compound.setDouble("final_mana_drain", finalManaDrain);
		compound.setDouble("final_burnout_fill", finalBurnoutFill);
		compound.setDouble("mana_drain", getManaDrain());
		compound.setDouble("burnout_fill", getBurnoutFill());
		compound.setDouble("mana_multiplier", getManaMultiplier());
		compound.setDouble("burnout_multiplier", getBurnoutMultiplier());
		compound.setDouble("chargeup_time", getChargeupTime());
		compound.setDouble("cooldown_time", getCooldownTime());
		compound.setTag("item_stack", getItemStack().serializeNBT());

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

		if (nbt.hasKey("final_mana_drain")) finalManaDrain = nbt.getDouble("final_mana_drain");
		if (nbt.hasKey("final_burnout_fill")) finalBurnoutFill = nbt.getDouble("final_burnout_fill");
		if (nbt.hasKey("primary_color")) setPrimaryColor(new Color(nbt.getInteger("primary_color")));
		if (nbt.hasKey("secondary_color")) setSecondaryColor(new Color(nbt.getInteger("secondary_color")));
		if (nbt.hasKey("mana_drain")) setManaDrain(nbt.getDouble("mana_drain"));
		if (nbt.hasKey("burnout_fill")) setBurnoutFill(nbt.getDouble("burnout_fill"));
		if (nbt.hasKey("mana_multiplier")) setManaMultiplier(nbt.getDouble("mana_multiplier"));
		if (nbt.hasKey("burnout_multiplier")) setBurnoutMultiplier(nbt.getDouble("burnout_multiplier"));
		if (nbt.hasKey("chargeup_time")) setChargeupTime(nbt.getInteger("chargeup_time"));
		if (nbt.hasKey("cooldown_time")) setCooldownTime(nbt.getInteger("cooldown_time"));
		if (nbt.hasKey("item_stack")) setItemStack(new ItemStack(nbt.getCompoundTag("item_stack")));
	}
}
