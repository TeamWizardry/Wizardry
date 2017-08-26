package com.teamwizardry.wizardry.common.block.fluid;

import com.google.common.base.Predicates;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.core.DamageSourceMana;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockFluidMana extends BlockFluidClassic {

	public static final String REACTION_COOLDOWN = "reaction_cooldown";

	public BlockFluidMana() {
		super(FluidMana.instance, Material.WATER);
		setRegistryName("mana");
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
		// Fizz all entities in the pool
		run(entityIn,
				entity -> true,
				entity -> {
					if (worldIn.isRemote) LibParticles.FIZZING_AMBIENT(worldIn, entityIn.getPositionVector());
				});

		// Nullify gravity of player
		run(entityIn,
				entity -> entity instanceof EntityLivingBase,
				entity -> {
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));

					if (RandUtil.nextInt(50) == 0) entity.attackEntityFrom(DamageSourceMana.INSTANCE, 0.1f);
				});

		// Subtract player food
		run(entityIn,
				entity -> entity instanceof EntityPlayer,
				entity -> {

					if (!worldIn.isRemote) {
						MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
						Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(Wizardry.MODID, "advancements/advancement_crunch.json"));
						if (advancement == null) return;
						AdvancementProgress progress = ((EntityPlayerMP) entity).getAdvancements().getProgress(advancement);
						for (String s : progress.getRemaningCriteria()) {
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(advancement, s);
						}
					}
					if (!((EntityPlayer) entity).capabilities.isCreativeMode && RandUtil.nextInt(50) == 0)
						((EntityPlayer) entity).getFoodStats().addExhaustion(1f);
				});

		// Turn plank to wisdom plank
		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "plank"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_PLANKS, item.getItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		// Turn log to wisdom log
		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "log"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_LOG, item.getItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		// Turn stair to wisdom stair
		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "stairs"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_STAIRS, item.getItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		// Turn slab to wisdom slab
		run(entityIn,
				entity -> entity instanceof EntityItem && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "slabs"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_SLAB, item.getItem().getCount()));
					if (worldIn.isRemote) {
						LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
					}
				});

		// Turn book to codex
		run(entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() == Items.BOOK,
				entity -> {
					ItemStack stack = ((EntityItem) entity).getItem();
					int expiry = ItemNBTHelper.getInt(stack, REACTION_COOLDOWN, 200);
					if (expiry > 0) {
						if (worldIn.isRemote)
							LibParticles.CRAFTING_ALTAR_IDLE(worldIn, entity.getPositionVector());

						ItemNBTHelper.setInt(stack, REACTION_COOLDOWN, --expiry);
						if ((expiry % 5) == 0)
							worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
					} else {
						PacketHandler.NETWORK.sendToAllAround(new PacketExplode(entity.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true),
								new NetworkRegistry.TargetPoint(worldIn.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 256));

						boom(worldIn, entity);

						((EntityItem) entity).setItem(new ItemStack(ModItems.BOOK, stack.getCount()));
						worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
					}
				});

		// Explode explodable items
		run(entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof IExplodable,
				entity -> {
					ItemStack stack = ((EntityItem) entity).getItem();
					int expiry = ItemNBTHelper.getInt(stack, REACTION_COOLDOWN, 200);
					if (expiry > 0) {
						ItemNBTHelper.setInt(stack, REACTION_COOLDOWN, --expiry);
						if ((expiry % 5) == 0)
							worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.BUBBLING, SoundCategory.AMBIENT, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
					} else {
						if (worldIn.isRemote) LibParticles.FIZZING_ITEM(worldIn, entity.getPositionVector());

						((IExplodable) stack.getItem()).explode(entityIn);
						worldIn.setBlockToAir(entity.getPosition());
						worldIn.removeEntity(entity);
						worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
					}
				});

		// Mana Battery Recipe
		run(entityIn, entity -> {
			if (worldIn.isRemote) return false;
			if (entityIn instanceof EntityItem && !((EntityItem) entity).getItem().isEmpty() && ((EntityItem) entity).getItem().getItem() == Items.DIAMOND) { // World worldIn, BlockPos pos, IBlockState state, Entity entityIn
				List<Entity> entities = worldIn.getEntitiesInAABBexcluding(entityIn, new AxisAlignedBB(pos),
						Predicates.instanceOf(EntityItem.class));
				EntityItem devilDust = null, soulSand = null;
				for (Entity e : entities) {
					EntityItem entityItem = (EntityItem) e;
					if (entityItem.getItem().getItem() == ModItems.DEVIL_DUST)
						devilDust = entityItem;
					else if (entityItem.getItem().getItem() == Item.getItemFromBlock(Blocks.SOUL_SAND))
						soulSand = entityItem;
				}

				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++) {
						if (worldIn.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState())
							return false;
					}

				return devilDust != null && soulSand != null;
			}
			return false;
		}, entity -> {
			ItemStack stack = ((EntityItem) entity).getItem();
			int expiry = ItemNBTHelper.getInt(stack, REACTION_COOLDOWN, 200);
			if (expiry > 0) {
				if (worldIn.isRemote)
					LibParticles.CRAFTING_ALTAR_IDLE(worldIn, entity.getPositionVector());

				ItemNBTHelper.setInt(stack, REACTION_COOLDOWN, --expiry);
				if ((expiry % 5) == 0)
					worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			} else {
				if (worldIn.isRemote) return;

				List<Entity> entities = worldIn.getEntitiesInAABBexcluding(entityIn, new AxisAlignedBB(pos), Predicates.instanceOf(EntityItem.class));
				EntityItem devilDust = null, soulSand = null;
				for (Entity e : entities) {
					EntityItem entityItem = (EntityItem) e;
					if (entityItem.getItem().getItem() == ModItems.DEVIL_DUST)
						devilDust = entityItem;
					else if (entityItem.getItem().getItem() == Item.getItemFromBlock(Blocks.SOUL_SAND))
						soulSand = entityItem;
				}

				((EntityItem) entity).getItem().shrink(1);
				if (((EntityItem) entity).getItem().isEmpty())
					worldIn.removeEntity(entity);

				devilDust.getItem().shrink(1);
				if (devilDust.getItem().isEmpty())
					worldIn.removeEntity(devilDust);

				soulSand.getItem().shrink(1);
				if (soulSand.getItem().isEmpty())
					worldIn.removeEntity(soulSand);

				EntityItem manaBattery = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModBlocks.MANA_BATTERY));
				manaBattery.motionX = 0;
				manaBattery.motionY = 0;
				manaBattery.motionZ = 0;
				manaBattery.forceSpawn = true;
				worldIn.spawnEntity(manaBattery);

				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						worldIn.setBlockToAir(pos.add(i, 0, j));

				PacketHandler.NETWORK.sendToAllAround(new PacketExplode(entity.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true),
						new NetworkRegistry.TargetPoint(worldIn.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 256));

				boom(worldIn, manaBattery);

				worldIn.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
			}
		});
	}

	private void boom(World worldIn, Entity entity) {
		List<Entity> entityList = worldIn.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(entity.getPosition()).expand(32, 32, 32));
		for (Entity entity1 : entityList) {
			double dist = entity1.getDistanceToEntity(entity);
			final double upperMag = 3;
			final double scale = 0.8;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);
			Vec3d dir = entity1.getPositionVector().subtract(entity.getPositionVector()).normalize().scale(mag);

			entity1.motionX += (dir.x);
			entity1.motionY += (dir.y);
			entity1.motionZ += (dir.z);
			entity1.fallDistance = 0;
			entity1.velocityChanged = true;

			if (entity1 instanceof EntityPlayerMP)
				((EntityPlayerMP) entity1).connection.sendPacket(new SPacketEntityVelocity(entity1));
		}
	}

	public void run(Entity entity, Predicate<Entity> test, Consumer<Entity> process) {
		if (test.test(entity)) process.accept(entity);
	}
}
