package com.teamwizardry.wizardry.common.block.fluid;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.ManaTracker;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.core.DamageSourceMana;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;

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
	public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
		super.updateTick(world, pos, state, rand);

		if (world.isRemote)
			LibParticles.FIZZING_AMBIENT(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
		// Fizz all entities in the pool
		run(entityIn,
				entity -> true,
				entity -> {
					if (world.isRemote) LibParticles.FIZZING_AMBIENT(world, entityIn.getPositionVector());
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

					if (!world.isRemote) {
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
				entity -> entity instanceof EntityItem && !((EntityItem) entity).getItem().isEmpty() && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "plank"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_PLANKS, item.getItem().getCount()));
					if (world.isRemote) {
						LibParticles.FIZZING_AMBIENT(world, item.getPositionVector());
					}
				});

		// Turn log to wisdom log
		run(entityIn,
				entity -> entity instanceof EntityItem && !((EntityItem) entity).getItem().isEmpty() && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "log"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_LOG, item.getItem().getCount()));
					if (world.isRemote) {
						LibParticles.FIZZING_AMBIENT(world, item.getPositionVector());
					}
				});

		// Turn stair to wisdom stair
		run(entityIn,
				entity -> entity instanceof EntityItem && !((EntityItem) entity).getItem().isEmpty() && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "stairs"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_STAIRS, item.getItem().getCount()));
					if (world.isRemote) {
						LibParticles.FIZZING_AMBIENT(world, item.getPositionVector());
					}
				});

		// Turn slab to wisdom slab
		run(entityIn,
				entity -> entity instanceof EntityItem && !((EntityItem) entity).getItem().isEmpty() && Utils.hasOreDictPrefix(((EntityItem) entity).getItem(), "slabs"),
				entity -> {
					EntityItem item = (EntityItem) entity;
					item.setItem(new ItemStack(ModBlocks.WISDOM_WOOD_SLAB, item.getItem().getCount()));
					if (world.isRemote) {
						LibParticles.FIZZING_AMBIENT(world, item.getPositionVector());
					}
				});

		// Turn book to codex
		run (entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() == Items.BOOK,
				entity -> {
					ManaTracker.INSTANCE.addManaCraft(world, pos, (World worldIn, BlockPos posIn) -> {
						return PosUtils.blockHasItems(worldIn, posIn, Items.BOOK);
					}, 200, (worldIn, posIn, cookTimeIn, durationIn) -> {
						EntityItem entityItem = PosUtils.getItemAtPos(worldIn, posIn, Items.BOOK);
						if (world.isRemote)
							LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
						if ((cookTimeIn % 5) == 0)
							world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
					}, (worldIn, posIn) -> {
						EntityItem entityItem = PosUtils.getItemAtPos(worldIn, posIn, Items.BOOK);
						PacketHandler.NETWORK.sendToAllAround(new PacketExplode(entityItem.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true),
								new NetworkRegistry.TargetPoint(world.provider.getDimension(), entityItem.posX, entityItem.posY, entityItem.posZ, 256));

						boom(world, entityItem);

						entityItem.setItem(new ItemStack(ModItems.BOOK, entityItem.getItem().getCount()));
						world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
					});
				});

		// Convert mana to nacre
		run(entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() == Items.GOLD_NUGGET,
				entity -> {
					ManaTracker.INSTANCE.addManaCraft(world, pos, (worldIn, posIn) -> {
						return PosUtils.blockHasItems(worldIn, posIn, Items.GOLD_NUGGET);
					}, 200, (worldIn, posIn, cookTimeIn, durationIn) -> {
						if (cookTimeIn % 5 == 0)
						{
							EntityItem item = PosUtils.getItemAtPos(worldIn, posIn, Items.GOLD_NUGGET);
							world.playSound(null, item.posX, item.posY, item.posZ, ModSounds.BUBBLING, SoundCategory.AMBIENT, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
						}
					}, (worldIn, posIn) -> {
						EntityItem item = PosUtils.getItemAtPos(worldIn, posIn, Items.GOLD_NUGGET);
						if (worldIn.isRemote)
							LibParticles.FIZZING_AMBIENT(worldIn, item.getPositionVector());
						world.setBlockState(posIn, ModBlocks.FLUID_NACRE.getDefaultState());
						item.getItem().shrink(1);
						if (item.getItem().isEmpty())
							world.removeEntity(item);
					});
				});
		
		// Explode explodable items
		run(entityIn,
				entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof IExplodable,
				entity -> {
					ManaTracker.INSTANCE.addManaCraft(world, pos, (worldIn, posIn) -> {
						List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
						for (EntityItem entityItem : entities)
						{
							if (entityItem.getItem() == null)
								continue;
							if (entityItem.getItem().getItem() instanceof IExplodable)
								return true;
						}
						return false;
					}, 200, (worldIn, posIn, cookTimeIn, durationIn) -> {
						List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
						for (EntityItem entityItem : entities)
						{
							if (entityItem.getItem() == null)
								continue;
							if (entityItem.getItem().getItem() instanceof IExplodable)
							{
								if (cookTimeIn % 5 == 0)
									worldIn.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.AMBIENT, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
								return;
							}
						}
					}, (worldIn, posIn) -> {
						List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
						for (EntityItem entityItem : entities)
						{
							if (entityItem.getItem() == null)
								continue;
							if (entityItem.getItem().getItem() instanceof IExplodable)
							{
								((IExplodable) entityItem.getItem().getItem()).explode(entityItem);
								worldIn.setBlockToAir(posIn);
								worldIn.removeEntity(entityItem);
								worldIn.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
								return;
							}
						}
					});
				});

		// Mana Battery Recipe
		run(entityIn,
				entity -> entity instanceof EntityItem && !((EntityItem) entity).getItem().isEmpty() && ((EntityItem) entity).getItem().getItem() == Items.DIAMOND,
				entity -> {
					ManaTracker.INSTANCE.addManaCraft(world, pos, (worldIn, posIn) -> {
						if (PosUtils.blockHasItems(worldIn, posIn, Items.DIAMOND, Item.getItemFromBlock(Blocks.SOUL_SAND), ModItems.DEVIL_DUST))
						{
							for (int i = -1; i <= 1; i++)
								for (int j = -1; j <= 1; j++)
									if (world.getBlockState(posIn.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState())
										return false;
							return true;
						}
						return false;
					}, 200, (worldIn, posIn, cookTimeIn, durationIn) -> {
						EntityItem diamond = PosUtils.getItemAtPos(worldIn, posIn, Items.DIAMOND);
						if (worldIn.isRemote)
							LibParticles.CRAFTING_ALTAR_IDLE(worldIn, diamond.getPositionVector());
						if (cookTimeIn % 5 == 0)
							worldIn.playSound(null, diamond.posX, diamond.posY, diamond.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
					}, (worldIn, posIn) -> {
						EntityItem diamond = PosUtils.getItemAtPos(worldIn, posIn, Items.DIAMOND);
						EntityItem soulSand = PosUtils.getItemAtPos(worldIn, posIn, Item.getItemFromBlock(Blocks.SOUL_SAND));
						EntityItem devilDust = PosUtils.getItemAtPos(worldIn, posIn, ModItems.DEVIL_DUST);
						if (diamond == null || soulSand == null || devilDust == null)
							return;
						diamond.getItem().shrink(1);
						if (diamond.getItem().isEmpty())
							world.removeEntity(diamond);
						soulSand.getItem().shrink(1);
						if (soulSand.getItem().isEmpty())
							world.removeEntity(soulSand);
						devilDust.getItem().shrink(1);
						if (devilDust.getItem().isEmpty())
							world.removeEntity(devilDust);
						
						EntityItem manaBattery = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModBlocks.MANA_BATTERY));
						manaBattery.motionX = 0;
						manaBattery.motionY = 0;
						manaBattery.motionZ = 0;
						manaBattery.forceSpawn = true;
						world.spawnEntity(manaBattery);
						
						for (int i = -1; i <= 1; i++)
							for (int j = -1; j <= 1; j++)
								worldIn.setBlockToAir(posIn.add(i, 0, j));
						
						PacketHandler.NETWORK.sendToAllAround(new PacketExplode(manaBattery.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true),
								new NetworkRegistry.TargetPoint(world.provider.getDimension(), manaBattery.posX, manaBattery.posY, manaBattery.posZ, 256));

						boom(world, manaBattery);

						world.playSound(null, manaBattery.posX, manaBattery.posY, manaBattery.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
					});
		});
	}

	private void boom(World worldIn, Entity entity) {
		List<Entity> entityList = worldIn.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(entity.getPosition()).grow(32, 32, 32));
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
