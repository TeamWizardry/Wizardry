package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.WizardManager;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class Module implements INBTSerializable<NBTTagCompound> {

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
	public double finalManaCost = 10;

	/**
	 * The final calculated cost of burnout this spell fills.
	 */
	public double finalBurnoutCost = 10;

	/**
	 * The summative final calculated/merged/mixed color from this module's children attributes.
	 */
	private Color color = null;

	public Module() {
	}

	@Nullable
	public static Color processColor(Module module) {
		Color color;
		if (module.nextModule != null) {
			Color childColor = processColor(module.nextModule);
			if (childColor == null) {
				color = module.getColor();
			} else {
				if (module.getColor() != null) color = Utils.mixColors(childColor, module.getColor());
				else color = childColor;
			}
		} else color = module.getColor();
		module.setColor(color);
		return color;
	}

	public static double processMana(Module module) {
		double mana = module.getManaToConsume();
		for (String key : module.attributes.getKeySet()) mana += module.attributes.getDouble(key);

		return module.finalManaCost = mana + (module.nextModule != null ? processMana(module.nextModule) : 0);
	}

	public static double processBurnout(Module module) {
		double burnout = module.getBurnoutToFill();
		for (String key : module.attributes.getKeySet()) burnout += module.attributes.getDouble(key);
		return module.finalBurnoutCost = burnout + (module.nextModule != null ? processBurnout(module.nextModule) : 0);
	}

	public static void process(Module module) {
		processBurnout(module);
		processMana(module);
		processColor(module);
	}

	/**
	 * The stack required during crafting to run this module.
	 *
	 * @return An itemstack representing the price.
	 */
	@NotNull
	public ItemStack getRequiredStack() {
		return new ItemStack(Blocks.STONE);
	}

	/**
	 * The type of module this module is.
	 *
	 * @return A ModuleType representing the type of module this is.
	 */
	@NotNull
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	/**
	 * A lower case snake_case string getID that reflects the module to identify it during serialization/deserialization.
	 *
	 * @return A lower case snake_case string.
	 */
	@NotNull
	public String getID() {
		return "null_id";
	}

	/**
	 * Represents the readable name of this module. Viewed in the worktable.
	 *
	 * @return A readable string representing it's name or title.
	 */
	@NotNull
	public String getReadableName() {
		return "Null Module";
	}

	/**
	 * The description of what this module does.
	 *
	 * @return A string representing what this module does.
	 */
	@NotNull
	public String getDescription() {
		return "This module is null";
	}

	public double getManaToConsume() {
		return 10;
	}

	public double getBurnoutToFill() {
		return 10;
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
		return 0;
	}

	public boolean run(@NotNull SpellData spell) {
		return false;
	}

	/**
	 * This method runs client side when the spell runs. Spawn particles here.
	 */
	public void runClient(@Nullable ItemStack stack, @NotNull SpellData spell) {

	}

	/**
	 * The color of this module. This color is used for particles and such.
	 *
	 * @return The current color.
	 */
	@Nullable
	public Color getColor() {
		return color;
	}

	/**
	 * Set the color of this module. This color is used for particles and such.
	 *
	 * @param color The new color.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * The secondary color of the module. Used for prettier particles.
	 *
	 * @return The secondary color.
	 */
	@Nullable
	public Color getSecondaryColor() {
		return null;
	}

	public boolean runNextModule(SpellData spell) {
		if (this instanceof IlingeringModule)
			if (!SpellTicker.INSTANCE.ticker.containsKey(this))
				SpellTicker.INSTANCE.ticker.put(this, new Pair<>(spell, ((IlingeringModule) this).lingeringTime(spell)));
		return nextModule != null && nextModule.run(spell);
	}

	/**
	 * Will apply modifiers to the head module given a stream of itemstacks representing the modifiers.
	 *
	 * @param modifiers The list of itemstacks representing a stream of modifiers to apply.
	 */
	public void processModifiers(List<ItemStack> modifiers) {
		for (ItemStack stack : modifiers) {
			Module modifier = ModuleRegistry.INSTANCE.getModule(stack);
			if (modifier == null) continue;
			if (!(modifier instanceof IModifier)) continue;

			((IModifier) modifier).apply(this);
		}
	}

	public double calcBurnoutPercent(@Nullable Entity player) {
		if (!(player instanceof EntityLivingBase)) return 1;
		if (player instanceof EntityPlayer && ((EntityPlayer) player).isCreative()) return 1;
		return ((WizardManager.getMaxBurnout((EntityLivingBase) player) - WizardManager.getBurnout((EntityLivingBase) player)) / (WizardManager.getMaxBurnout((EntityLivingBase) player) * 1.0));
	}

	@NotNull
	public Module copy() {
		Module clone = new Module();
		clone.deserializeNBT(serializeNBT());
		process(clone);
		return clone;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("id", getID());
		compound.setTag("attributes", attributes);
		if (nextModule != null) compound.setTag("next_module", nextModule.serializeNBT());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		attributes = nbt.getCompoundTag("attributes");
		if (nbt.hasKey("next_module")) {
			nextModule = ModuleRegistry.INSTANCE.getModule(nbt.getCompoundTag("next_module").getString("id"));
			if (nextModule != null) nextModule.deserializeNBT(nbt.getCompoundTag("next_module"));
		}
	}
}
