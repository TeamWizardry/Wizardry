package com.teamwizardry.wizardry.common.module.effects.vanish;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.events.EntityRenderShadowAndFireEvent;
import com.teamwizardry.wizardry.common.network.PacketSyncVanish;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Mod.EventBusSubscriber
public class VanishTracker {

	private static final List<VanishedObject> vanishes = new ArrayList<>();
	private static final Deque<VanishedObject> adds = new ArrayDeque<>();

	public static void addVanishObject(int entityID, int time) {
		for (VanishedObject v : vanishes) {
			if (v == null) continue;
			if (v.entityID == entityID) {
				v.tick = time;
				return;
			}
		}

		for (VanishedObject v : adds) {
			if (v == null) continue;
			if (v.entityID == entityID) {
				v.tick = time;
				return;
			}
		}

		adds.add(new VanishedObject(entityID, time));
	}

	public static NBTTagCompound serialize() {
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagList vanishList = new NBTTagList();
		for (VanishedObject v : vanishes) {
			vanishList.appendTag(v.serialize());
		}

		NBTTagList addsList = new NBTTagList();
		for (VanishedObject v : adds) {
			addsList.appendTag(v.serialize());
		}

		compound.setTag("vanishes", vanishList);
		compound.setTag("adds", addsList);

		return compound;
	}

	public static void deserialize(NBTTagCompound compound) {
		if (compound.hasKey("vanishes")) {
			vanishes.clear();

			NBTTagList list = compound.getTagList("vanishes", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : list) {
				if (base instanceof NBTTagCompound) {
					vanishes.add(VanishedObject.deserialize((NBTTagCompound) base));
				}
			}
		}

		if (compound.hasKey("adds")) {
			adds.clear();

			NBTTagList list = compound.getTagList("adds", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : list) {
				if (base instanceof NBTTagCompound) {
					adds.add(VanishedObject.deserialize((NBTTagCompound) base));
				}
			}
		}
	}

	public static boolean isVanished(Entity entity) {
		int id = entity.getEntityId();
		for (VanishedObject v : vanishes) {
			if (v.entityID == id) return true;
		}
		return false;
	}

	@SubscribeEvent
	public static void vanishTicker(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (event.type != TickEvent.Type.WORLD) return;
		if (event.side != Side.SERVER) return;

		final int sizeAddsBefore = adds.size();
		final int sizeVanishesBefore = vanishes.size();

		for (VanishedObject entry; ((entry = adds.pollFirst()) != null); ) {
			vanishes.add(entry);
		}
		vanishes.removeIf(entry -> {
			if (--entry.tick < 0) {
				Entity e = event.world.getEntityByID(entry.entityID);
				if (e != null && !event.world.isRemote) {
					event.world.playSound(null, e.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 0.5f, 1);
				}

				return true;
			}
			return false;
		});

		final int sizeAddsAfter = adds.size();
		final int sizeVanishesAfter = vanishes.size();

		if (!event.world.isRemote && sizeAddsAfter != sizeAddsBefore || sizeVanishesAfter != sizeVanishesBefore) {
			PacketHandler.NETWORK.sendToAll(new PacketSyncVanish(serialize()));
		}
	}

	@SubscribeEvent
	public static void interact(PlayerInteractEvent event) {
		if (isVanished(event.getEntityPlayer())) {
			event.setCancellationResult(EnumActionResult.FAIL);
			event.setResult(Event.Result.DENY);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderItem(RenderHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		if (isVanished(player)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void ai(LivingSetAttackTargetEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) return;
		if (!(event.getEntityLiving() instanceof EntityLiving)) return;

		EntityLivingBase potentialPotion = ((EntityLiving) event.getEntityLiving()).getAttackTarget();
		if (potentialPotion != null && isVanished(potentialPotion)) {
			((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
		}
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void doRenderOverride(RenderLivingEvent.Pre event) {
		EntityLivingBase entity = event.getEntity();

		if (isVanished(entity)) {
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderShadowAndFire(EntityRenderShadowAndFireEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		boolean iWalked = new Vec3d(player.posX, player.posY, player.posZ).distanceTo(new Vec3d(player.prevPosX, player.prevPosY, player.prevPosZ)) > 0.2;
		if (!(event.entity instanceof EntityLivingBase)) return;

		if (isVanished(event.entity))
			event.override = true;
	}

	public static class VanishedObject {

		final int entityID;
		int tick;

		public VanishedObject(int entityID, int tick) {
			this.entityID = entityID;
			this.tick = tick;
		}

		@Nullable
		public static VanishedObject deserialize(NBTTagCompound compound) {
			if (compound.hasKey("entity_id") && compound.hasKey("tick")) {
				return new VanishedObject(compound.getInteger("entity_id"), compound.getInteger("tick"));
			}
			return null;
		}

		public NBTTagCompound serialize() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("entity_id", entityID);
			compound.setInteger("tick", tick);
			return compound;
		}
	}
}
