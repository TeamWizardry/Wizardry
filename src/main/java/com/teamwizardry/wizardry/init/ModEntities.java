package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.entity.*;
import com.teamwizardry.wizardry.common.entity.*;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityCorruptionProjectile;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityZachriel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Demoniaque on 8/17/2016.
 */
public class ModEntities {

	private static int i = 0;

	public static void init() {
		registerEntity(new ResourceLocation(Wizardry.MODID, "bomb"), EntityBomb.class, "bomb", 256, 1, true);
		registerEntity(new ResourceLocation(Wizardry.MODID, "halo_infusion_item"), EntityHaloInfusionItem.class, "halo_infusion_item", 256, 1, true);
		registerEntity(new ResourceLocation(Wizardry.MODID, "spirit_blight"), EntitySpiritBlight.class, "spirit_blight");
		registerEntity(new ResourceLocation(Wizardry.MODID, "spirit_wight"), EntitySpiritWight.class, "spirit_wight");
		registerEntity(new ResourceLocation(Wizardry.MODID, "angel_zachriel"), EntityZachriel.class, "angel_zachriel");
		registerEntity(new ResourceLocation(Wizardry.MODID, "fairy"), EntityFairy.class, "fairy");
		registerEntity(new ResourceLocation(Wizardry.MODID, "spell_projectile"), EntitySpellProjectile.class, "spell_projectile", 256, 1, true);
		registerEntity(new ResourceLocation(Wizardry.MODID, "jump_pad"), EntityJumpPad.class, "jump_pad", 64, 1, false);
		registerEntity(new ResourceLocation(Wizardry.MODID, "zachriel_corruption"), EntityCorruptionProjectile.class, "zachriel_corruption", 64, 1, false);
		registerEntity(new ResourceLocation(Wizardry.MODID, "unicorn"), EntityUnicorn.class, "unicorn");
		registerEntity(new ResourceLocation(Wizardry.MODID, "summon_zombie"), EntityBackupZombie.class, "summon_zombie");
	}

	public static void registerEntity(ResourceLocation loc, Class<? extends Entity> entityClass, String entityName) {
		registerEntity(loc, entityClass, entityName, 256, 1, true);
	}

	//Use when default parameters are not sufficient, e.g fast-moving projectiles
	public static void registerEntity(ResourceLocation loc, Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(loc, entityClass, entityName, i, Wizardry.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
		i++;
	}

	// TODO: ModelEvent?
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		RenderingRegistry.registerEntityRenderingHandler(EntityZachriel.class, manager -> new RenderZachriel(manager, new ModelZachriel()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritBlight.class, manager -> new RenderSpiritBlight(manager, new ModelSpiritBlight()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritWight.class, manager -> new RenderSpiritWight(manager, new ModelSpiritWight()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFairy.class, manager -> new RenderFairy(manager, new ModelNull()));
		RenderingRegistry.registerEntityRenderingHandler(EntityUnicorn.class, manager -> new RenderUnicorn(manager, new ModelUnicorn()));
		RenderingRegistry.registerEntityRenderingHandler(EntityJumpPad.class, manager -> new RenderJumpPad(manager, new ModelNull()));
		RenderingRegistry.registerEntityRenderingHandler(EntityCorruptionProjectile.class, RenderZachrielCorruption::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellProjectile.class, RenderSpellProjectile::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityHaloInfusionItem.class, RenderHaloInfusionItem::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBackupZombie.class, RenderSummonZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBomb.class, RenderBomb::new);
	}
}
