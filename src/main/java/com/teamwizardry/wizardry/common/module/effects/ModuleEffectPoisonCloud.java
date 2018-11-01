package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
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
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ORIGIN;


@RegisterModule(ID="effect_poison_cloud")
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

        double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);

        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(position)).grow(area, area, area))) {
            if (entity == null) continue;
            if(entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                living.addPotionEffect(new PotionEffect(Potion.getPotionById(9),100));
                living.addPotionEffect(new PotionEffect(Potion.getPotionById(19),60));
            }
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
        Vec3d position = spell.getData(ORIGIN);

        if (position == null) return;

        ParticleBuilder glitter = new ParticleBuilder(0);
        glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
        ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(position), 50, 10, (aFloat, particleBuilder) -> {
            glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
            glitter.setAlphaFunction(new InterpFloatInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
            glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SMOKE));
            glitter.setLifetime(RandUtil.nextInt(20, 40));
        });
    }

    @Override
    public int getLingeringTime(SpellData spell, SpellRing spellRing) {
        return (int) (spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10);
    }
}
