package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.BounceHandler;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants.MISC;
import com.teamwizardry.wizardry.api.block.FluidTracker;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.TeleportUtil;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.crafting.burnable.EntityBurnableItem;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EventHandler {

	private final HashSet<UUID> fallResetter = new HashSet<>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent event) {
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SMOKE));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.DIAMOND));
	}


	private static final BlockPos sub = new BlockPos(0, -1, 0);

	/**
	 * Code "borrowed" from Tinker's construct slime boots
	 * https://github.com/SlimeKnights/TinkersConstruct/blob/23034cb63e98bba06faf1cdc4074009daf93be1f/src/main/java/slimeknights/tconstruct/gadgets/item/ItemSlimeBoots.java
	 * <p>
	 * I don't feel like re-inventing the wheel. Shut up.
	 */
	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.getEntityWorld().isRemote) return;

		BounceHandler.bouncingBlocks.removeIf(bouncyBlock -> {
			if (bouncyBlock.getWorld() == entity.getEntityWorld().provider.getDimension()) {
				return entity.getEntityWorld().getTotalWorldTime() - bouncyBlock.getTime() >= bouncyBlock.getExpiry();
			}
			return false;
		});

		if (!entity.isPotionActive(ModPotions.BOUNCING)) {
			boolean success = false;

			int x1 = entity.getPosition().getX();
			int y1 = entity.getPosition().getY();
			int z1 = entity.getPosition().getZ();

			for (BounceHandler.BouncyBlock bouncyBlock : BounceHandler.bouncingBlocks) {
				int x2 = bouncyBlock.getPos().getX();
				int y2 = bouncyBlock.getPos().getY();
				int z2 = bouncyBlock.getPos().getZ();
				Minecraft.getMinecraft().player.sendChatMessage(bouncyBlock.getPos() + " - " + entity.getPosition().add(sub));
				if (bouncyBlock.getWorld() == entity.getEntityWorld().provider.getDimension() &&
						((x1 >= x2 - 1 || x1 <= x2 + 1)
								&& (y1 >= y2 - 1 || y1 <= y2 + 1)
								&& (z1 >= z2 - 1 || z1 <= z2 + 1))) {
					success = true;
					break;
				}
			}
			if (!success) return;
		}

		//	if (event.getDistance() > 2) {

		event.setCanceled(true);
		event.setDamageMultiplier(0);
		entity.fallDistance = 0;
		entity.isAirBorne = true;
		entity.onGround = false;
		entity.motionY = -entity.motionY;
		entity.velocityChanged = true;

		if (entity instanceof EntityPlayerMP)
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		//	PacketHandler.NETWORK.sendToServer(new PacketBounce());
		//	PacketHandler.NETWORK.sendToServer(new PacketAddBouncyEntity(entity, entity.motionY));
		//	BounceHandler.addBounceHandler(entity, entity.motionY);

		entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
		//	}
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {

	}

	@SubscribeEvent
	public void redstoneHandler(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote) {
			return;
		}

		if (event.getEntity() instanceof EntityItem && !(event.getEntity() instanceof EntityBurnableItem)) {
			EntityItem item = (EntityItem) event.getEntity();
			if (EntityBurnableItem.isBurnable(item.getItem())) {
				EntityBurnableItem newItem = new EntityBurnableItem(event.getWorld(), item.posX, item.posY, item.posZ, item.getItem());
				newItem.motionX = item.motionX;
				newItem.motionY = item.motionY;
				newItem.motionZ = item.motionZ;
				newItem.setDefaultPickupDelay();
				item.setDead();
				event.getWorld().spawnEntity(newItem);
			}
		}
	}

	@SubscribeEvent
	public void tickEvent(WorldTickEvent event) {
		if (event.phase == Phase.END) {
			FluidTracker.INSTANCE.tick(event.world);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void underworldTeleport(LivingHurtEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		if (fallResetter.contains(event.getEntity().getUniqueID())) {
			if (event.getEntity().posY < 0 || event.getEntity().posY > event.getEntity().world.getHeight()) {
				event.setCanceled(true);
				return;
			}
			if (event.getSource() == DamageSource.FALL) {
				fallResetter.remove(event.getEntity().getUniqueID());
				event.setCanceled(true);
				return;
			}
		}
		if (event.getEntity().getEntityWorld().provider.getDimension() == Wizardry.underWorld.getId()) {
			if (event.getEntity().posY < 0) {
				EntityPlayer player = ((EntityPlayer) event.getEntityLiving());
				player.isDead = false;
				event.setAmount(0);
				BlockPos spawn = player.isSpawnForced(0) ? player.getBedLocation(0) : player.world.getSpawnPoint().add(player.world.rand.nextGaussian() * 16, 0, player.world.rand.nextGaussian() * 16);
				BlockPos teleportTo = spawn.add(0, 300 - spawn.getY(), 0);
				fallResetter.add(event.getEntity().getUniqueID());
				TeleportUtil.teleportToDimension((EntityPlayer) event.getEntity(), 0, teleportTo.getX(), teleportTo.getY(), teleportTo.getZ());
				event.setCanceled(true);
			}
		} else if (event.getEntity().getEntityWorld().provider.getDimension() == 0) {
			if (event.getSource() == EntityDamageSource.FALL && event.getEntity().fallDistance >= ConfigValues.underworldFallDistance) {
				BlockPos location = event.getEntity().getPosition();
				BlockPos bedrock = PosUtils.checkNeighborBlocksThoroughly(event.getEntity().getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					fallResetter.add(event.getEntity().getUniqueID());
					TeleportUtil.teleportToDimension((EntityPlayer) event.getEntity(), Wizardry.underWorld.getId(), 0, 300, 0);
					((EntityPlayer) event.getEntity()).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onFlyFall(PlayerFlyableFallEvent event) {
		if (event.getEntityPlayer().getEntityWorld().provider.getDimension() == 0) {
			if (event.getEntityPlayer().fallDistance >= ConfigValues.underworldFallDistance) {
				BlockPos location = event.getEntityPlayer().getPosition();
				BlockPos bedrock = PosUtils.checkNeighborBlocksThoroughly(event.getEntity().getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					if (event.getEntity().getEntityWorld().getBlockState(bedrock).getBlock() == Blocks.BEDROCK) {
						TeleportUtil.teleportToDimension(event.getEntityPlayer(), Wizardry.underWorld.getId(), 0, 300, 0);
						((EntityPlayer) event.getEntity()).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));
						fallResetter.add(event.getEntity().getUniqueID());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void fairyAmbush(SpellCastEvent event) {
		Entity caster = event.getSpellData().getData(SpellData.DefaultKeys.CASTER);
		int chance = 5;
		for (SpellRing spellRing : SpellUtils.getAllSpellRings(event.getSpellRing()))
			if (spellRing.getModule() != null && spellRing.getModule().getModuleClass() instanceof IContinuousModule) {
				chance = 1000;
				break;
			}
		if (RandUtil.nextInt(chance) == 0 && caster != null) {
			List<EntityFairy> fairyList = event.getSpellData().world.getEntitiesWithinAABB(EntityFairy.class, new AxisAlignedBB(caster.getPosition()).grow(64, 64, 64));
			if (fairyList.isEmpty()) return;
			EntityFairy fairy = fairyList.get(RandUtil.nextInt(fairyList.size() - 1));
			if (fairy == null) return;
			fairy.ambush = true;
		}
	}
}
