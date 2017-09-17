package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.MISC;
import com.teamwizardry.wizardry.api.events.SpellCastEvent;
import com.teamwizardry.wizardry.api.item.RedstoneTracker;
import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.TeleportUtil;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventHandler {

	private static Function2<Entity, Object, Unit> entityFireHandler = MethodHandleHelper.wrapperForSetter(Entity.class, "isImmuneToFire", "field_70178_ae", "sm");
	private final ArrayList<UUID> fallResetUUIDs = new ArrayList<>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent event) {
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE));
		event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
	}

	@SubscribeEvent
	public void redstoneHandler(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityItem) {
			EntityItem item = (EntityItem) event.getEntity();
			if (item.getItem().getItem() == Items.REDSTONE) {
				entityFireHandler.invoke(item, true);
				RedstoneTracker.INSTANCE.addRedstone(item, event.getWorld());
			} else if (item.getItem().getItem() == ModItems.DEVIL_DUST) {
				entityFireHandler.invoke(item, true);
				item.extinguish();
			}
		}
	}

	@SubscribeEvent
	public void tickEvent(WorldTickEvent event) {
		RedstoneTracker.INSTANCE.tick();
		if (event.phase != Phase.START) {
			if (!fallResetUUIDs.isEmpty())
				event.world.playerEntities.stream().filter(entity -> fallResetUUIDs.contains(entity.getUniqueID())).forEach(entity -> entity.fallDistance = -500);
			fallResetUUIDs.clear();
		}
	}

	@SubscribeEvent
	public void underworldVoidDamage(LivingHurtEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		if (event.getEntity().getEntityWorld().provider.getDimension() != Wizardry.underWorld.getId()) return;
		if (event.getSource() == EntityDamageSource.OUT_OF_WORLD) {
			EntityPlayer player = ((EntityPlayer) event.getEntityLiving());
			BlockPos spawn = player.isSpawnForced(0) ? player.getBedLocation(0) : player.world.getSpawnPoint().add(player.world.rand.nextGaussian() * 16, 0, player.world.rand.nextGaussian() * 16);
			BlockPos teleportTo = spawn.add(0, 300 - spawn.getY(), 0);
			TeleportUtil.teleportToDimension((EntityPlayer) event.getEntity(), 0, teleportTo.getX(), teleportTo.getY(), teleportTo.getZ());
			event.getEntity().fallDistance = -500;
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onBedrockSmack(LivingFallEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		if (event.getEntity().getEntityWorld().provider.getDimension() == 0) {
			if (event.getEntity().fallDistance >= 250) {
				BlockPos location = event.getEntity().getPosition();
				BlockPos bedrock = PosUtils.checkNeighbor(event.getEntity().getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					if (event.getEntity().getEntityWorld().getBlockState(bedrock).getBlock() == Blocks.BEDROCK) {
						TeleportUtil.teleportToDimension((EntityPlayer) event.getEntity(), Wizardry.underWorld.getId(), 0, 300, 0);
						((EntityPlayer) event.getEntity()).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));
						fallResetUUIDs.add(event.getEntity().getUniqueID());
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onFlyFall(PlayerFlyableFallEvent event) {
		if (event.getEntityPlayer().getEntityWorld().provider.getDimension() == 0) {
			if (event.getEntityPlayer().fallDistance >= 250) {
				BlockPos location = event.getEntityPlayer().getPosition();
				BlockPos bedrock = PosUtils.checkNeighbor(event.getEntity().getEntityWorld(), location, Blocks.BEDROCK);
				if (bedrock != null) {
					if (event.getEntity().getEntityWorld().getBlockState(bedrock).getBlock() == Blocks.BEDROCK) {
						TeleportUtil.teleportToDimension(event.getEntityPlayer(), Wizardry.underWorld.getId(), 0, 300, 0);
						((EntityPlayer) event.getEntity()).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));
						fallResetUUIDs.add(event.getEntityPlayer().getUniqueID());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void fairyAmbush(SpellCastEvent event) {
		Entity caster = event.spell.getData(SpellData.DefaultKeys.CASTER);
		int chance = 5;
		for (Module module : event.module.getAllChildModules())
			if (module instanceof IContinuousModule) {
				chance = 1000;
				break;
			}
		if (RandUtil.nextInt(chance) == 0 && caster != null) {
			List<EntityFairy> fairyList = event.spell.world.getEntitiesWithinAABB(EntityFairy.class, new AxisAlignedBB(caster.getPosition()).grow(64, 64, 64));
			for (EntityFairy fairy : fairyList) {
				fairy.ambush = true;
			}
		}
	}
}
