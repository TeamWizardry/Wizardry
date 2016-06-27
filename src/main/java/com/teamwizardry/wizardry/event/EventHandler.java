package com.teamwizardry.wizardry.event;

import com.teamwizardry.wizardry.ModItems;
import com.teamwizardry.wizardry.Wizardry;
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
    public void redstoneExistenentialCrisisEvent(TickEvent.WorldTickEvent event) {
        ArrayList<RedstoneTrackerObject> expiredRedstone = new ArrayList<>();
        for (RedstoneTrackerObject redstone : redstoneTracker)
            if (!redstone.isStartCountDown()) {
                if (redstone.getItem().isBurning() || redstone.getItem().isDead)
                    if (event.world.isMaterialInBB(redstone.getItem().getEntityBoundingBox().expand(0, 0.2, 0), Material.FIRE)) {
                        redstone.setStartCountDown(true);
                        redstone.setPos(redstone.getItem().getPosition());
                        redstone.getItem().setDead();
                    }
            } else {
                if (redstone.getCountdown() >= 30) {
                    EntityItem vinteum = new EntityItem(event.world, redstone.getPos().getX() + 0.5, redstone.getPos().getY() + 0.5, redstone.getPos().getZ() + 0.5, new ItemStack(ModItems.vinteumDust, redstone.getStackSize()));
                    vinteum.setNoPickupDelay();
                    vinteum.setVelocity(0, 0.5, 0);
                    event.world.spawnEntityInWorld(vinteum);
                    expiredRedstone.add(redstone);
                } else redstone.setCountdown(redstone.getCountdown() + 1);
            }
        redstoneTracker.removeAll(expiredRedstone);
    }
}
