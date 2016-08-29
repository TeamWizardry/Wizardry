package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.bloat.TeleportUtil;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.entity.EntityDevilDust;
import com.teamwizardry.wizardry.common.entity.EntitySpellCodex;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.UUID;

public class EventHandler {

	private ArrayList<UUID> fallResetUUIDs = new ArrayList<>();

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/hexagon"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/octagon"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/hexagon_blur_1"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/hexagon_blur_2"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/hexagon_blur_3"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/octagon_blur_1"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/octagon_blur_2"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/octagon_blur_3"));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/sprite_sheet"));
	}

	@SubscribeEvent
	public void redstoneBornEvent(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityItem) {
			EntityItem item = (EntityItem) event.getEntity();
			if (item.getEntityItem().getItem() == Items.REDSTONE)
				event.getWorld().spawnEntityInWorld(new EntityDevilDust(event.getWorld(), item));
			else if (item.getEntityItem().getItem() == Items.BOOK)
				event.getWorld().spawnEntityInWorld(new EntitySpellCodex(event.getWorld(), item));
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == ModBlocks.PEDESTAL) {
			TilePedestal pedestal = (TilePedestal) event.getWorld().getTileEntity(event.getPos());
			//for (pedestal.getLinkedPedestals())
		}
	}

	@SubscribeEvent
	public void tickEvent(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			if (!fallResetUUIDs.isEmpty())
				event.world.playerEntities.stream().filter(entity -> fallResetUUIDs.contains(entity.getUniqueID())).forEach(entity -> entity.fallDistance = -255);
			fallResetUUIDs.clear();
		}
	}

	@SubscribeEvent
	public void onFallDamage(LivingHurtEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		if (event.getSource() == EntityDamageSource.outOfWorld) {
			EntityPlayer player = ((EntityPlayer) event.getEntityLiving());
			BlockPos spawn = player.isSpawnForced(0) ? player.getBedLocation(0) : player.worldObj.getSpawnPoint().add(player.worldObj.rand.nextGaussian() * 16, 0, player.worldObj.rand.nextGaussian() * 16);
			BlockPos teleportTo = spawn.add(0, 255 - spawn.getY(), 0);
			TeleportUtil.INSTANCE.teleportToDimension((EntityPlayer) event.getEntity(), 0, teleportTo.getX(), teleportTo.getY(), teleportTo.getZ());
			event.getEntity().fallDistance = -500;
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		if (event.getEntity().getEntityWorld().provider.getDimension() != Wizardry.underWorld.getId()) {
			if (event.getEntity().fallDistance >= 250) {
				BlockPos location = event.getEntity().getPosition();
				BlockPos bedrock = PosUtils.checkNeighbor(event.getEntity().getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					if (event.getEntity().getEntityWorld().getBlockState(bedrock).getBlock() == Blocks.BEDROCK) {
						TeleportUtil.INSTANCE.teleportToDimension((EntityPlayer) event.getEntity(), Wizardry.underWorld.getId(), 0, 100, 0);
						fallResetUUIDs.add(event.getEntity().getUniqueID());
						((EntityPlayer) event.getEntity()).addStat(Achievements.CRUNCH);
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
