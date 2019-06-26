
package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.NBTConstants.MISC;
import com.teamwizardry.wizardry.api.block.FluidTracker;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.TeleportUtil;
import com.teamwizardry.wizardry.crafting.burnable.EntityBurnableItem;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class EventHandler {

	public static final HashSet<UUID> fallResetter = new HashSet<>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent event) {
		if (event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks()) {
			event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SMOKE));
			event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
			event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.DIAMOND));
		}
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
				newItem.setPickupDelay(40);
				item.setDead();
				event.getWorld().spawnEntity(newItem);
			}
		}
	}

	//Added hashmap to work in servers
	HashMap<Integer, Boolean> passmap = new HashMap<>();

	@SubscribeEvent
	public void tickEvent(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.side == Side.SERVER) {
			FluidTracker.INSTANCE.tick(event.world);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerTick(TickEvent.PlayerTickEvent event) {
		//Leaving the underowrld
		if (event.player.getEntityWorld().provider.getDimension() == Wizardry.underWorld.getId()) {
			if (event.player.posY < 0) {
				//Gets the players spwanpoint and sets the location to teleport to around that
				event.player.isDead = false;
				BlockPos spawn = event.player.isSpawnForced(0) ? event.player.getBedLocation(0) : event.player.world.getSpawnPoint().add(event.player.world.rand.nextGaussian() * 16, 0, event.player.world.rand.nextGaussian() * 16);
				BlockPos teleportTo = spawn.add(0, 300 - spawn.getY(), 0);
				//stops fall damage
				fallResetter.add(event.player.getUniqueID());
				TeleportUtil.teleportToDimension(event.player, 0, teleportTo.getX(), teleportTo.getY(), teleportTo.getZ());
			}
		}
		//adds pass to check if player on bedrock after reached velocity
		if (event.player.getEntityWorld().provider.getDimension() == 0 && ConfigValues.underworldFallSpeed <= 0) {
			if (event.player.motionY < ConfigValues.underworldFallSpeed || passmap.get(event.player.getEntityId()) != null) {
				passmap.put(event.player.getEntityId(), true);
				BlockPos location = event.player.getPosition();
				BlockPos bedrock = PosUtils.checkNeighborBlocksThoroughly(event.player.getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					event.player.isDead = false;
					fallResetter.add(event.player.getUniqueID());
					TeleportUtil.teleportToDimension(event.player, Wizardry.underWorld.getId(), 0, 300, 0);
					passmap.remove(event.player.getEntityId());
				} else if (event.player.motionY > ConfigValues.underworldFallSpeed) {//resets pass if stopped falling or slowed down alot
					passmap.remove(event.player.getEntityId());
				}
			}
		}

		if (!event.player.world.isRemote) {
			for (ItemStack stack : event.player.inventory.mainInventory) {
				if (stack.getItem() == ModItems.LEVITATION_ORB) {

					if (stack.getItemDamage() + 1 > stack.getMaxDamage()) {
						stack.shrink(1);
						continue;
					}

					if (event.player.world.getTotalWorldTime() % 10 == 0)
						stack.setItemDamage(stack.getItemDamage() + 1);

					if (!event.player.isPotionActive(ModPotions.LOW_GRAVITY)) {
						event.player.addPotionEffect(new PotionEffect(ModPotions.LOW_GRAVITY, 3, 3, false, false));
					}
					break;
				}
			}
		}
	}

	@SubscribeEvent
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
			}
		}
	}

	@SubscribeEvent
	public void onFlyFall(PlayerFlyableFallEvent event) {
		if (event.getEntityPlayer().getEntityWorld().provider.getDimension() == 0) {
			if (event.getEntityPlayer().posY <= 0) {
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
}
