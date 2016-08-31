package com.teamwizardry.wizardry.common.spell.module.effects;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.capability.bloods.BloodRegistry;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IHasAffinity;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;

public class ModuleSaturation extends Module implements IHasAffinity {
    public ModuleSaturation(ItemStack stack) {
        super(stack);
        attributes.addAttribute(Attribute.POWER);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Give the targeted player hunger and saturation points.";
    }

    @Override
    public String getDisplayName() {
        return "Saturation";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(POWER, (int) attributes.apply(Attribute.POWER, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
        if (caster instanceof EntityPlayer) {
            int power = spell.getInteger(POWER);
            EntityPlayer target = (EntityPlayer) caster;
            //Vanilla code: addStats(power, mult) adds power to hunger and power*mult*2 to saturation
            target.getFoodStats().addStats(power, 0.5F);
            return true;
        }
        return false;
    }
    
    @Override
	public Map<IBloodType, Integer> getAffinityLevels()
	{
		Map<IBloodType, Integer> levels = new HashMap<>();
		levels.put(BloodRegistry.TERRABLOOD, 2);
		levels.put(BloodRegistry.AQUABLOOD, 1);
		return levels;
	}
}
