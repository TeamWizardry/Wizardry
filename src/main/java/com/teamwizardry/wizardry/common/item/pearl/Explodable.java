package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.particle.SparkleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Saad on 6/30/2016.
 */
public class Explodable extends Item {

    private List<Integer> potions = new ArrayList<>();

    Explodable() {
        addPotions();
    }

    private void addPotions() {
        potions.add(1);
        potions.add(3);
        potions.add(5);
        potions.add(8);
        potions.add(11);
        potions.add(12);
        potions.add(21);
    }

    public void explode(Entity entityIn) {
        Random rand = new Random();
        int range = 5;
        List<EntityLivingBase> entitys = entityIn.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(entityIn.posX - range, entityIn.posY - range, entityIn.posZ - range, entityIn.posX + range, entityIn.posY + range, entityIn.posZ + range));
        for (EntityLivingBase e : entitys)
            e.addPotionEffect(new PotionEffect(Potion.getPotionById(potions.get(rand.nextInt(potions.size()))), rand.nextInt(30) * 20, rand.nextInt(2) + 1));

        for (int i = 0; i < 300; i++) {
            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(entityIn.worldObj, entityIn.posX, entityIn.posY + 0.5, entityIn.posZ, 1, 1F, 30, false);
            fizz.jitter(10, 0.1, 0.1, 0.1);
            fizz.randomDirection(0.3, 0.3, 0.3);
            fizz.setRandomizedSizes(true);
        }
    }
}
