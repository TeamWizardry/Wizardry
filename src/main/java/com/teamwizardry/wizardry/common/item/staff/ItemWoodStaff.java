package com.teamwizardry.wizardry.common.item.staff;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/7/2016.
 */
public class ItemWoodStaff extends ItemMod implements INacreColorable {
    
    public ItemWoodStaff() {
        super("wood_staff", "wood_staff_pearl", "wood_staff");
        setMaxStackSize(1);
    }
    
    private static int intColor(int r, int g, int b) {
        return (r * 65536 + g * 256 + b);
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (stack == null || world == null || entityLiving == null) return;
        if (!stack.hasTagCompound()) return;
        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("Spell")) return;
        NBTTagCompound spell = compound.getCompoundTag("Spell");
        Module module = ModuleRegistry.getInstance().getModuleByLocation(spell.getString(Module.SHAPE));
        if (!(module instanceof IContinuousCast)) {
            new SpellStack((EntityPlayer) entityLiving, entityLiving, spell).castSpell();
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote && Minecraft.getMinecraft().currentScreen != null) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        } else {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
    
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (count > 0 && count < (getMaxItemUseDuration(stack) - 20) && player instanceof EntityPlayer) {
            if (stack.hasTagCompound()) {
                NBTTagCompound compound = stack.getTagCompound();
                if (compound.hasKey("Spell")) {
                    NBTTagCompound spell = compound.getCompoundTag("Spell");
                    if (spell.hasKey(Module.SHAPE)) {
                        Module module = ModuleRegistry.getInstance().getModuleByLocation(spell.getString(Module.SHAPE));
                        if (module instanceof IContinuousCast) {
                            new SpellStack((EntityPlayer) player, player, spell).castSpell();
                        }
                    }
                }
            }
        }
        
        // TODO: PARTICLES
//        int betterCount = Math.abs(count - 72000);
//        Circle3D circle = new Circle3D(player.getPositionVector(), player.width + 0.3, 5);
//        for (Vec3d points : circle.getPoints()) {
//            Vec3d target = new Vec3d(player.posX, player.posY + player.getEyeHeight() - 0.3, player.posZ);
//            Arc3D arc = new Arc3D(points, target, (float) 0.9, 20);
//            if (betterCount < arc.getPoints().size()) {
//                Vec3d point = arc.getPoints().get(betterCount);
//                SparkleFX fizz = GlitterFactory.getInstance().createSparkle(player.worldObj, point, 10);
//                fizz.setFadeOut();
//                fizz.setAlpha(0.1f);
//                fizz.setScale(0.3f);
//                fizz.setBlurred();
//            }
//        }
    }
    
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote) return;
        
        colorableOnUpdate(stack);
    }
    
    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (!entityItem.worldObj.isRemote) return false;
        
        colorableOnEntityItemUpdate(entityItem);
        
        return super.onEntityItemUpdate(entityItem);
    }
}
