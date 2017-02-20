package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.MISC;
import com.teamwizardry.wizardry.api.WizardManager;
import com.teamwizardry.wizardry.api.spell.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.TeleportUtil;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.entity.EntityDevilDust;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntitySpellCodex;
import com.teamwizardry.wizardry.common.tile.TileStaff;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EventHandler {

	private final ArrayList<UUID> fallResetUUIDs = new ArrayList<>();

	@SubscribeEvent
	public void onTextureStitchEvent(Pre event) {
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
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
				event.getWorld().spawnEntity(new EntityDevilDust(event.getWorld(), item));
			else if (item.getEntityItem().getItem() == Items.BOOK)
				event.getWorld().spawnEntity(new EntitySpellCodex(event.getWorld(), item));
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == ModBlocks.STAFF_BLOCK) {
			TileStaff pedestal = (TileStaff) event.getWorld().getTileEntity(event.getPos());
			//for (pedestal.getLinkedPedestals())
		}
	}

	@SubscribeEvent
	public void tickEvent(WorldTickEvent event) {
		if (event.phase != Phase.START) {
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
			BlockPos spawn = player.isSpawnForced(0) ? player.getBedLocation(0) : player.world.getSpawnPoint().add(player.world.rand.nextGaussian() * 16, 0, player.world.rand.nextGaussian() * 16);
			BlockPos teleportTo = spawn.add(0, 300 - spawn.getY(), 0);
			TeleportUtil.teleportToDimension((EntityPlayer) event.getEntity(), 0, teleportTo.getX(), teleportTo.getY(), teleportTo.getZ());
			event.getEntity().fallDistance = -500;
			event.setCanceled(true);
		}
		if (event.getEntity().getEntityWorld().provider.getDimension() == Wizardry.underWorld.getId()) {
			event.getEntity().fallDistance = 0;
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
						TeleportUtil.teleportToDimension((EntityPlayer) event.getEntity(), Wizardry.underWorld.getId(), 0, 300, 0);
						((EntityPlayer) event.getEntity()).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 1, false, false));
						fallResetUUIDs.add(event.getEntity().getUniqueID());
						((EntityPlayer) event.getEntity()).addStat(Achievements.CRUNCH);
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onFlyFall(PlayerFlyableFallEvent event) {
		if (event.getEntityPlayer().getEntityWorld().provider.getDimension() != Wizardry.underWorld.getId()) {
			if (event.getEntityPlayer().fallDistance >= 250) {
				BlockPos location = event.getEntityPlayer().getPosition();
				BlockPos bedrock = PosUtils.checkNeighbor(event.getEntity().getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					if (event.getEntity().getEntityWorld().getBlockState(bedrock).getBlock() == Blocks.BEDROCK) {
						TeleportUtil.teleportToDimension(event.getEntityPlayer(), Wizardry.underWorld.getId(), 0, 300, 0);
						((EntityPlayer) event.getEntity()).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 1, false, false));
						fallResetUUIDs.add(event.getEntityPlayer().getUniqueID());
						event.getEntityPlayer().addStat(Achievements.CRUNCH);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void capTick(TickEvent.PlayerTickEvent event) {
		WizardManager.addMana(WizardManager.getMaxMana(event.player) / 1000, event.player);
		WizardManager.removeBurnout(WizardManager.getMaxBurnout(event.player) / 1000, event.player);

		ItemStack cape = event.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (cape == null || cape.getItem() != ModItems.CAPE) {
			if (WizardManager.getMaxMana(event.player) != 100)
				WizardManager.setMaxMana(100, event.player);
			if (WizardManager.getMaxBurnout(event.player) != 100)
				WizardManager.setMaxBurnout(100, event.player);

		} else if (cape.getItem() == ModItems.CAPE) {
			WizardManager.setMaxMana((int) Math.log(ItemNBTHelper.getInt(cape, "time", 0)) * 1000, event.player);
			WizardManager.setMaxBurnout((int) Math.log(ItemNBTHelper.getInt(cape, "time", 0)) * 1000, event.player);
		}
		//Minecraft.getMinecraft().player.sendChatMessage(WizardManager.getMaxMana(event.player)+ "");
	}

	@SubscribeEvent
	public void fairyAmbush(SpellCastEvent event) {
		Entity caster = event.spell.getData(SpellData.DefaultKeys.CASTER);
		if (caster != null) {
			List<EntityFairy> fairyList = event.spell.world.getEntitiesWithinAABB(EntityFairy.class, new AxisAlignedBB(caster.getPosition()).expand(64, 64, 64));
			for (EntityFairy fairy : fairyList) {
				if (ThreadLocalRandom.current().nextInt(5) == 0) fairy.ambush = true;
			}
		}
	}
}
