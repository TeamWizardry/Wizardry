package com.teamwizardry.wizardry.api.spell;

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

	private Color primaryColor = null;
	private Color secondaryColor = null;

	public Module() {
	}

	/**
	 * The stack required during crafting to run this module.
	 *
	 * @return An itemstack representing the price.
	 */
	@Nonnull
	public abstract ItemStack getRequiredStack();

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

	/**
	 * The amount of mana to drain when the spell runs.
	 */
	public double getManaDrain() {
		return 0;
	}

	/**
	 * The amount of burnout to fill when the spell runs.
	 */
	public double getBurnoutFill() {
		return 0;
	}

	/**
	 * The multiplier of the mana to drain on the final mana cost.
	 */
	public double getManaMultiplier() {
		return 1;
	}

	/**
	 * The multiplier of the burnout to fill on the final burnout cost.
	 */
	public double getBurnoutMultiplier() {
		return 1;
	}

	/**
	 * The amount of time in ticks the item needs to be right clicked for in order to execute the spell.
	 */
	public int getChargeUpTime() {
		return 0;
	}

	/**
	 * The amount of time in ticks the item needs to cooldown for in order to run again.
	 */
	public int getCooldownTime() {
		return 10;
	}

	public abstract boolean run(@Nonnull SpellData spell);

	/**
	 * This method runs client side when the spell runs. Spawn particles here.
	 */
	public abstract void runClient(@Nonnull SpellData spell);

	/**
	 * The primary color of this module. This color is used for particles and such.
	 */
	@Nullable
	public Color getPrimaryColor() {
		return primaryColor;
	}

	public void setPrimaryColor(Color primaryColor) {
		this.primaryColor = primaryColor;
	}

	/**
	 * The secondary color of the module. Used for prettier particles.
	 */
	@Nullable
	public Color getSecondaryColor() {
		return secondaryColor;
	}

	public void setSecondaryColor(Color secondaryColor) {
		this.secondaryColor = secondaryColor;
	}

	/**
	 * Use this to effectively run the entire module, rendering and all.
	 *
	 * @param data The spellData associated with it.
	 * @return If the spell has succeeded.
	 */
	public final boolean castSpell(@NotNull SpellData data) {
		processModule();

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

	protected final boolean runNextModule(@NotNull SpellData data) {
		return nextModule != null && nextModule.castSpell(data);
	}

	private void processModule() {
		if (nextModule == null) {
			finalManaDrain = getManaDrain();
			finalBurnoutFill = getBurnoutFill();
			return;
		}
		nextModule.processModule();

		if (getPrimaryColor() == null) setPrimaryColor(nextModule.getPrimaryColor());
		if (getSecondaryColor() == null) setPrimaryColor(nextModule.getSecondaryColor());

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
		return toCloneTo;
	}

	public abstract Module copy();

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("id", getID());
		compound.setTag("attributes", attributes);
		if (nextModule != null) {
			compound.setTag("next_module", nextModule.serializeNBT());
		}
		compound.setDouble("final_mana_drain", finalManaDrain);
		compound.setDouble("final_burnout_fill", finalBurnoutFill);
		if (primaryColor != null) compound.setInteger("primary_color", primaryColor.getRGB());
		if (secondaryColor != null) compound.setInteger("secondary_color", secondaryColor.getRGB());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		attributes = nbt.getCompoundTag("attributes");
		if (nbt.hasKey("next_module")) {
			Module tempModule = ModuleRegistry.INSTANCE.getModule(nbt.getCompoundTag("next_module").getString("id"));
			if (tempModule != null) {
				nextModule = tempModule.copy();
				if (nextModule != null) nextModule.deserializeNBT(nbt.getCompoundTag("next_module"));
			}
		}
		if (nbt.hasKey("primary_color")) setPrimaryColor(new Color(nbt.getInteger("primary_color")));
		if (nbt.hasKey("secondary_color")) setSecondaryColor(new Color(nbt.getInteger("secondary_color")));

		processModule();
	}
}
