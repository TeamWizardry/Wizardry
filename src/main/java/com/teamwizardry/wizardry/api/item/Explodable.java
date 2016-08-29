package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/30/2016.
 */
public interface Explodable {

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

		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		ParticleSpawner.spawn(glitter, entityIn.worldObj, new StaticInterp<>(entityIn.getPositionVector()), 300, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(100, 150)));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5)));
			glitter.disableMotion();
		});
	}
}
