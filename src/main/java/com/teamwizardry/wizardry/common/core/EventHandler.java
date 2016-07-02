package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.api.util.misc.PosUtils;
import com.teamwizardry.librarianlib.math.shapes.Helix;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Config;
import com.teamwizardry.wizardry.api.trackerobject.BookTrackerObject;
import com.teamwizardry.wizardry.api.trackerobject.RedstoneTrackerObject;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

import static com.teamwizardry.wizardry.common.fluid.FluidBlockMana.bookTracker;

public class EventHandler {

    private ArrayList<RedstoneTrackerObject> redstoneTracker = new ArrayList<>();

    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "item/manaIconNoOutline"));
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "item/manaIconOutline"));
    }

    @SubscribeEvent
    public void redstoneBornEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityItem) {
            EntityItem item = (EntityItem) event.getEntity();
            if (item.getEntityItem().getItem() == Items.REDSTONE)
                redstoneTracker.add(new RedstoneTrackerObject(item));
        }
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.WorldTickEvent event) {
        // VINTEUM SPAWNING
        ArrayList<RedstoneTrackerObject> expiredRedstone = new ArrayList<>();

        for (RedstoneTrackerObject redstone : redstoneTracker) {

            if (!redstone.hasAdjusted()) {
                BlockPos pos = PosUtils.adjustPositionToBlock(event.world, new BlockPos(redstone.getRedstone().posX, redstone.getRedstone().posY, redstone.getRedstone().posZ), Blocks.FIRE);
                if (redstone.getWorld().getBlockState(pos).getBlock() == Blocks.FIRE) {

                    redstone.setPos(pos.add(0.5, 0, 0.5));
                    redstone.setHelix(new Helix(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), 100, 2, 3, 1, 10, true).getPoints());
                    redstone.getRedstone().setDead();
                    redstone.setStartCountdown(true);

                    redstone.setHasAdjusted(true);

                }
            }

            if (redstone.isStartCountdown()) {
                if (redstone.getCountdown() >= 200) {
                    if (!redstone.hasVinteumSpawned()) {
                        EntityItem vinteum = new EntityItem(redstone.getWorld(), redstone.getPos().xCoord + 0.5, redstone.getPos().yCoord + 0.5, redstone.getPos().zCoord + 0.5, new ItemStack(ModItems.VINTEUM_DUST, redstone.getStackSize()));
                        vinteum.setPickupDelay(5);
                        vinteum.setVelocity(0, 0.8, 0);
                        vinteum.forceSpawn = true;
                        event.world.spawnEntityInWorld(vinteum);

                        redstone.setHasVinteumSpawned(true);
                        redstone.setVinteum(vinteum);
                        redstone.setStartCountdown(false);
                        expiredRedstone.add(redstone);
                    }
                } else {
                    redstone.setCountdown(redstone.getCountdown() + 1);

                    if (redstone.getQueue() < redstone.getHelix().size()) {
                        for (int i = 0; i < 10 / Config.particlePercentage; i++) {
                            Vec3d location = redstone.getHelix().get(redstone.getQueue());
                            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(event.world, location.xCoord, location.yCoord, location.zCoord, 0.8F, 0.5F, 50, false);
                            fizz.setMotion(0, -0.1, 0);
                            fizz.jitter(20, 0.05, 0.05, 0.05);
                            fizz.setColor((int) Color.RED.r, (int) Color.RED.g, (int) Color.RED.b);
                            fizz.randomlyOscillateColor(true, false, false);
                        }
                        redstone.setQueue(redstone.getQueue() + 1);
                    }
                    if (redstone.getQueue() % 5 == 0)
                        redstone.getWorld().playSound(null, redstone.getPos().xCoord, redstone.getPos().yCoord, redstone.getPos().zCoord, ModSounds.FIRE_SIZZLE_LOOP, SoundCategory.BLOCKS, 0.7F, (float) ThreadLocalRandom.current().nextDouble(0.8, 1.3));
                }
            }
        }
        redstoneTracker.removeAll(expiredRedstone);


        // BOOK SPAWNING
        ArrayList<BookTrackerObject> expiredBooks = new ArrayList<>();
        for (BookTrackerObject book : bookTracker) {

            if (book.getQueue() < book.getHelix().size()) {
                Vec3d location = book.getHelix().get(book.getQueue());

                for (int i = 0; i < 10 / Config.particlePercentage; i++) {
                    SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(book.getWorld(), location.xCoord, location.yCoord, location.zCoord, 0.5F, 0.5F, 100, false);
                    fizz.jitter(10, 0.01, 0, 0.01);
                    fizz.randomDirection(0.05, 0, 0.05);
                    fizz.setMotion(0, ThreadLocalRandom.current().nextDouble(-0.2, -0.05), 0);
                }
                if (book.getQueue() % 5 == 0)
                    book.getWorld().playSound(null, location.xCoord, location.yCoord, location.zCoord, ModSounds.FIRE_SIZZLE_LOOP, SoundCategory.BLOCKS, 0.7F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);
                book.setQueue(book.getQueue() + 1);
            } else {
                for (int i = 0; i < 600 / Config.particlePercentage; i++) {
                    SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(book.getWorld(), book.getX(), book.getY() + 8, book.getZ(), 1F, 0.5F, 200, true);
                    fizz.jitter(10, 0.01, 0, 0.01);
                    fizz.randomDirection(0.25, 0.01, 0.25);
                    fizz.setMotion(0, ThreadLocalRandom.current().nextDouble(-0.2, -0.05), 0);
                }

                EntityItem ei = new EntityItem(book.getWorld(), book.getX(), book.getY() + 10, book.getZ(), new ItemStack(ModItems.PHYSICS_BOOK));
                book.getWorld().spawnEntityInWorld(ei);
                book.getWorld().playSound(null, book.getX(), book.getY(), book.getZ(), ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1F);
                expiredBooks.add(book);
            }
        }
        bookTracker.removeAll(expiredBooks);
    }
}