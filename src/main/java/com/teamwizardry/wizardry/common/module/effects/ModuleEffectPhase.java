package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.client.core.PhasedBlockRenderer;
import com.teamwizardry.wizardry.common.core.nemez.NemezEventHandler;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseDuration;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.minecraft.util.EnumFacing.*;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectPhase extends ModuleEffect implements IDelayedModule {

	@Nonnull
	@Override
	public String getID() {
		return "effect_phase";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseDuration(), new ModuleModifierIncreaseAOE(), new ModuleModifierIncreaseRange()};
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity caster = spell.getCaster();
		Entity targetEntity = spell.getVictim();
		BlockPos targetPos = spell.getTargetPosBlockFirst();
		EnumFacing faceHit = spell.getFaceHit();

		double duration = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 20;
		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);
		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);

		if (!spellRing.taxCaster(spell)) return false;

		if (targetEntity instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) targetEntity;
			entity.addPotionEffect(new PotionEffect(ModPotions.PHASE, (int) duration, 0, true, false));
			spell.world.playSound(null, targetEntity.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 1, 1);
		}

		if (targetPos != null && faceHit != null) {
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(targetPos);

			faceHit = faceHit.getOpposite();
			NemezTracker nemezDrive = new NemezTracker();

			//switch (faceHit) {
			//	case DOWN:
			//		mutable.move(EnumFacing.DOWN, 2);
			//		break;
			//	case UP:
			//		break;
			//	case NORTH:
			//		mutable.move(EnumFacing.NORTH, 2);
			//		break;
			//	case SOUTH:
			//		mutable.move(EnumFacing.SOUTH);
			//		break;
			//	case WEST:
			//		mutable.move(EnumFacing.WEST, 2);
			//		break;
			//	case EAST:
			//		//mutable.move(EnumFacing.EAST);
			//		break;
			//}

			IBlockState targetState = spell.world.getBlockState(mutable);
			if (targetState.getBlock() == Blocks.AIR) return true;

			Set<BlockPos> poses = new HashSet<>();

			int rangeTick = 0;
			while (rangeTick < (int) range) {

				AxisAlignedBB bb = new AxisAlignedBB(mutable, mutable);
				switch (faceHit) {
					case DOWN:
					case UP:
						bb = bb.grow(area + 1, 0, area + 1);
						break;
					case NORTH:
					case SOUTH:
						bb = bb.grow(area + 1, area + 1, 0);
						break;
					case WEST:
					case EAST:
						bb = bb.grow(0, area + 1, area + 1);
						break;
				}

				Set<BlockPos> edges = new HashSet<>();
				switch (faceHit) {
					case DOWN:
					case UP:
						for (int x = (int) bb.minX; x <= (int) bb.maxX; x++) {
							for (int z = (int) bb.minZ; z <= (int) bb.maxZ; z++) {
								if (x == (int) bb.maxX || x == (int) bb.minX) {
									edges.add(new BlockPos(x, mutable.getY(), z));
								} else if (z == (int) bb.minZ || z == (int) bb.maxZ) {
									edges.add(new BlockPos(x, mutable.getY(), z));
								}
							}
						}
						break;
					case NORTH:
					case SOUTH:
						for (int x = (int) bb.minX; x <= (int) bb.maxX; x++) {
							for (int y = (int) bb.minY; y <= (int) bb.maxY; y++) {
								if (y == (int) bb.maxY || y == (int) bb.minY) {
									edges.add(new BlockPos(x, y, mutable.getZ()));
								} else if (x == (int) bb.minX || x == (int) bb.maxX) {
									edges.add(new BlockPos(x, y, mutable.getZ()));
								}
							}
						}
						break;
					case WEST:
					case EAST:
						for (int z = (int) bb.minZ; z <= (int) bb.maxZ; z++) {
							for (int y = (int) bb.minY; y <= (int) bb.maxY; y++) {
								if (y == (int) bb.maxY || y == (int) bb.minY) {
									edges.add(new BlockPos(mutable.getX(), y, z));
								} else if (z == (int) bb.minZ || z == (int) bb.maxZ) {
									edges.add(new BlockPos(mutable.getX(), y, z));
								}
							}
						}
						break;
				}


				Set<BlockPos> airBlocks = new HashSet<>();
				HashMap<BlockPos, IBlockState> tmp = new HashMap<>();
				boolean fullAirPlane = true;
				int edgeAirCount = 0;
				int edgeBlockCount = 0;
				for (BlockPos pos : BlockPos.getAllInBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ)) {
					IBlockState originalState = spell.world.getBlockState(pos);
					Block block = originalState.getBlock();

					if (edges.contains(pos)) {
						if (block == Blocks.AIR) edgeAirCount++;
						else edgeBlockCount++;
						continue;
					}

					if (block != Blocks.AIR) fullAirPlane = false;
					if (block == ModBlocks.FAKE_AIR) continue;
					if (spell.world.getTileEntity(pos) != null) continue;

					tmp.put(pos, originalState);
				}

				if (!fullAirPlane) {
					if (edgeAirCount <= edgeBlockCount) {
						for (Map.Entry<BlockPos, IBlockState> entry : tmp.entrySet()) {

							nemezDrive.trackBlock(entry.getKey(), entry.getValue());

							IBlockState state = ModBlocks.FAKE_AIR.getDefaultState();
							BlockUtils.placeBlock(spell.world, entry.getKey(), state, (EntityPlayerMP) caster);

							nemezDrive.trackBlock(entry.getKey(), state);
						}
						poses.addAll(tmp.keySet());
					} else {
						for (Map.Entry<BlockPos, IBlockState> entry : tmp.entrySet()) {
							if (entry.getValue().getBlock() == Blocks.AIR) continue;

							nemezDrive.trackBlock(entry.getKey(), entry.getValue());

							IBlockState state = ModBlocks.FAKE_AIR.getDefaultState();
							BlockUtils.placeBlock(spell.world, entry.getKey(), state, (EntityPlayerMP) caster);

							nemezDrive.trackBlock(entry.getKey(), state);

							poses.add(entry.getKey());
						}
					}
				} else break;

				mutable.move(faceHit);
				rangeTick++;
			}

			nemezDrive.endUpdate();

			spell.addData(SpellData.DefaultKeys.NEMEZ, nemezDrive);
			spell.addData(SpellData.DefaultKeys.BLOCK_SET, poses);

			addDelayedSpell(this, spellRing, spell, (int) duration);
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Set<BlockPos> blockSet = spell.getData(SpellData.DefaultKeys.BLOCK_SET, new HashSet<>());

		double duration = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 20;

		PhasedBlockRenderer.addPhase(spell.world, blockSet, (int) duration);
	}

	@Override
	public void runDelayedEffect(SpellData spell, SpellRing spellRing) {
		NemezTracker nemezDrive = spell.getData(SpellData.DefaultKeys.NEMEZ);
		BlockPos targetPos = spell.getTargetPos();

		if (nemezDrive != null && targetPos != null) {
			NemezEventHandler.reverseTime(spell.world, nemezDrive, targetPos);
		}
	}

	public EnumFacing[] getPerpendicularFacings(EnumFacing facing) {
		switch (facing) {
			case DOWN:
			case UP:
				return EnumFacing.HORIZONTALS;
			case NORTH:
			case SOUTH:
				return new EnumFacing[]{UP, DOWN, WEST, EAST};
			case WEST:
			case EAST:
				return new EnumFacing[]{UP, DOWN, NORTH, SOUTH};
		}

		return new EnumFacing[]{};
	}
}
