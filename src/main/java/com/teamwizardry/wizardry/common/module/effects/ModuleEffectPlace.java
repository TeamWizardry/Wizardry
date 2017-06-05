package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectPlace extends Module implements IBlockSelectable, ITaxing {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_place";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Place";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will place the block selected";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Vec3d originPos = spell.getData(ORIGIN);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);
		EnumFacing facing = spell.getData(FACE_HIT);
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);

		if (facing == null) {
			RayTraceResult trace = Utils.raytrace(world, PosUtils.vecFromRotations(pitch, yaw), originPos, 64, caster);
			if (trace == null) return false;
			if (trace.typeOfHit != RayTraceResult.Type.BLOCK) return false;
			facing = trace.sideHit;
		}

		double strength = 1 * getMultiplier();
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(64.0, attributes.getDouble(Attributes.EXTEND));
		strength *= calcBurnoutPercent(caster);

		if (caster != null && facing != null && targetPos != null && caster.getEntityData().hasKey("selected")) {
			IBlockState state = NBTUtil.readBlockState(caster.getEntityData().getCompoundTag("selected"));

			ItemStack stackBlock = null;
			for (ItemStack stack : ((EntityPlayer) caster).inventory.mainInventory) {
				if (stack.isEmpty()) continue;
				if (!(stack.getItem() instanceof ItemBlock)) continue;
				Block block = ((ItemBlock) stack.getItem()).getBlock();
				if (block != state.getBlock()) continue;
				stackBlock = stack;
				break;
			}

			if (stackBlock == null) return false;

			targetPos = targetPos.offset(facing);
			if (!world.isAirBlock(targetPos)) return false;
			if (!tax(this, spell)) return false;
			stackBlock.shrink(1);
			IBlockState oldState = spell.world.getBlockState(targetPos);
			spell.world.setBlockState(targetPos, state);
			((EntityPlayer) caster).inventory.addItemStackToInventory(new ItemStack(oldState.getBlock().getItemDropped(oldState, spell.world.rand, 0)));

		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectPlace());
	}
}
