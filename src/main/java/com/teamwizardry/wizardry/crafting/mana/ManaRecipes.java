package com.teamwizardry.wizardry.crafting.mana;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ManaRecipes
{
	public static final ManaRecipes INSTANCE = new ManaRecipes();
	
	public static final String CODEX = "codex";
	public static final String NACRE = "nacre";
	public static final String EXPLODABLE = "explodable";
	public static final String MANA_BATTERY = "mana battery";
	
	public class CodexCrafter extends ManaCrafter
	{
		public CodexCrafter()
		{
			super(CODEX, 200);
		}

		@Override
		public boolean isValid(World world, BlockPos pos, List<EntityItem> items)
		{
			return items.stream().map(entityItem -> entityItem.getItem().getItem()).anyMatch(item -> item == Items.BOOK);
		}

		@Override
		public void tick(World world, BlockPos pos, List<EntityItem> items)
		{
			super.tick(world, pos, items);
			for (EntityItem entity : items)
				if (entity.getItem().getItem() == Items.BOOK)
				{
					if (world.isRemote)
						LibParticles.CRAFTING_ALTAR_IDLE(world, entity.getPositionVector());
					if (currentDuration % 5 == 0)
						world.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
					return;
				}
		}
		
		@Override
		public void finish(World world, BlockPos pos, List<EntityItem> items)
		{
			for (EntityItem entity : items)
				if (entity.getItem().getItem() == Items.BOOK)
				{
					PacketHandler.NETWORK.sendToAllAround(new PacketExplode(entity.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true),
							new NetworkRegistry.TargetPoint(world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 256));
					
					Utils.boom(world, entity);
					
					entity.setItem(new ItemStack(ModItems.BOOK, entity.getItem().getCount()));
					world.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
					return;
				}
		}
	};

	public class NacreCrafter extends ManaCrafter
	{
		public NacreCrafter()
		{
			super(NACRE, 200);
		}

		@Override
		public boolean isValid(World world, BlockPos pos, List<EntityItem> items)
		{
			return items.stream().map(entity -> entity.getItem().getItem()).anyMatch(item -> item == Items.GOLD_NUGGET);
		}

		@Override
		public void tick(World world, BlockPos pos, List<EntityItem> items)
		{
			super.tick(world, pos, items);
			for (EntityItem entity : items)
				if (entity.getItem().getItem() == Items.GOLD_NUGGET)
				{
					if (world.isRemote)
						LibParticles.CRAFTING_ALTAR_IDLE(world, entity.getPositionVector());
					if (currentDuration % 5 == 0)
						world.playSound(null, entity.posX, entity.posY, entity.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
					return;
				}
		}
		
		@Override
		public void finish(World world, BlockPos pos, List<EntityItem> items)
		{
			for (EntityItem entity : items)
				if (entity.getItem().getItem() == Items.GOLD_NUGGET)
				{
					if (world.isRemote)
						LibParticles.FIZZING_AMBIENT(world, entity.getPositionVector());
					world.setBlockState(pos, ModBlocks.FLUID_NACRE.getDefaultState());
					entity.getItem().shrink(1);
					if (entity.getItem().isEmpty())
						world.removeEntity(entity);
					return;
				}
		}
	}

	public class ExplodableCrafter extends ManaCrafter
	{
		public ExplodableCrafter()
		{
			super(EXPLODABLE, 200);
		}

		@Override
		public boolean isValid(World world, BlockPos pos, List<EntityItem> items)
		{
			return items.stream().map(entity -> entity.getItem().getItem()).anyMatch(item -> item instanceof IExplodable);
		}
		
		@Override
		public void tick(World world, BlockPos pos, List<EntityItem> items)
		{
			super.tick(world, pos, items);
			EntityItem item = items.stream().filter(entity -> entity.getItem().getItem() instanceof IExplodable).findFirst().orElse(null);
			if (item != null)
				if (currentDuration % 5 == 0)
					world.playSound(null, item.posX, item.posY, item.posZ, ModSounds.BUBBLING, SoundCategory.AMBIENT, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
		}

		@Override
		public void finish(World world, BlockPos pos, List<EntityItem> items)
		{
			EntityItem item = items.stream().filter(entity -> entity.getItem().getItem() instanceof IExplodable).findFirst().orElse(null);
			if (item != null)
			{
				((IExplodable) item.getItem().getItem()).explode(item);
				world.setBlockToAir(pos);
				world.removeEntity(item);
				world.playSound(null, item.posX, item.posY, item.posZ, ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			}
		}
	}
	
	public class ManaBatteryCrafter extends ManaCrafter
	{
		public ManaBatteryCrafter()
		{
			super(MANA_BATTERY, 200);
		}

		@Override
		public boolean isValid(World world, BlockPos pos, List<EntityItem> items)
		{
			List<Item> list = items.stream().map(entity -> entity.getItem().getItem()).filter(item -> item == Items.DIAMOND || item == Item.getItemFromBlock(Blocks.SOUL_SAND) || item == ModItems.DEVIL_DUST).collect(Collectors.toList());
			if (!list.contains(Items.DIAMOND))
				return false;
			if (!list.contains(Item.getItemFromBlock(Blocks.SOUL_SAND)))
				return false;
			if (!list.contains(ModItems.DEVIL_DUST))
				return false;
			
			for (int i = -1; i <= 1; i++)
				for (int j = -1; j <= 1; j++)
					if (world.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState())
						return false;
			return true;
		}
		
		@Override
		public void tick(World world, BlockPos pos, List<EntityItem> items)
		{
//			super.tick(world, pos, items);
			currentDuration++;
			EntityItem diamond = items.stream().filter(entity -> entity.getItem().getItem() == Items.DIAMOND).findFirst().orElse(null);
			if (world.isRemote)
				LibParticles.CRAFTING_ALTAR_IDLE(world, diamond.getPositionVector());
			if (currentDuration % 5 == 0)
				world.playSound(null, diamond.posX, diamond.posY, diamond.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
		}

		@Override
		public void finish(World world, BlockPos pos, List<EntityItem> items)
		{
			Item blockSoulSand = Item.getItemFromBlock(Blocks.SOUL_SAND);
			List<EntityItem> list = items.stream().filter(entity -> {
				Item item = entity.getItem().getItem();
				return item == Items.DIAMOND || item == blockSoulSand || item == ModItems.DEVIL_DUST;
			}).collect(Collectors.toList());
			EntityItem diamond = null;
			EntityItem soulSand = null;
			EntityItem devilDust = null;
			for (EntityItem entity : list)
			{
				if (diamond == null && entity.getItem().getItem() == Items.DIAMOND)
					diamond = entity;
				else if (soulSand == null && entity.getItem().getItem() == blockSoulSand)
					soulSand = entity;
				else if (devilDust == null && entity.getItem().getItem() == ModItems.DEVIL_DUST)
					devilDust = entity;
				else
					break;
			}
			
			diamond.getItem().shrink(1);
			if (diamond.getItem().isEmpty())
				world.removeEntity(diamond);
			soulSand.getItem().shrink(1);
			if (soulSand.getItem().isEmpty())
				world.removeEntity(soulSand);
			devilDust.getItem().shrink(1);
			if (devilDust.getItem().isEmpty())
				world.removeEntity(devilDust);
			
			EntityItem manaBattery = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModBlocks.MANA_BATTERY));
			manaBattery.motionX = 0;
			manaBattery.motionY = 0;
			manaBattery.motionZ = 0;
			manaBattery.forceSpawn = true;
			world.spawnEntity(manaBattery);
			
			for (int i = -1; i <= 1; i++)
				for (int j = -1; j <= 1; j++)
					world.setBlockToAir(pos.add(i, 0, j));
			
			PacketHandler.NETWORK.sendToAllAround(new PacketExplode(manaBattery.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), manaBattery.posX, manaBattery.posY, manaBattery.posZ, 256));

			Utils.boom(world, manaBattery);

			world.playSound(null, manaBattery.posX, manaBattery.posY, manaBattery.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
		}
	}
}
