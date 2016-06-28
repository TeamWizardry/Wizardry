package com.teamwizardry.wizardry.event;

import com.teamwizardry.wizardry.ModItems;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.trackers.BookTrackerObject;
import com.teamwizardry.wizardry.api.trackers.RedstoneTrackerObject;
import com.teamwizardry.wizardry.particles.SparkleFX;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

import static com.teamwizardry.wizardry.fluid.FluidBlockMana.bookTracker;

public class EventHandler {

    private ArrayList<RedstoneTrackerObject> redstoneTracker = new ArrayList<>();

    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "items/manaIconNoOutline"));
        event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "items/manaIconOutline"));
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
        ArrayList<RedstoneTrackerObject> expiredRedstone = new ArrayList<>();
        ArrayList<EntityItem> vinteumList = new ArrayList<>();
        for (RedstoneTrackerObject redstone : redstoneTracker)
            if (!redstone.isStartCountDown()) {
                if (redstone.getItem().isBurning() || redstone.getItem().isDead)
                    if (event.world.isMaterialInBB(redstone.getItem().getEntityBoundingBox().expand(0, 0.2, 0), Material.FIRE)) {
                        redstone.setStartCountDown(true);
                        redstone.setPos(redstone.getItem().getPosition());
                        redstone.getItem().setDead();
                    }
            } else {
                if (redstone.getCountdown() >= 50) {
                    EntityItem vinteum = new EntityItem(event.world, redstone.getPos().getX() + 0.5, redstone.getPos().getY() + 0.5, redstone.getPos().getZ() + 0.5, new ItemStack(ModItems.vinteumDust, redstone.getStackSize()));
                    vinteum.setPickupDelay(5);
                    vinteum.setVelocity(0, 0.7, 0);
                    vinteumList.add(vinteum);
                    expiredRedstone.add(redstone);

                } else redstone.setCountdown(redstone.getCountdown() + 1);
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

                    book.getWorld().spawnEntityInWorld(new EntityItem(book.getWorld(), book.getX(), book.getY(), book.getZ(), new ItemStack(ModItems.physicsBook)));
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