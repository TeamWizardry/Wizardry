package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FluidBlockMana extends BlockFluidClassic {

	public static final FluidBlockMana instance = new FluidBlockMana();
	public static final String REACTION_COOLDOWN = "reaction_cooldown";

	public FluidBlockMana() {
		super(FluidMana.instance, Material.WATER);
		GameRegistry.register(this, new ResourceLocation(Wizardry.MODID, "mana"));
		setQuantaPerBlock(6);
		setUnlocalizedName("mana");
	}

	@Override
	public Fluid getFluid() {
		return FluidMana.instance;
	}

	@Override
	public void updateTick(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
		super.updateTick(world, pos, state, rand);

		if (world.isRemote)
			LibParticles.FIZZING_AMBIENT(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		run(entityIn,
				entity -> true,
				entity -> {
					if (worldIn.isRemote) LibParticles.FIZZING_AMBIENT(worldIn, entityIn.getPositionVector());
				});

		run(entityIn,
				entity -> entity instanceof EntityLivingBase,
				entity -> ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 1, true, false)));

		run(entityIn,
				entity -> entity instanceof EntityPlayer,
				entity -> {
					((EntityPlayer) entityIn).addStat(Achievements.MANAPOOL);

					if (ThreadLocalRandom.current().nextInt(50) == 0) {
						if (((EntityPlayer) entity).getFoodStats().getFoodLevel() > 0)
							((EntityPlayer) entity).getFoodStats().addStats(-1, 0);
						if (((EntityPlayer) entity).getFoodStats().getSaturationLevel() > 0)
							((EntityPlayer) entity).getFoodStats().addStats(0, -1);
					}
					if (ThreadLocalRandom.current().nextInt(50) == 0) {
						((EntityPlayer) entity).setHealth((float) Math.max(0, ((EntityPlayer) entity).getHealth() - 0.1));
					}
				});

		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getEntityItem(), "plank"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setEntityItemStack(new ItemStack(ModBlocks.WISDOM_WOOD_PLANKS, item.getEntityItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getEntityItem(), "log"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setEntityItemStack(new ItemStack(ModBlocks.WISDOM_WOOD_LOG, item.getEntityItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getEntityItem(), "stairs"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setEntityItemStack(new ItemStack(ModBlocks.WISDOM_WOOD_STAIRS, item.getEntityItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getEntityItem(), "slabs"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setEntityItemStack(new ItemStack(ModBlocks.WISDOM_WOOD_SLAB, item.getEntityItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		run(entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getEntityItem().getItem() == Items.BOOK,
				entity -> {
					ItemStack stack = ((EntityItem) entity).getEntityItem();
					int expiry = ItemNBTHelper.getInt(stack, REACTION_COOLDOWN, 200);
					if (expiry > 0) {
						if (worldIn.isRemote)
							LibParticles.CRAFTING_ALTAR_IDLE(worldIn, entity.getPositionVector());

						ItemNBTHelper.setInt(stack, REACTION_COOLDOWN, --expiry);
						if ((expiry % 5) == 0)
							worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (ThreadLocalRandom.current().nextFloat() * 0.4F) + 0.8F);
					} else {
						PacketHandler.NETWORK.sendToAllAround(new PacketExplode(entity.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50),
								new NetworkRegistry.TargetPoint(worldIn.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 256));

						List<Entity> entityList = worldIn.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(entity.getPosition()).expand(32, 32, 32));
						for (Entity entity1 : entityList) {
							double dist = entity1.getDistanceToEntity(entity);
							final double upperMag = 20;
							final double scale = 3.5;
							double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);
							Vec3d dir = entity1.getPositionVector().subtract(entity.getPositionVector()).normalize().scale(mag);

							entity1.motionX += (dir.xCoord);
							entity1.motionY += (dir.yCoord);
							entity1.motionZ += (dir.zCoord);
							entity1.fallDistance = 0;
							entity1.velocityChanged = true;

							if (entity1 instanceof EntityPlayerMP)
								((EntityPlayerMP) entity1).connection.sendPacket(new SPacketEntityVelocity(entity1));
						}

						((EntityItem) entity).setEntityItemStack(new ItemStack(ModItems.BOOK, stack.getCount()));
						worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
					}
				});

		run(entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getEntityItem().getItem() instanceof IExplodable,
				entity -> {
					ItemStack stack = ((EntityItem) entity).getEntityItem();
					int expiry = ItemNBTHelper.getInt(stack, REACTION_COOLDOWN, 200);
					if (expiry > 0) {
						ItemNBTHelper.setInt(stack, REACTION_COOLDOWN, --expiry);
						if ((expiry % 5) == 0)
							worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.BUBBLING, SoundCategory.AMBIENT, 0.7F, (ThreadLocalRandom.current().nextFloat() * 0.4F) + 0.8F);
					} else {
						if (worldIn.isRemote) LibParticles.FIZZING_ITEM(worldIn, entity.getPositionVector());

						((IExplodable) stack.getItem()).explode(entityIn);
						worldIn.setBlockState(entity.getPosition(), Blocks.AIR.getDefaultState());
						worldIn.removeEntity(entity);
						worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (ThreadLocalRandom.current().nextFloat() * 0.4F) + 0.8F);
					}
				});
	}

	public void run(Entity entity, Predicate<Entity> test, Consumer<Entity> process) {
		if (test.test(entity)) process.accept(entity);
	}

	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
