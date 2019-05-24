package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_substitution")
public class ModuleEffectSubstitution implements IModuleEffect, IBlockSelectable {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);
		Entity caster = spell.getCaster(world);
		BlockPos targetBlock = spell.getTargetPos();
		EnumFacing facing = spell.getFaceHit();

		if (caster == null) return false;

		if (targetEntity instanceof EntityLivingBase) {
			if (!spellRing.taxCaster(world, spell, true)) return false;

			Vec3d posTarget = new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ),
					posCaster = new Vec3d(caster.posX, caster.posY, caster.posZ);
			float yawTarget = targetEntity.rotationYaw,
					pitchTarget = targetEntity.rotationPitch,
					yawCaster = caster.rotationYaw,
					pitchCaster = caster.rotationPitch;

			targetEntity.rotationYaw = yawCaster;
			targetEntity.rotationPitch = pitchCaster;
			targetEntity.setPositionAndUpdate(posCaster.x, posCaster.y, posCaster.z);

			caster.rotationYaw = yawTarget;
			caster.rotationPitch = pitchTarget;
			caster.setPositionAndUpdate(posTarget.x, posTarget.y, posTarget.z);
			world.playSound(null, caster.getPosition(), ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat());
			world.playSound(null, targetEntity.getPosition(), ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat());

			return true;

		} else if (targetBlock != null && caster instanceof EntityPlayer) {
			if (facing == null) return false;
			ItemStack hand = ((EntityPlayer) caster).getHeldItemMainhand();
			if (hand.isEmpty()) return false;

			world.playSound(null, targetBlock, ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat());
			if (NBTHelper.hasNBTEntry(hand, "selected")) {

				NBTTagCompound compound = NBTHelper.getCompound(hand, "selected");
				if (compound == null) return false;

				IBlockState state = NBTUtil.readBlockState(compound);
				IBlockState touchedBlock = world.getBlockState(targetBlock);

				if (touchedBlock.getBlock() == state.getBlock()) return false;

				double area = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);

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

				Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetBlock, facing, Math.min(stackBlock.getCount(), (int) area), (int) ((Math.sqrt(area)+1)/2), pos -> {
					BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
					IBlockState adjacentState = world.getBlockState(mutable.offset(facing));
					if (adjacentState.getBlock() != Blocks.AIR) return true;

					IBlockState block = world.getBlockState(pos);
					return block.getBlock() != touchedBlock.getBlock();
				});

				if (blocks.isEmpty()) return true;

				for (BlockPos pos : blocks) {
					if (stackBlock.isEmpty()) return true;
					if (!spellRing.taxCaster(world, spell, 1 / area, false)) return false;
					if (world.isAirBlock(pos)) continue;
					if (world.getBlockState(pos).getBlock() == state.getBlock()) continue;

					stackBlock.shrink(1);

					IBlockState oldState = world.getBlockState(pos);
					BlockUtils.placeBlock(world, pos, state, (EntityPlayerMP) caster);
					((EntityPlayer) caster).inventory.addItemStackToInventory(new ItemStack(oldState.getBlock().getItemDropped(oldState, world.rand, 0)));
				}
			}
			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity caster = spell.getCaster(world);
		BlockPos targetBlock = spell.getTargetPos();
		Entity targetEntity = spell.getVictim(world);

		if (targetEntity != null && caster != null) {

			ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(20, 30));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ)), 50, RandUtil.nextInt(20, 30), (aFloat, particleBuilder) -> {
				glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFloatInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
				glitter.setLifetime(RandUtil.nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 2, 0),
						0.5f, 0f, 1, RandUtil.nextFloat()
				));
			});

			glitter.setColorFunction(new InterpColorHSV(instance.getSecondaryColor(), instance.getPrimaryColor()));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(caster.posX, caster.posY, caster.posZ)), 50, RandUtil.nextInt(20, 30), (aFloat, particleBuilder) -> {
				glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFloatInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
				glitter.setLifetime(RandUtil.nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 4, 0),
						1f, 0f, 1, RandUtil.nextFloat()
				));
			});
		} else if (targetBlock != null) {
			ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(20, 30));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(targetBlock).add(0.5, 0.5, 0.5)), 20, 0, (aFloat, particleBuilder) -> {
				glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
				glitter.setAlphaFunction(new InterpFloatInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
				glitter.setLifetime(RandUtil.nextInt(10, 20));
				glitter.setScaleFunction(new InterpScale(1, 0));
				glitter.setMotion(new Vec3d(
						RandUtil.nextDouble(-0.1, 0.1),
						RandUtil.nextDouble(-0.1, 0.1),
						RandUtil.nextDouble(-0.1, 0.1)
				));
			});
		}
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull World world, ModuleInstanceEffect instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		if (ring.getParentRing() != null
				&& ring.getParentRing().getModule() != null
				&& ring.getParentRing().getModule() == ModuleRegistry.INSTANCE.getModule("event_collide_entity"))
			return previousData;

		Entity caster = data.getCaster(world);
		BlockPos targetBlock = data.getTargetPos();
		EnumFacing facing = data.getFaceHit();


		if (!(caster instanceof EntityLivingBase)) return previousData;
		ItemStack hand = ((EntityLivingBase) caster).getHeldItemMainhand();

		if (hand.isEmpty()) return previousData;

		if (targetBlock != null && caster instanceof EntityPlayer) {
			if (facing == null) return previousData;
			if (NBTHelper.hasNBTEntry(hand, "selected")) {
				NBTTagCompound compound = NBTHelper.getCompound(hand, "selected");
				if (compound == null) return previousData;

				IBlockState state = NBTUtil.readBlockState(compound);
				IBlockState targetState = instance.getCachableBlockstate(world, targetBlock, previousData);
				if (targetState.getBlock() == state.getBlock()) return previousData;

				double area = ring.getAttributeValue(world, AttributeRegistry.AREA, data);

				ItemStack stackBlock = null;
				for (ItemStack stack : ((EntityPlayer) caster).inventory.mainInventory) {
					if (stack.isEmpty()) continue;
					if (!(stack.getItem() instanceof ItemBlock)) continue;
					Block block = ((ItemBlock) stack.getItem()).getBlock();
					if (block != state.getBlock()) continue;
					stackBlock = stack;
					break;
				}

				if (stackBlock == null) return previousData;
				stackBlock = stackBlock.copy();

				Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetBlock, facing, Math.min(stackBlock.getCount(), (int) area), (int) ((Math.sqrt(area)+1)/2), pos -> {
					BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
					IBlockState adjacentState = world.getBlockState(mutable.offset(facing));
					if (adjacentState.getBlock() != Blocks.AIR) return true;

					IBlockState block = world.getBlockState(pos);
					return block.getBlock() != targetState.getBlock();
				});
				
				if (blocks.isEmpty()) return previousData;

				HashMap<BlockPos, IBlockState> blockStateCache = new HashMap<>();
				for (BlockPos pos : blocks) {
					blockStateCache.put(pos, world.getBlockState(pos));
				}

				HashMap<BlockPos, IBlockState> tmpCache = new HashMap<>(blockStateCache);


				for (Map.Entry<BlockPos, IBlockState> entry : tmpCache.entrySet()) {

					if (BlockUtils.isAnyAir(entry.getValue())) continue;

					BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(entry.getKey());
					for (EnumFacing face : EnumFacing.VALUES) {

						mutable.move(face);

						IBlockState adjState;
						if (!blockStateCache.containsKey(mutable)) {
							adjState = world.getBlockState(mutable);
							blockStateCache.put(mutable.toImmutable(), adjState);
						} else adjState = blockStateCache.get(mutable);

						if (adjState.getBlock() != targetState.getBlock() || !blocks.contains(mutable)) {

							instance.drawFaceOutline(mutable, face.getOpposite());
						}
						mutable.move(face.getOpposite());
					}
				}
			}
		}

		return previousData;
	}
}
