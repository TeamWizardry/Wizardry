package com.teamwizardry.wizardry.common.item.staff;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.math.shapes.Arc3D;
import com.teamwizardry.librarianlib.math.shapes.Circle3D;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IColorable;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;

/**
 * Created by Saad on 6/7/2016.
 */
public class ItemGoldStaff extends Item implements IColorable {

    public ItemGoldStaff() {
        setRegistryName("gold_staff");
        setUnlocalizedName("gold_staff");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
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
        SpellCastEvent event = new SpellCastEvent(spell, entityLiving, (EntityPlayer) entityLiving);
        MinecraftForge.EVENT_BUS.post(event);
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
                    if (spell.hasKey(Module.CLASS)) {
                        Module module = ModuleList.INSTANCE.modules.get(spell.getString(Module.CLASS)).construct();
                        if (module instanceof IContinuousCast) {
                            module.cast((EntityPlayer) player, player, spell);
                        }
                    }
                }
            }
        }

        int betterCount = Math.abs(count - 72000);
        Circle3D circle = new Circle3D(player.getPositionVector(), player.width + 0.3, 5);
        for (Vec3d points : circle.getPoints()) {
            Vec3d target = new Vec3d(player.posX, player.posY + player.getEyeHeight() - 0.3, player.posZ);
            Arc3D arc = new Arc3D(points, target, (float) 0.9, 20);
            if (betterCount < arc.getPoints().size()) {
                Vec3d point = arc.getPoints().get(betterCount);
                SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(player.worldObj, point.xCoord, point.yCoord, point.zCoord, 0.1F, 0.5F, 20, true);
                fizz.setRandomizedSizes(true);
                fizz.blur();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation full = new ModelResourceLocation(getRegistryName() + "_pearl", "inventory");
        ModelResourceLocation empty = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, empty);
        ModelLoader.setCustomModelResourceLocation(this, 1, full);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        int max = 220, min = 120;
        if (isSelected) return;
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("red") && compound.hasKey("green") && compound.hasKey("blue")) {

                int red = compound.getInteger("red");
                int green = compound.getInteger("green");
                int blue = compound.getInteger("blue");
                boolean checkRed = compound.getBoolean("checkRed");
                boolean checkGreen = compound.getBoolean("checkGreen");
                boolean checkBlue = compound.getBoolean("checkBlue");

                if (checkRed && red < max) red++;
                else if (red > min) red--;
                else green++;

                if (checkGreen && green < max) green++;
                else if (green > min) green--;
                else green++;

                if (checkBlue && blue < max) blue++;
                else if (blue > min) blue--;
                else blue++;

                if (itemRand.nextInt(100) == 0) checkRed = !checkRed;
                if (itemRand.nextInt(100) == 0) checkGreen = !checkGreen;
                if (itemRand.nextInt(100) == 0) checkBlue = !checkBlue;

                compound.setInteger("red", red);
                compound.setInteger("green", green);
                compound.setInteger("blue", blue);
                compound.setBoolean("checkRed", checkRed);
                compound.setBoolean("checkBlue", checkGreen);
                compound.setBoolean("checkGreen", checkBlue);

            } else setDefaultColor(stack, min, max);
        } else setDefaultColor(stack, min, max);
    }

    @Override
    public boolean canItemEditBlocks() {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldS, ItemStack newS, boolean slotChanged) {
    	if (oldS == null || newS == null) return true;
    	if (!ItemStack.areItemsEqual(oldS, newS)) return true;
    	if (oldS.stackSize != newS.stackSize) return true;
        return slotChanged;
    }

    @Override
    public Color getColor(ItemStack stack) {
        int r = stack.getTagCompound().getInteger("red");
        int g = stack.getTagCompound().getInteger("green");
        int b = stack.getTagCompound().getInteger("blue");
        return new Color(r, g, b);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
    	if (!stack.hasTagCompound()) return;
    	NBTTagCompound compound = stack.getTagCompound();
    	if (!compound.hasKey("Spell")) return;
    	tooltip.add("Spell:");
    	addInformation(compound.getCompoundTag("Spell"), tooltip, 0);
    }
    
    private void addInformation(NBTTagCompound compound, List<String> tooltip, int level)
    {
    	if (!compound.hasKey(Module.CLASS)) return;
    	String cls = compound.getString(Module.CLASS);
    	cls = cls.substring(cls.lastIndexOf('.') + 1);
    	for (int i = 0; i < level; i++)
    		cls = ' ' + cls;
    	tooltip.add(cls);
    	if (!compound.hasKey(Module.MODULES)) return;
    	NBTTagList children = compound.getTagList(Module.MODULES, NBT.TAG_COMPOUND);
    	for (int i = 0; i < children.tagCount(); i++)
    		addInformation(children.getCompoundTagAt(i), tooltip, level + 1);
    }

    @SideOnly(Side.CLIENT)
    public static class ColorHandler implements IItemColor {
        public ColorHandler() {
        }

        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            if (stack.hasTagCompound()) {
                int r = stack.getTagCompound().getInteger("red");
                int g = stack.getTagCompound().getInteger("green");
                int b = stack.getTagCompound().getInteger("blue");
                return intColor(r, g, b);
            }
            return intColor(255, 255, 255);
        }
    }
}
