package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FluidBlockMana extends BlockFluidClassic {

	public static final FluidBlockMana instance = new FluidBlockMana();
	public static final String REACTION_COOLDOWN = "reaction_cooldown";

	public FluidBlockMana() {
		super(FluidMana.instance, ModBlocks.MANA_MATERIAL);
		GameRegistry.register(this, new ResourceLocation(Wizardry.MODID, "mana"));
		setQuantaPerBlock(6);
		setUnlocalizedName("mana");
	}

	@Override
	public Fluid getFluid() {
		return FluidMana.instance;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);
		LibParticles.FIZZING_AMBIENT(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote) {

			LibParticles.FIZZING_AMBIENT(worldIn, entityIn.getPositionVector());

			if ((entityIn instanceof EntityItem) && new BlockPos(entityIn.getPositionVector()).equals(pos) && (state.getValue(BlockFluidBase.LEVEL) == 0)) {
				EntityItem ei = (EntityItem) entityIn;
				ItemStack stack = ei.getEntityItem();

				if (stack.getItem() instanceof Explodable) {

					LibParticles.FIZZING_ITEM(worldIn, ei.getPositionVector());

					if (stack.hasTagCompound()) {
						NBTTagCompound compound = stack.getTagCompound();
						if (compound != null) {
							if (compound.hasKey(REACTION_COOLDOWN)) {
								if (compound.getInteger(REACTION_COOLDOWN) >= 100) {
									compound.setInteger(REACTION_COOLDOWN, 0);

									ei.setDead();
									((Explodable) stack.getItem()).explode(entityIn);
									worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
									worldIn.playSound(null, ei.posX, ei.posY, ei.posZ, ModSounds.GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, (ThreadLocalRandom.current().nextFloat() * 0.4F) + 0.8F);

								} else {
									compound.setInteger(REACTION_COOLDOWN, compound.getInteger(REACTION_COOLDOWN) + 1);
									if ((compound.getInteger(REACTION_COOLDOWN) % 5) == 0)
										worldIn.playSound(null, ei.posX, ei.posY, ei.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.3F, (ThreadLocalRandom.current().nextFloat() * 0.4F) + 0.8F);
								}
							} else stack.getTagCompound().setInteger(REACTION_COOLDOWN, 0);
						}
					} else stack.setTagCompound(new NBTTagCompound());
				}

			} else if (entityIn instanceof EntityPlayer) {
				((EntityPlayer) entityIn).addStat(Achievements.MANAPOOL);
			}
		}
	}

	@NotNull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
