package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.fx.GlitterFactory;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Saad on 6/30/2016.
 */
public interface IExplodable {

    List<Integer> potions = new ArrayList<>();

    default void explode(Entity entityIn) {
        if (potions.isEmpty()) {
            potions.add(1);
            potions.add(3);
            potions.add(5);
            potions.add(8);
            potions.add(11);
            potions.add(12);
            potions.add(21);
        }

        Random rand = new Random();
        int range = 5;
        List<EntityLivingBase> entitys = entityIn.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(entityIn.posX - range, entityIn.posY - range, entityIn.posZ - range, entityIn.posX + range, entityIn.posY + range, entityIn.posZ + range));
        for (EntityLivingBase e : entitys)
            e.addPotionEffect(new PotionEffect(Potion.getPotionById(potions.get(rand.nextInt(potions.size()))), rand.nextInt(30) * 20, rand.nextInt(2) + 1));

        for (int i = 0; i < 300; i++) {
            SparkleFX fizz = GlitterFactory.getInstance().createSparkle(entityIn.worldObj, entityIn.getPositionVector(), 30);
            fizz.setAlpha(1f);
            fizz.setScale(0.5f);
            fizz.setShrink();
            fizz.setRandomDirection(0.4, 0.4, 0.4);
        }
    }
}
