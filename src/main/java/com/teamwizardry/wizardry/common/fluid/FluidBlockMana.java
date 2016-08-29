package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FluidBlockMana extends BlockFluidClassic {

	public static final FluidBlockMana instance = new FluidBlockMana();

	public FluidBlockMana() {
		super(FluidMana.instance, Material.WATER);
		GameRegistry.register(this, new ResourceLocation(Wizardry.MODID, "mana"));
		this.setQuantaPerBlock(6);
		this.setUnlocalizedName("mana");
	}

	@Override
	public Fluid getFluid() {
		return FluidMana.instance;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID,  "particles/sparkle_blurred"));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos.getX() + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.getY() + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.getZ() + ThreadLocalRandom.current().nextDouble(-0.5, 0.5))), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(0.05, 0.1), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
			glitter.disableMotion();
		});
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote) {

			ParticleBuilder glitter = new ParticleBuilder(30);
			glitter.setScale(0.3f);
			glitter.setRender(new ResourceLocation(Wizardry.MODID,  "particles/sparkle_blurred"));
			ParticleSpawner.spawn(glitter, worldIn, new StaticInterp<>(entityIn.getPositionVector().addVector(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5))), 1, 0, (aFloat, particleBuilder) -> {
				glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
				glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(0.01, 0.05), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
				glitter.disableMotion();
			});

			if (entityIn instanceof EntityItem && new BlockPos(entityIn.getPositionVector()).equals(pos) && state.getValue(BlockFluidClassic.LEVEL) == 0) {
				EntityItem ei = (EntityItem) entityIn;
				ItemStack stack = ei.getEntityItem();

				if (stack.getItem() instanceof Explodable) {

					ParticleBuilder fizz = new ParticleBuilder(10);
					fizz.setScale(0.3f);
					fizz.setRender(new ResourceLocation(Wizardry.MODID,  "particles/sparkle_blurred"));
					ParticleSpawner.spawn(fizz, worldIn, new StaticInterp<>(entityIn.getPositionVector().addVector(0, 0.5, 0)), 10, 0, (aFloat, particleBuilder) -> {
						fizz.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
						fizz.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
						fizz.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.005, 0.005), ThreadLocalRandom.current().nextDouble(0.04, 0.08), ThreadLocalRandom.current().nextDouble(-0.005, 0.005)));
						fizz.disableMotion();
					});

					if (stack.hasTagCompound()) {
						NBTTagCompound compound = stack.getTagCompound();
						if (compound.hasKey("reactionCooldown")) {
							if (compound.getInteger("reactionCooldown") >= 100) {
								compound.setInteger("reactionCooldown", 0);

								ei.setDead();
								((Explodable) stack.getItem()).explode(entityIn);
								worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
								worldIn.playSound(null, ei.posX, ei.posY, ei.posZ, ModSounds.GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);

							} else {
								compound.setInteger("reactionCooldown", compound.getInteger("reactionCooldown") + 1);
								if (compound.getInteger("reactionCooldown") % 5 == 0)
									worldIn.playSound(null, ei.posX, ei.posY, ei.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.3F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);
							}
						} else stack.getTagCompound().setInteger("reactionCooldown", 0);
					} else stack.setTagCompound(new NBTTagCompound());
				}

			} else if (entityIn instanceof EntityPlayer) {
				((EntityPlayer) entityIn).addStat(Achievements.MANAPOOL);
			}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
