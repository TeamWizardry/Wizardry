package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectPlace extends Module implements IBlockSelectable {

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
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);
		EnumFacing facing = spell.getData(FACE_HIT);

		double strength = 1;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(64.0, attributes.getDouble(Attributes.EXTEND));
		if (!processCost(strength, spell)) return false;
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

			stackBlock.shrink(1);

			targetPos = targetPos.offset(facing);
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
