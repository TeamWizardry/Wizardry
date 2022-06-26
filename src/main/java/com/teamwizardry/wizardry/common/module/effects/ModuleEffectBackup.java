package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.entity.EntityBackupZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import java.util.UUID;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.FACE_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_backup")
@Mod.EventBusSubscriber
public class ModuleEffectBackup implements IModuleEffect {
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if(world.isRemote) return true;

		Vec3d targetPos = spell.getTarget(world);
		EnumFacing facing = spell.getData(FACE_HIT);
		Entity caster = spell.getCaster(world);

		double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 20;

		if (!spellRing.taxCaster(world, spell, true)) return false;

		if (targetPos == null) return true;
		if (!(caster instanceof EntityLivingBase)) return true;
		if (facing != null && !world.isAirBlock(new BlockPos(targetPos))) {
			targetPos = new Vec3d(new BlockPos(targetPos).offset(facing)).add(0.5, 0.5, 0.5);
		}

		UUID player = caster.getUniqueID();

		WizardryWorld world1 = WizardryWorldCapability.get(world);

		if(world1 == null) return true;

		if(world1.getBackupCount(player) < ConfigValues.maxZombies) {
			EntityBackupZombie zombie = new EntityBackupZombie(world, (EntityLivingBase) caster, (int) duration);
			zombie.setPosition(targetPos.x, targetPos.y, targetPos.z);
			zombie.forceSpawn = true;
			world.spawnEntity(zombie);
			world1.incBackupCount(player);
		}

		return true;
	}

	@SubscribeEvent
	public static void onZombieDeath(LivingDeathEvent e) {
		if(e.getEntity() instanceof EntityBackupZombie) {
			if(((EntityBackupZombie)e.getEntity()).getOwner() != null) {
				WizardryWorld world = WizardryWorldCapability.get(e.getEntity().world);

				if(world != null) {
					world.decBackupCount(((EntityBackupZombie)e.getEntity()).getOwner());
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setAlphaFunction(new InterpFloatInOut(0.0f, 0.1f));
		glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
		glitter.enableMotionCalculation();
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setAcceleration(new Vec3d(0, -0.05, 0));
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), RandUtil.nextInt(20, 30), 0, (aFloat, particleBuilder) -> {
			if (RandUtil.nextInt(5) == 0) {
				glitter.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
			} else {
				glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
			}

			glitter.setScale(RandUtil.nextFloat());
			glitter.setLifetime(RandUtil.nextInt(50, 100));
			glitter.addMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(0.01, 0.05), RandUtil.nextDouble(-0.05, 0.05)));
		});

	}
}
