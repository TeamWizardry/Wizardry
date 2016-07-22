package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ModuleFlame extends Module {
    public ModuleFlame() {
        attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Inflict fire damage every tick. Will smelt any block or item it touches.";
    }

    @Override
    public String getDisplayName() {
        return "Inflame";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(DURATION, (int) attributes.apply(Attribute.DURATION, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell) {
        if (caster instanceof EntityItem && !caster.worldObj.isRemote) {
            int duration = spell.getInteger(DURATION);
            EntityItem item = (EntityItem) caster;
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(item.getEntityItem());
            if (result != null) {
                if (duration >= item.getEntityItem().stackSize) {
                    EntityItem output = new EntityItem(item.worldObj, item.posX, item.posY + 0.5, item.posZ);
                    result.stackSize *= item.getEntityItem().stackSize;
                    output.setEntityItemStack(result);
                    output.worldObj.spawnEntityInWorld(output);
                    item.setDead();
                } else {
                    EntityItem output = new EntityItem(item.worldObj, item.posX, item.posY + 0.5, item.posZ);
                    result.stackSize *= duration;
                    item.getEntityItem().stackSize -= duration;
                    output.setEntityItemStack(result);
                    output.worldObj.spawnEntityInWorld(output);
                }
            }
        } else if (caster instanceof EntityLivingBase) {
            int duration = spell.getInteger(DURATION);
            caster.setFire(MathHelper.ceiling_double_int(duration / 20.));
        } else if (caster instanceof SpellEntity) {
            BlockPos pos = caster.getPosition();
            IBlockState state = caster.worldObj.getBlockState(pos);
            Block block = state.getBlock();
            ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
            if (result != null) {
                Block smelted = Block.getBlockFromItem(result.getItem());
                caster.worldObj.setBlockState(pos, smelted.getStateFromMeta(result.getMetadata()));
                caster.worldObj.playEvent(2001, pos, Block.getStateId(smelted.getDefaultState()));
            }
        }

        for (int i = 0; i < 5; i++) {
         /*   SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(caster.worldObj, caster.posX + 0.5, caster.posY + 0.5, caster.posZ + 0.5, 0.5F, 1F, 50, false);
            Color orange = Color.rgb(0xFF4500);
            fizz.setColor(Color.RED.r, Color.RED.g, Color.RED.b);
            //fizz.randomlyOscillateColor(true, false, false);
            fizz.lerp(orange);
            fizz.blur();
            fizz.setMotion(0, ThreadLocalRandom.current().nextDouble(0.01, 0.05), 0);
            fizz.randomizeSizes();
            fizz.randomDirection(0.1, 0, 0.1);
            fizz.jitter(1, 0.01, 0.01, 0.01);*/
            Wizardry.proxy.spawnParticleFire(caster.worldObj, new Vec3d(caster.posX + 0.5, caster.posY + 1, caster.posZ + 0.5), 50, 0.5);
        }
        return true;
    }
}