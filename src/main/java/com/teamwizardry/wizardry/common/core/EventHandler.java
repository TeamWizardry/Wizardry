package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.api.util.misc.PosUtils;
import com.teamwizardry.librarianlib.math.shapes.Helix;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.trackerobject.BookTrackerObject;
import com.teamwizardry.wizardry.api.trackerobject.RedstoneTrackerObject;
import com.teamwizardry.wizardry.client.fx.GlitterFactory;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

import static com.teamwizardry.wizardry.common.fluid.FluidBlockMana.bookTracker;

public class EventHandler {

    private ArrayList<RedstoneTrackerObject> redstoneTracker = new ArrayList<>();

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
                redstoneTracker.add(new RedstoneTrackerObject(item));
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
        ArrayList<RedstoneTrackerObject> expiredRedstone = new ArrayList<>();

        for (RedstoneTrackerObject redstone : redstoneTracker) {

            if (!redstone.hasAdjusted()) {
                BlockPos pos = PosUtils.adjustPositionToBlock(event.world, new BlockPos(redstone.getRedstone().posX, redstone.getRedstone().posY, redstone.getRedstone().posZ), Blocks.FIRE);
                if (redstone.getWorld().getBlockState(pos).getBlock() == Blocks.FIRE
                        && redstone.getWorld().isMaterialInBB(redstone.getRedstone().getEntityBoundingBox().expand(0.1, 0.1, 0.1), Material.FIRE)) {

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
                        EntityItem vinteum = new EntityItem(redstone.getWorld(), redstone.getPos().xCoord + 0.5, redstone.getPos().yCoord + 0.5, redstone.getPos().zCoord + 0.5, new ItemStack(ModItems.DEVIL_DUST, redstone.getStackSize()));
                        vinteum.setPickupDelay(5);
                        vinteum.setVelocity(0, 0.8, 0);
                        vinteum.forceSpawn = true;
                        event.world.spawnEntityInWorld(vinteum);

                        redstone.setHasVinteumSpawned(true);
                        redstone.setVinteum(vinteum);
                        redstone.setStartCountdown(false);
                        expiredRedstone.add(redstone);
                        redstone.getWorld().playSound(null, redstone.getPos().xCoord, redstone.getPos().yCoord, redstone.getPos().zCoord, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.7F, (float) ThreadLocalRandom.current().nextDouble(0.8, 1.3));
                    }
                } else {
                    redstone.setCountdown(redstone.getCountdown() + 1);

                    if (redstone.getQueue() < redstone.getHelix().size()) {
                        for (int i = 0; i < 10 * Wizardry.proxy.getParticleDensity() / 100; i++) {
                            Vec3d location = redstone.getHelix().get(redstone.getQueue());
                            SparkleFX fizz = GlitterFactory.getInstance().createSparkle(event.world, location, 150);
                            fizz.setShrink();
                            fizz.setFadeOut();
                            fizz.setAlpha(0.8f);
                            fizz.setScale(0.5f);
                            fizz.setMotion(0, -0.1, 0);
                            fizz.setJitter(30, 0.05, 0, 0.05);
                            fizz.setColor(Color.RED);
                            fizz.setRandomlyShiftColor(-1f, -0.9f, true, false, false);
                        }
                        redstone.setQueue(redstone.getQueue() + 1);
                    }

                    if (redstone.getQueue() % 5 == 0)
                        redstone.getWorld().playSound(null, redstone.getPos().xCoord, redstone.getPos().yCoord, redstone.getPos().zCoord, ModSounds.FRYING_SIZZLE, SoundCategory.BLOCKS, 0.7F, (float) ThreadLocalRandom.current().nextDouble(0.8, 1.3));
                }
            }
        }
        redstoneTracker.removeAll(expiredRedstone);

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
}