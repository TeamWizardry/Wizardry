package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.InterpScale;
import com.teamwizardry.wizardry.api.util.PosUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectSubstitution extends Module {

	public ModuleEffectSubstitution() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_substitution";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Substitution";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will swap the caster's position with that of the target. Also applies to blocks from the caster's inventory on the target block";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);
		BlockPos targetBlock = spell.getData(BLOCK_HIT);
		EnumFacing facing = spell.getData(FACE_HIT);

		if (caster == null) return false;

		if (targetEntity != null && targetEntity instanceof EntityLivingBase) {
			if (!processCost(spell)) return false;

			Vec3d posTarget = targetEntity.getPositionVector(), posCaster = caster.getPositionVector();
			float yawTarget = targetEntity.rotationYaw,
					pitchTarget = targetEntity.rotationPitch,
					yawCaster = caster.rotationYaw,
					pitchCaster = caster.rotationPitch;

			targetEntity.rotationYaw = yawCaster;
			targetEntity.rotationPitch = pitchCaster;
			targetEntity.setPositionAndUpdate(posCaster.xCoord, posCaster.yCoord, posCaster.zCoord);

			caster.rotationYaw = yawTarget;
			caster.rotationPitch = pitchTarget;
			caster.setPositionAndUpdate(posTarget.xCoord, posTarget.yCoord, posTarget.zCoord);

			return true;

		} else if (targetBlock != null && caster instanceof EntityPlayer) {
			if (caster.getEntityData().hasKey("substitution_block")) {
				IBlockState state = NBTUtil.readBlockState(caster.getEntityData().getCompoundTag("substitution_block"));
				IBlockState touchedBlock = spell.world.getBlockState(targetBlock);

				if (touchedBlock.getBlock() == state.getBlock()) return false;

				int strength = 10;
				if (attributes.hasKey(Attributes.EXTEND))
					strength += Math.min(32, attributes.getDouble(Attributes.EXTEND));

				if (!processCost(strength / 10.0, spell)) return false;

				strength *= calcBurnoutPercent(caster);

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

				HashSet<BlockPos> blocks = new HashSet<>();
				HashSet<BlockPos> branch = new HashSet<>();
				branch.add(targetBlock);
				blocks.add(targetBlock);
				getBlocks(spell.world, touchedBlock.getBlock(), strength, branch, blocks);

				if (blocks.isEmpty()) return true;

				for (int q = 0; q < blocks.size(); q++) {

					BlockPos nearest = null;
					for (BlockPos pos : blocks) {
						if (spell.world.getBlockState(pos).getBlock() == state.getBlock()) continue;

						if (nearest == null) {
							nearest = pos;
							continue;
						}
						if (pos.distanceSq(targetBlock) < nearest.distanceSq(targetBlock)) nearest = pos;
					}
					if (nearest == null) return true;

					stackBlock.shrink(1);

					IBlockState oldState = spell.world.getBlockState(nearest);
					spell.world.setBlockState(nearest, state);
					((EntityPlayer) caster).inventory.addItemStackToInventory(new ItemStack(oldState.getBlock().getItemDropped(oldState, spell.world.rand, 0)));

				}
			}
			return true;
		}

		return false;
	}

	private void getBlocks(World world, Block block, int maxBlocks, HashSet<BlockPos> branch, HashSet<BlockPos> allBlocks) {
		if (allBlocks.size() >= maxBlocks) return;

		HashSet<BlockPos> newBranch = new HashSet<>();

		for (BlockPos branchPos : branch) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos posAdj = branchPos.offset(facing);
				IBlockState state = world.getBlockState(posAdj);

				if (!world.isBlockLoaded(posAdj)) continue;
				if (allBlocks.contains(posAdj)) continue;
				if (state.getBlock() != block) continue;

				boolean sideSolid = false;
				for (EnumFacing dir : PosUtils.symmetricFacingValues) {
					BlockPos adjPos = branchPos.offset(dir);
					IBlockState adjState = world.getBlockState(adjPos);
					if (!adjState.isSideSolid(world, adjPos, dir.getOpposite())) {
						sideSolid = true;
						break;
					}
				}
				if (!sideSolid) continue;

				if (allBlocks.size() >= maxBlocks) return;

				newBranch.add(posAdj);
				allBlocks.add(posAdj);
			}
		}
		boolean mismatched = false;
		for (BlockPos pos : branch) if (!newBranch.contains(pos)) mismatched = true;
		if (mismatched)
			getBlocks(world, block, maxBlocks, newBranch, allBlocks);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Entity caster = spell.getData(CASTER);
		BlockPos targetBlock = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);

		if (targetEntity != null && caster != null) {

			ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
			ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ)), 50, ThreadLocalRandom.current().nextInt(20, 30), (aFloat, particleBuilder) -> {
				glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) ThreadLocalRandom.current().nextDouble(0.6, 1)));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 2, 0),
						0.5f, 0f, 1, ThreadLocalRandom.current().nextFloat()
				));
			});

			glitter.setColorFunction(new InterpColorHSV(getSecondaryColor(), getPrimaryColor()));
			ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(new Vec3d(caster.posX, caster.posY, caster.posZ)), 50, ThreadLocalRandom.current().nextInt(20, 30), (aFloat, particleBuilder) -> {
				glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) ThreadLocalRandom.current().nextDouble(0.6, 1)));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 4, 0),
						1f, 0f, 1, ThreadLocalRandom.current().nextFloat()
				));
			});
		} else if (targetBlock != null) {
			ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
			ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(new Vec3d(targetBlock).addVector(0.5, 0.5, 0.5)), 20, ThreadLocalRandom.current().nextInt(20, 30), (aFloat, particleBuilder) -> {
				glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) ThreadLocalRandom.current().nextDouble(0.6, 1)));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setMotion(new Vec3d(
						ThreadLocalRandom.current().nextDouble(-0.001, 0.001),
						ThreadLocalRandom.current().nextDouble(-0.001, 0.001),
						ThreadLocalRandom.current().nextDouble(-0.001, 0.001)
				));
			});
		}
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectSubstitution());
	}
}
