package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.bloat.TeleportUtil;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.trackerobject.BookTrackerObject;
import com.teamwizardry.wizardry.api.trackerobject.DevilDustTracker;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.client.fx.GlitterFactory;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;

import static com.teamwizardry.wizardry.common.fluid.FluidBlockMana.bookTracker;

public class EventHandler {

	private ArrayList<DevilDustTracker> redstoneTracker = new ArrayList<>();

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
				redstoneTracker.add(new DevilDustTracker(item, 200));
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
		// DEVIL DUST SPAWNING
		redstoneTracker.forEach(DevilDustTracker::tick);
		redstoneTracker.removeAll(DevilDustTracker.expired);
		DevilDustTracker.expired.clear();

		// BOOK SPAWNING
		ArrayList<BookTrackerObject> expiredBooks = new ArrayList<>();
		for (BookTrackerObject book : bookTracker) {

			if (book.getQueue() < book.getHelix().size()) {
				Vec3d location = book.getHelix().get(book.getQueue());

				for (int i = 0; i < 10 * Wizardry.proxy.getParticleDensity() / 100; i++) {
					SparkleFX fizz = GlitterFactory.getInstance().createSparkle(book.getWorld(), location, 100);
					fizz.setFadeOut();
					fizz.setAlpha(0.5f);
					fizz.setScale(0.5f);
					fizz.setColor(Color.WHITE);
					fizz.setRandomlyShiftColor(-0.2f, 0.2f, true, false, false);
					fizz.setRandomDirection(0.05, 0, 0.05);
					fizz.setJitter(10, 0.05, 0, 0.05);
					fizz.addMotion(0, ThreadLocalRandom.current().nextDouble(-0.2, -0.1), 0);
				}
				if (book.getQueue() % 5 == 0)
					book.getWorld().playSound(null, location.xCoord, location.yCoord, location.zCoord, ModSounds.FIZZING_LOOP, SoundCategory.BLOCKS, 0.7F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);
				book.setQueue(book.getQueue() + 1);
			} else {
				for (int i = 0; i < 600; i++) {
					SparkleFX fizz = GlitterFactory.getInstance().createSparkle(book.getWorld(), new Vec3d(book.getX(), book.getY() + 10, book.getZ()), 200);
					fizz.setFadeOut();
					fizz.setAlpha(0.5f);
					fizz.setScale(0.5f);
					//fizz.setJitter(10, 0.05, 0, 0.05);
					fizz.setRandomDirection(0.3, 0, 0.3);
					fizz.addMotion(0, ThreadLocalRandom.current().nextDouble(-0.2, -0.1), 0);
				}

				EntityItem ei = new EntityItem(book.getWorld(), book.getX(), book.getY() + 10, book.getZ(), new ItemStack(ModItems.PHYSICS_BOOK));
				book.getWorld().spawnEntityInWorld(ei);
				book.getWorld().playSound(null, book.getX(), book.getY(), book.getZ(), ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1F);
				expiredBooks.add(book);
			}
		}
		bookTracker.removeAll(expiredBooks);
	}

	@SubscribeEvent
	public void onFallDamage(LivingHurtEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		if (event.getEntity().getEntityWorld().provider.getDimension() != Wizardry.underWorld.getId()) {
			if (event.getSource() == EntityDamageSource.fall) {
				if (event.getEntity().fallDistance >= 250) {
					BlockPos location = event.getEntity().getPosition();
					BlockPos bedrock = PosUtils.INSTANCE.checkNeighbor(event.getEntity().getEntityWorld(), location, Blocks.BEDROCK);
					if (bedrock != null) {
						if (event.getEntity().getEntityWorld().getBlockState(bedrock).getBlock() == Blocks.BEDROCK) {
							TeleportUtil.INSTANCE.teleportToDimension((EntityPlayer) event.getEntity(), Wizardry.underWorld.getId(), 0, 100, 0);
							event.getEntity().fallDistance = -300;
							((EntityPlayer) event.getEntity()).addStat(Achievements.CRUNCH);
							event.setCanceled(true);
						}
					}
				}
			}
		} else {
			if (event.getSource() == EntityDamageSource.outOfWorld) {
				BlockPos bed = ((EntityPlayer) event.getEntity()).getBedLocation(0).add(0, 255, 0);
				TeleportUtil.INSTANCE.teleportToDimension((EntityPlayer) event.getEntity(), 0, bed.getX(), bed.getY(), bed.getZ());
				event.getEntity().fallDistance = -300;
				event.setCanceled(true);
			}
		}
	}
}