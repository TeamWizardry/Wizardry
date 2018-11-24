package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import javax.annotation.Nonnull;



@RegisterModule(ID = "effect_poison_cloud")
public class ModuleEffectPoisonCloud implements IModuleEffect, ILingeringModule {

    @Override
    public String[] compatibleModifierClasses() {
        return new String[]{"modifier_increase_aoe", "modifier_increase_potency", "modifier_extend_time"};
    }

    @Override
    public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
        World world = spell.world;
        Vec3d position = spell.getTarget();

        if (position == null) return false;

        if (!spellRing.taxCaster(spell, true)) return false;

        double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);

        double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);

        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(position)).grow(area, area, area))) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                if(potency >= 3) {
                    living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100));
                }
                living.addPotionEffect(new PotionEffect(MobEffects.POISON, 60, (int) (potency/3)));
            }
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
        Vec3d position = spell.getTarget();

        if (position == null) return;


        ParticleBuilder glitter = new ParticleBuilder(0);
        glitter.setColor(instance.getPrimaryColor());
        glitter.setScale(1);
        glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SMOKE));
        glitter.disableRandom();

        ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(position), 10, 0, (aFloat, particleBuilder) -> {
            glitter.setLifetime(RandUtil.nextInt(10, 40));
            glitter.setScale(RandUtil.nextFloat());
            glitter.setAlphaFunction(new InterpFloatInOut(0.3f, RandUtil.nextFloat()));
            double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);
            double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
            double r = area * RandUtil.nextFloat();
            double x = r * MathHelper.cos((float) theta);
            double z = r * MathHelper.sin((float) theta);
            Vec3d start = new Vec3d(x,0,z);
            glitter.setPositionFunction(new InterpLine(start,start.add(0,area,0)));
        });
    }

    @Override
    public int getLingeringTime(SpellData spell, SpellRing spellRing) {
        return (int) (spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10);
    }
}
