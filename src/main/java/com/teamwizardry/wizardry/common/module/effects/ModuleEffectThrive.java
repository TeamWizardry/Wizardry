package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.network.PacketThriveBlock;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_thrive")
public class ModuleEffectThrive implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		BlockPos targetPos = spell.getTargetPos();
		Entity targetEntity = spell.getVictim(world);
		Entity caster = spell.getCaster(world);
		Vec3d pos = spell.getTarget(world);

		if (pos == null) return true;

		if (targetEntity instanceof EntityLivingBase) {
			double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell) / 2;

			if (!spellRing.taxCaster(world, spell, true)) return false;

			((EntityLivingBase) targetEntity).heal((float) potency);
			world.playSound(null, new BlockPos(pos), ModSounds.HEAL, SoundCategory.NEUTRAL, 1, 1);
		}

		if (targetPos != null) {
			BlockPos otherTargetPos = spell.getTargetPos().add(0, 1, 0); // beam missed crops and targets farmland

			if (world.getBlockState(targetPos).getBlock() instanceof IGrowable || world.getBlockState(otherTargetPos).getBlock() instanceof IGrowable) {
				if (!spellRing.taxCaster(world, spell, true)) return false;
				if (!(caster instanceof EntityPlayerMP) || BlockUtils.hasEditPermission(targetPos, (EntityPlayerMP) caster)) {
					ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, targetPos);
					ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, otherTargetPos);

					PacketThriveBlock ptb = new PacketThriveBlock(targetPos);
					PacketHandler.NETWORK.sendToAllAround(ptb, new NetworkRegistry.TargetPoint(world.provider.getDimension(), targetPos.getX(), targetPos.getY(), targetPos.getZ(), 100));

					if (!world.isRemote) {
						world.playEvent(2005, targetPos, 0);
						world.playEvent(2005, otherTargetPos, 0);
					}
				}
			} else if (world.getBlockState(targetPos).getBlock() instanceof IPlantable || world.getBlockState(otherTargetPos).getBlock() instanceof IPlantable) {
				IBlockState state = world.getBlockState(targetPos);
				Block block = state.getBlock();
				if (!spellRing.taxCaster(world, spell, true)) return false;
				if (caster == null || (caster instanceof EntityPlayer && BlockUtils.hasEditPermission(targetPos, (EntityPlayerMP) caster))) {
					while (world.getBlockState(targetPos.up()).getBlock() == block) {
						targetPos = targetPos.up();
						state = world.getBlockState(targetPos);
						block = state.getBlock();
					}
					world.immediateBlockTick(targetPos, state, RandUtil.random);
					world.playSound(null, new BlockPos(pos), ModSounds.HEAL, SoundCategory.NEUTRAL, 1, 1);
				}

				state = world.getBlockState(otherTargetPos);
				block = state.getBlock();

				if (caster == null || (caster instanceof EntityPlayer && BlockUtils.hasEditPermission(otherTargetPos, (EntityPlayerMP) caster))) {
					while (world.getBlockState(otherTargetPos.up()).getBlock() == block) {
						otherTargetPos = otherTargetPos.up();
						state = world.getBlockState(otherTargetPos);
						block = state.getBlock();
					}
					world.immediateBlockTick(otherTargetPos, state, RandUtil.random);
					world.playSound(null, new BlockPos(pos), ModSounds.HEAL, SoundCategory.NEUTRAL, 1, 1);
				}
			}
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, new BlockPos(position));
		ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, new BlockPos(position.add(0,1,0)));

		if (position == null) return;
		LibParticles.EFFECT_REGENERATE(world, position, instance.getPrimaryColor());
	}
}
