package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IRequireItem;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

public class ModulePotion extends Module implements IRequireItem {
    public static final String POTION = "Potion";
    private int potionID;

    public ModulePotion(ItemStack stack) {
        super(stack);
        attributes.addAttribute(Attribute.POWER);
        attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Cause the targeted entity to gain the given potion effect, at a certain power and duration.";
    }

    @Override
    public String getDisplayName() {
        return "Potion";
    }

    @Override
    public void handle(ItemStack stack) {
        if (stack == null) return;
        if (stack.getItem() != Items.POTIONITEM) return;
        PotionEffect effect = PotionUtils.getEffectsFromStack(stack).get(0);
        if (effect.getPotion().isInstant()) return;
        potionID = Potion.getIdFromPotion(effect.getPotion());
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(POTION, potionID);
        compound.setInteger(POWER, (int) attributes.apply(Attribute.POWER, 1));
        compound.setInteger(DURATION, (int) attributes.apply(Attribute.DURATION, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
        int potionId = spell.getInteger(POTION);
        int power = spell.getInteger(POWER);
        int duration = spell.getInteger(DURATION);
        if (caster instanceof EntityLivingBase) {
            ((EntityLivingBase) caster).addPotionEffect(new PotionEffect(Potion.getPotionById(potionId), duration * 20, power - 1));
            return true;
        }
        return false;
    }
}
