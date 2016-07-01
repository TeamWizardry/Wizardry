package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.api.util.misc.PosUtils;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.trackerobject.BookTrackerObject;
import com.teamwizardry.wizardry.api.trackerobject.RedstoneTrackerObject;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
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
        // WARNING: VOLATILE CODE. DO NOT TOUCH. //
        // Vinteum spawning //
        ArrayList<RedstoneTrackerObject> expiredRedstone = new ArrayList<>();
        ArrayList<EntityItem> vinteumList = new ArrayList<>();
        for (RedstoneTrackerObject redstone : redstoneTracker)
            if (!redstone.isStartCountDown()) {
                if (redstone.getItem().isBurning() || redstone.getItem().isDead)
                    if (event.world.isMaterialInBB(redstone.getItem().getEntityBoundingBox().expand(0, 0.2, 0), Material.FIRE)) {

                        redstone.setPos(PosUtils.adjustPositionToBlock(event.world, redstone.getItem().getPosition(), Blocks.FIRE));
                        redstone.setStartCountDown(true);
                        redstone.getItem().setDead();
                    }
            } else {
                if (redstone.getCountdown() >= 200) {
                    EntityItem vinteum = new EntityItem(event.world, redstone.getPos().getX() + 0.5, redstone.getPos().getY() + 0.5, redstone.getPos().getZ() + 0.5, new ItemStack(ModItems.VINTEUM_DUST, redstone.getStackSize()));
                    vinteum.setPickupDelay(0);
                    vinteum.setVelocity(0, 0.7, 0);
                    vinteumList.add(vinteum);
                    expiredRedstone.add(redstone);

                } else {
                    redstone.setCountdown(redstone.getCountdown() + 1);
                    event.world.playSound(null, redstone.getPos(), ModSounds.FIRE_SIZZLE_LOOP, SoundCategory.BLOCKS, 0.3F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);

                    SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(event.world, redstone.getPos().getX() + 0.5, redstone.getPos().getY() + 0.3, redstone.getPos().getZ() + 0.5, 1, 1F, 10, false);
                    fizz.jitter(10, 0.05, 0.05, 0.05);
                    fizz.randomDirection(0.1, 0, 0.1);
                    fizz.setMotion(0, ThreadLocalRandom.current().nextDouble(0.05, 0.2), 0);
                    fizz.setColor(128, 0, 128);
                    fizz.randomlyOscillateColor();
                }
            }
        for (EntityItem vinteum : vinteumList) {
            vinteum.forceSpawn = true;
            event.world.spawnEntityInWorld(vinteum);
        }
        vinteumList.clear();
        redstoneTracker.removeAll(expiredRedstone);
        // WARNING: VOLATILE CODE. DO NOT TOUCH. //


        ArrayList<BookTrackerObject> expiredBooks = new ArrayList<>();
        for (BookTrackerObject book : bookTracker) {

            book.getItem().setInvisible(true);
            book.getItem().setInfinitePickupDelay();

            if (book.isStartCountDown() && !book.getItem().isDead) {
                if (book.getCountdown() >= 500) {

                    book.getWorld().spawnEntityInWorld(new EntityItem(book.getWorld(), book.getX(), book.getY(), book.getZ(), new ItemStack(ModItems.PHYSICS_BOOK)));
                    book.setStartCountDown(false);
                    book.getItem().setDead();
                    expiredBooks.add(book);
                } else {
                    book.setCountdown(book.getCountdown() + 1);
                    book.setY(book.getY() + 0.005);
                    if (book.itemExists()) {
                        SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(event.world, book.getX(), book.getY() + 0.5, book.getZ(), 1, 1F, 10, false);
                        fizz.jitter(10, 0.05, 0.05, 0.05);
                        fizz.randomDirection(0.05, 0.05, 0.05);
                    }
                }
            }
        }
        bookTracker.removeAll(expiredBooks);
    }
}