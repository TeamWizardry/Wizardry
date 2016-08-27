package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;

public class ModuleMelee extends Module {
    public ModuleMelee(ItemStack stack) {
        super(stack);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public String getDescription() {
        return "Casts the spell on the object you are looking at.";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    @Override
    public String getDisplayName() {
        return "Melee";
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
        double distance = 3;
        RayTraceResult raycast = RaycastUtils.raycast(caster, distance);
        if (raycast.typeOfHit == RayTraceResult.Type.BLOCK) {
            Entity entity = new SpellEntity(caster.worldObj, raycast.getBlockPos().getX(), raycast.getBlockPos().getY(), raycast.getBlockPos().getZ());
            stack.castEffects(entity);
            return true;
        } else if (raycast.typeOfHit == RayTraceResult.Type.ENTITY) {
            stack.castEffects(raycast.entityHit);
            return true;
        }
        return false;
    }
}
