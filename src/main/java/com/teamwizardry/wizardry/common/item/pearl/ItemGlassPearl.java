package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.client.particle.SparkleFX;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Saad on 6/20/2016.
 */
public class ItemGlassPearl extends Item implements IExplodable {

    public List<Integer> potions = new ArrayList<>();

    public ItemGlassPearl() {
        setRegistryName("glass_pearl");
        setUnlocalizedName("glass_pearl");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
        addPotions();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {

        Wizardry.proxy.spawnParticleMagicBurst(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ);

        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
    }

    private void addPotions() {
        potions.add(1);
        potions.add(3);
        potions.add(5);
        potions.add(8);
        potions.add(11);
        potions.add(12);
        potions.add(21);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    }

    public void explode(Entity entityIn) {
        Random rand = new Random();
        int range = 5;
        List<EntityLivingBase> entitys = entityIn.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(entityIn.posX - range, entityIn.posY - range, entityIn.posZ - range, entityIn.posX + range, entityIn.posY + range, entityIn.posZ + range));
        for (EntityLivingBase e : entitys)
            e.addPotionEffect(new PotionEffect(Potion.getPotionById(potions.get(rand.nextInt(potions.size()))), rand.nextInt(30) * 20, rand.nextInt(2) + 1));

        for (int i = 0; i < 300; i++) {
            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(entityIn.worldObj, entityIn.posX, entityIn.posY + 0.5, entityIn.posZ, 1, 1F, 30, false);
            fizz.jitter(10, 0.1, 0.1, 0.1);
            fizz.randomDirection(0.3, 0.3, 0.3);
        }
    }

    @Override
    public boolean canItemEditBlocks() {
        return false;
    }
}
