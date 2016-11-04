package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.bloods.BloodRegistry;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IHasAffinity;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ModuleFlame extends Module implements IHasAffinity {

	public ModuleFlame(ItemStack stack) {
		super(stack);
		attributes.addAttribute(Attribute.DURATION);
	}

	@Override
	public Color getColor() {
		return Color.RED;
	}

	@Override
	public ModuleType getType() {
		return ModuleType.EFFECT;
	}

	@Override
	public String getDescription() {
		return "Inflict fire damage every tick. Will smelt any block or item it touches.";
	}

	@Override
	public String getDisplayName() {
		return "Inflame";
	}

	@Override
	public NBTTagCompound getModuleData() {
		NBTTagCompound compound = super.getModuleData();
		compound.setInteger(Constants.Module.DURATION, (int) attributes.apply(Attribute.DURATION, 1.0));
		compound.setDouble(Constants.Module.MANA, attributes.apply(Attribute.MANA, 10.0));
		compound.setDouble(Constants.Module.BURNOUT, attributes.apply(Attribute.BURNOUT, 10.0));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		if (caster.worldObj.isRemote) return false;
		if (caster instanceof EntityItem) {
			int duration = spell.getInteger(Constants.Module.DURATION);
			EntityItem item = (EntityItem) caster;
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(item.getEntityItem());
			if (result != null) {
				if (duration >= item.getEntityItem().stackSize) {
					EntityItem output = new EntityItem(item.worldObj, item.posX, item.posY + 0.5, item.posZ);
					result.stackSize *= item.getEntityItem().stackSize;
					output.setEntityItemStack(result);
					output.worldObj.spawnEntityInWorld(output);
					item.setDead();
				} else {
					EntityItem output = new EntityItem(item.worldObj, item.posX, item.posY + 0.5, item.posZ);
					result.stackSize *= duration;
					item.getEntityItem().stackSize -= duration;
					output.setEntityItemStack(result);
					output.worldObj.spawnEntityInWorld(output);
				}
			}
		} else if (caster instanceof EntityLivingBase) {
			int duration = spell.getInteger(Constants.Module.DURATION);
			caster.setFire(MathHelper.ceiling_double_int(duration / 20.0));
		} else if (caster instanceof SpellEntity) {
			BlockPos pos = caster.getPosition();
			IBlockState state = caster.worldObj.getBlockState(pos);
			Block block = state.getBlock();
			ItemStack item = new ItemStack(block, 1, block.getMetaFromState(state));
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(item);
			if (result != null) {
				Block smelted = Block.getBlockFromItem(result.getItem());
				if (!caster.worldObj.isRemote) {
					caster.worldObj.setBlockState(pos, smelted.getStateFromMeta(result.getMetadata()));
					caster.worldObj.playEvent(2001, pos, Block.getStateId(smelted.getDefaultState()));
				}
			}
		}

		LibParticles.EFFECT_FIRE(caster.worldObj, caster.getPositionVector().addVector(0.0, 1.0, 0.0), Vec3d.ZERO, 0.7f);

		return true;
	}

	@Override
	public Map<IBloodType, Integer> getAffinityLevels() {
		Map<IBloodType, Integer> levels = new HashMap<>();
		levels.put(BloodRegistry.PYROBLOOD, 3);
		return levels;
	}
}
